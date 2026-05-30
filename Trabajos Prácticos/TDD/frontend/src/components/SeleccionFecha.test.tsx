import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { SeleccionFecha } from './SeleccionFecha';

describe('SeleccionFecha', () => {
  // ─── Ciclo 16.1 ───────────────────────────────────────────────────────
  it('renderiza un input de tipo date', () => {
    render(<SeleccionFecha value="" onChange={() => {}} />);
    expect(document.querySelector('input[type="date"]')).not.toBeNull();
  });

  // ─── Ciclo 16.2 ───────────────────────────────────────────────────────
  it('llama a onChange con la fecha seleccionada', async () => {
    const onChange = vi.fn();
    render(<SeleccionFecha value="" onChange={onChange} />);
    const input = document.querySelector('input[type="date"]') as HTMLInputElement;
    await userEvent.type(input, '2026-06-15');
    expect(onChange).toHaveBeenCalled();
  });

  // ─── Ciclo 16.3 ───────────────────────────────────────────────────────
  it('muestra el mensaje de error si se pasa prop error', () => {
    render(<SeleccionFecha value="" onChange={() => {}} error="Fecha inválida" />);
    expect(screen.getByRole('alert')).toHaveTextContent('Fecha inválida');
  });

  // ─── Ciclo 16.4 ───────────────────────────────────────────────────────
  it('no muestra error si no hay prop error', () => {
    render(<SeleccionFecha value="" onChange={() => {}} />);
    expect(screen.queryByRole('alert')).toBeNull();
  });

  // ─── Ciclo 16.5 ───────────────────────────────────────────────────────
  it('deshabilita fechas pasadas con el atributo min', () => {
    render(<SeleccionFecha value="" onChange={() => {}} />);
    const input = document.querySelector('input[type="date"]') as HTMLInputElement;
    const hoy = new Date().toISOString().split('T')[0];
    expect(input.min).toBe(hoy);
  });

  // ─── Ciclo 16.6 ───────────────────────────────────────────────────────
  it('acepta props diasCerrados y feriadosFijos sin errores', () => {
    expect(() =>
      render(
        <SeleccionFecha
          value=""
          onChange={() => {}}
          diasCerrados={['MONDAY']}
          feriadosFijos={['12-25', '01-01']}
        />,
      ),
    ).not.toThrow();
  });
});
