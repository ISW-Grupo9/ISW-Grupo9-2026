import { describe, it, expect } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { http, HttpResponse } from 'msw';
import { server } from '../mocks/server';
import { MercadoPagoPage } from './MercadoPagoPage';

function renderConRuta(url = '/pago/simulado?compraId=1&monto=10000') {
  return render(
    <MemoryRouter initialEntries={[url]}>
      <Routes>
        <Route path="/pago/simulado" element={<MercadoPagoPage />} />
      </Routes>
    </MemoryRouter>,
  );
}

describe('MercadoPagoPage', () => {
  // ─── Ciclo 22.1 ───────────────────────────────────────────────────────────
  it('muestra el monto de la transacción formateado', () => {
    renderConRuta('/pago/simulado?compraId=1&monto=10000');
    expect(screen.getByTestId('monto').textContent).toMatch(/10[.,]?000/);
  });

  // ─── Ciclo 22.2 ───────────────────────────────────────────────────────────
  it('muestra el botón Pagar', () => {
    renderConRuta();
    expect(screen.getByRole('button', { name: /pagar/i })).toBeTruthy();
  });

  // ─── Ciclo 22.3 ───────────────────────────────────────────────────────────
  it('muestra estado de carga al hacer click en Pagar', async () => {
    server.use(
      http.post('/api/compras/:id/confirmar', async () => {
        await new Promise((r) => setTimeout(r, 50));
        return HttpResponse.json({ estado: 'CONFIRMADA' });
      }),
    );
    renderConRuta();
    await userEvent.click(screen.getByRole('button', { name: /pagar/i }));
    await waitFor(() => expect(screen.getByRole('button', { name: /procesando/i })).toBeTruthy());
  });

  // ─── Ciclo 22.4 ───────────────────────────────────────────────────────────
  it('muestra ¡Pago exitoso! después de confirmar', async () => {
    renderConRuta();
    await userEvent.click(screen.getByRole('button', { name: /pagar/i }));
    await waitFor(() => expect(screen.getByTestId('pago-exitoso')).toBeTruthy());
    expect(screen.getByTestId('pago-exitoso')).toHaveTextContent('¡Pago exitoso!');
  });

  // ─── Ciclo 22.5 ───────────────────────────────────────────────────────────
  it('muestra error si la confirmación falla', async () => {
    server.use(
      http.post('/api/compras/:id/confirmar', () =>
        HttpResponse.json({ error: 'Error al confirmar' }, { status: 500 }),
      ),
    );
    renderConRuta();
    await userEvent.click(screen.getByRole('button', { name: /pagar/i }));
    await waitFor(() => expect(screen.getByRole('alert')).toBeTruthy());
  });
});
