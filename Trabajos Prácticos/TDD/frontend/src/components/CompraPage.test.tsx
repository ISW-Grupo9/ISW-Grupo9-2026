import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { http, HttpResponse } from 'msw';
import { server } from '../mocks/server';
import { CompraPage } from './CompraPage';

const FECHA_FUTURA = '2026-06-16'; // martes

async function completarFormulario() {
  // Fecha — fireEvent.change reemplaza el valor directamente (evita conflicto con default de hoy)
  const inputFecha = document.querySelector('input[type="date"]') as HTMLInputElement;
  fireEvent.change(inputFecha, { target: { value: FECHA_FUTURA } });

  // Cantidad
  await userEvent.selectOptions(screen.getByLabelText('Cantidad de entradas'), '1');

  // Nombre del visitante
  await userEvent.type(screen.getByLabelText('Nombre visitante 1'), 'Ana');

  // Edad del visitante
  const inputEdad = screen.getByLabelText('Edad visitante 1');
  await userEvent.clear(inputEdad);
  await userEvent.type(inputEdad, '25');
}

describe('CompraPage — flujo completo', () => {
  // ─── Ciclo 21.1 ───────────────────────────────────────────────────────
  it('compra exitosa con efectivo muestra la confirmación', async () => {
    server.use(
      http.post('/api/compras', () =>
        HttpResponse.json(
          {
            id: '1',
            fechaVisita: FECHA_FUTURA,
            cantidadEntradas: 1,
            montoTotal: 5000,
            formaPago: 'EFECTIVO',
            estado: 'PENDIENTE_BOLETERIA',
          },
          { status: 201 },
        ),
      ),
    );
    render(<CompraPage usuarioId="1" />);
    await completarFormulario();
    await userEvent.click(screen.getByText('Efectivo'));
    await userEvent.click(screen.getByText('Confirmar compra'));

    await waitFor(() => expect(screen.getByTestId('confirmacion')).toBeTruthy());
    expect(screen.getByTestId('confirmacion')).toHaveTextContent('¡Compra confirmada!');
  });

  // ─── Ciclo 21.2 ───────────────────────────────────────────────────────
  it('compra exitosa con tarjeta llama a redirect con URL de MP', async () => {
    const redirect = vi.fn();
    server.use(
      http.post('/api/compras', () =>
        HttpResponse.json({
          id: '1',
          fechaVisita: FECHA_FUTURA,
          cantidadEntradas: 1,
          montoTotal: 5000,
          formaPago: 'TARJETA',
          estado: 'PENDIENTE',
          urlPago: 'https://mercadopago.com/mock',
        }),
      ),
    );
    render(<CompraPage usuarioId="1" redirect={redirect} />);
    await completarFormulario();
    await userEvent.click(screen.getByText('Tarjeta'));
    await userEvent.click(screen.getByText('Confirmar compra'));

    await waitFor(() => expect(redirect).toHaveBeenCalledWith('https://mercadopago.com/mock'));
  });

  // ─── Ciclo 21.3 ───────────────────────────────────────────────────────
  it('error de fecha cerrada muestra el error sin avanzar', async () => {
    server.use(
      http.post('/api/compras', () =>
        HttpResponse.json({ error: 'El parque está cerrado ese día' }, { status: 400 }),
      ),
    );
    render(<CompraPage usuarioId="1" />);
    await completarFormulario();
    await userEvent.click(screen.getByText('Efectivo'));
    await userEvent.click(screen.getByText('Confirmar compra'));

    await waitFor(() => expect(screen.getByTestId('error-global')).toBeTruthy());
    expect(screen.queryByTestId('confirmacion')).toBeNull();
  });

  // ─── Ciclo 21.4 ───────────────────────────────────────────────────────
  it('submit sin forma de pago muestra error y no envía la request', async () => {
    const fetchSpy = vi.fn();
    server.use(
      http.post('/api/compras', () => {
        fetchSpy();
        return HttpResponse.json({});
      }),
    );

    render(<CompraPage usuarioId="1" />);
    await completarFormulario();
    // NO seleccionamos forma de pago
    await userEvent.click(screen.getByText('Confirmar compra'));

    expect(screen.getByRole('alert')).toHaveTextContent('Seleccioná una forma de pago');
    expect(fetchSpy).not.toHaveBeenCalled();
  });

  // ─── Ciclo 21.10 ──────────────────────────────────────────────────────
  it('submit con fecha en el pasado no envía la request', async () => {
    const fetchSpy = vi.fn();
    server.use(
      http.post('/api/compras', () => {
        fetchSpy();
        return HttpResponse.json({});
      }),
    );

    render(<CompraPage usuarioId="1" />);

    // Fecha en el pasado
    const inputFecha = document.querySelector('input[type="date"]') as HTMLInputElement;
    fireEvent.change(inputFecha, { target: { value: '2020-01-01' } });

    await userEvent.selectOptions(screen.getByLabelText('Cantidad de entradas'), '1');
    await userEvent.type(screen.getByLabelText('Nombre visitante 1'), 'Ana');
    await userEvent.click(screen.getByText('Efectivo'));
    await userEvent.click(screen.getByText('Confirmar compra'));

    expect(fetchSpy).not.toHaveBeenCalled();
  });

  // ─── Ciclo 21.8 ───────────────────────────────────────────────────────
  it('submit sin visitantes muestra error de visitantes', async () => {
    render(<CompraPage usuarioId="1" />);
    await userEvent.click(screen.getByText('Confirmar compra'));
    const alerts = screen.getAllByRole('alert');
    expect(alerts.some((a) => /Seleccioná visitantes/i.test(a.textContent ?? ''))).toBe(true);
  });

  // ─── Ciclo 21.5 ───────────────────────────────────────────────────────
  it('confirmación con efectivo muestra botón para volver al inicio', async () => {
    server.use(
      http.post('/api/compras', () =>
        HttpResponse.json(
          {
            id: '1',
            fechaVisita: FECHA_FUTURA,
            cantidadEntradas: 1,
            montoTotal: 5000,
            formaPago: 'EFECTIVO',
            estado: 'PENDIENTE_BOLETERIA',
          },
          { status: 201 },
        ),
      ),
    );
    render(<CompraPage usuarioId="1" />);
    await completarFormulario();
    await userEvent.click(screen.getByText('Efectivo'));
    await userEvent.click(screen.getByText('Confirmar compra'));

    await waitFor(() => expect(screen.getByTestId('confirmacion')).toBeTruthy());
    expect(screen.getByRole('link', { name: /volver/i })).toBeTruthy();
  });

  // ─── Ciclo 21.6 ───────────────────────────────────────────────────────
  it('error 401 redirige a /login', async () => {
    const navigate = vi.fn();
    server.use(http.post('/api/compras', () => new HttpResponse(null, { status: 401 })));
    render(<CompraPage usuarioId="1" navigate={navigate} />);
    await completarFormulario();
    await userEvent.click(screen.getByText('Efectivo'));
    await userEvent.click(screen.getByText('Confirmar compra'));

    await waitFor(() => expect(navigate).toHaveBeenCalledWith('/login'));
  });
});
