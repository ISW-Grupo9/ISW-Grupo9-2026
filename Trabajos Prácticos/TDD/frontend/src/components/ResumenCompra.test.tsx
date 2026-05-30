import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { ResumenCompra } from './ResumenCompra';
import type { Visitante } from '../types';

const regular = (n = 1): Visitante[] =>
  Array.from({ length: n }, (_, i) => ({
    nombre: `R${i}`,
    edad: 20,
    tipoPase: 'REGULAR' as const,
  }));
const vip = (n = 1): Visitante[] =>
  Array.from({ length: n }, (_, i) => ({ nombre: `V${i}`, edad: 30, tipoPase: 'VIP' as const }));

describe('ResumenCompra', () => {
  // ─── Ciclo 18.1 ───────────────────────────────────────────────────────
  it('muestra la cantidad de entradas', () => {
    render(
      <ResumenCompra
        fechaVisita="2026-06-15"
        visitantes={[...regular(), ...regular(), ...vip()]}
      />,
    );
    expect(screen.getByTestId('cantidad')).toHaveTextContent('3 entradas');
  });

  // ─── Ciclo 18.2 ───────────────────────────────────────────────────────
  it('muestra la fecha formateada como dd/mm/yyyy', () => {
    render(<ResumenCompra fechaVisita="2026-06-15" visitantes={regular()} />);
    expect(screen.getByTestId('fecha')).toHaveTextContent('15/06/2026');
  });

  // ─── Ciclo 18.3 ───────────────────────────────────────────────────────
  it('muestra el monto total', () => {
    render(<ResumenCompra fechaVisita="2026-06-15" visitantes={[...regular(2), ...vip()]} />);
    // 2 REGULAR ($10000 c/u) + 1 VIP ($20000) = $40000
    expect(screen.getByTestId('total').textContent).toMatch(/40[.,]?000/);
  });

  // ─── Ciclo 18.4 ───────────────────────────────────────────────────────
  it('muestra el desglose por tipo de pase', () => {
    render(<ResumenCompra fechaVisita="2026-06-15" visitantes={[...regular(2), ...vip()]} />);
    expect(screen.getByTestId('desglose')).toHaveTextContent('2 regular + 1 VIP');
  });
});
