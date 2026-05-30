import { describe, it, expect } from 'vitest';
import {
  formatearFecha,
  formatearPrecio,
  calcularTotal,
  precioVisitante,
  esFechaValida,
  esDiaHabil,
  esFeriadoFijo,
  esCantidadValida,
  esVisitanteValido,
} from './compraUtils';
import type { Visitante } from '../types';

const REGULAR: Visitante = { nombre: 'Ana', edad: 25, tipoPase: 'REGULAR' };
const VIP: Visitante = { nombre: 'Luis', edad: 30, tipoPase: 'VIP' };

// ─── Ciclo 14.1 ──────────────────────────────────────────────────────────────
describe('formatearFecha', () => {
  it('formatea ISO a dd/mm/yyyy', () => {
    expect(formatearFecha('2026-06-15')).toBe('15/06/2026');
  });
});

// ─── Ciclo 14.2 ──────────────────────────────────────────────────────────────
describe('formatearPrecio', () => {
  it('formatea con signo pesos y separador de miles', () => {
    expect(formatearPrecio(5000)).toMatch(/\$5[.,]?000/);
  });
});

// ─── Ciclo 14.3 – 14.5 ───────────────────────────────────────────────────────
describe('calcularTotal', () => {
  it('1 entrada REGULAR → $10000', () => {
    expect(calcularTotal([REGULAR])).toBe(10000);
  });

  it('1 entrada VIP → $20000', () => {
    expect(calcularTotal([VIP])).toBe(20000);
  });

  it('mix 2 REGULAR + 1 VIP → $40000', () => {
    expect(calcularTotal([REGULAR, REGULAR, VIP])).toBe(40000);
  });
});

// ─── Ciclo 14.6 – 14.8 ───────────────────────────────────────────────────────
describe('esFechaValida', () => {
  // Helpers con formato local para evitar desfase UTC
  const formatLocal = (d: Date) => {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  };
  const hoy = new Date();

  it('rechaza fecha pasada', () => {
    expect(esFechaValida('2020-01-01')).toBe(false);
  });

  it('acepta hoy', () => {
    expect(esFechaValida(formatLocal(hoy))).toBe(true);
  });

  it('acepta fecha futura', () => {
    const semana = new Date(hoy);
    semana.setDate(hoy.getDate() + 7);
    expect(esFechaValida(formatLocal(semana))).toBe(true);
  });
});

// ─── Ciclo 14-bis: descuentos por edad ───────────────────────────────────────
describe('precioVisitante — descuentos por edad', () => {
  it('≤ 3 años → gratis', () => {
    expect(precioVisitante({ nombre: 'B', edad: 3, tipoPase: 'REGULAR' })).toBe(0);
  });

  it('≤ 15 años → 50% del precio base', () => {
    expect(precioVisitante({ nombre: 'N', edad: 15, tipoPase: 'REGULAR' })).toBe(5000);
  });

  it('≥ 60 años → 50% del precio base', () => {
    expect(precioVisitante({ nombre: 'J', edad: 60, tipoPase: 'REGULAR' })).toBe(5000);
  });

  it('adulto (16–59) → precio completo', () => {
    expect(precioVisitante({ nombre: 'A', edad: 30, tipoPase: 'REGULAR' })).toBe(10000);
  });

  it('descuento aplica también a VIP', () => {
    expect(precioVisitante({ nombre: 'N', edad: 10, tipoPase: 'VIP' })).toBe(10000);
  });
});

// ─── Ciclo 14-bis: días hábiles ──────────────────────────────────────────────
describe('esDiaHabil', () => {
  it('retorna false para un lunes (2026-06-01)', () => {
    expect(esDiaHabil('2026-06-01')).toBe(false);
  });

  it('retorna true para un martes (2026-06-02)', () => {
    expect(esDiaHabil('2026-06-02')).toBe(true);
  });
});

// ─── Ciclo 14-bis: feriados fijos ────────────────────────────────────────────
describe('esFeriadoFijo', () => {
  it('retorna true para el 25 de diciembre', () => {
    expect(esFeriadoFijo('2026-12-25')).toBe(true);
  });

  it('retorna true para el 1 de enero', () => {
    expect(esFeriadoFijo('2027-01-01')).toBe(true);
  });

  it('retorna false para un día común', () => {
    expect(esFeriadoFijo('2026-06-15')).toBe(false);
  });
});

// ─── Ciclo 14.12 – 14.14 ─────────────────────────────────────────────────────
describe('esVisitanteValido', () => {
  it('retorna true cuando tiene edad >= 0 (nombre opcional)', () => {
    expect(esVisitanteValido({ nombre: 'Ana', edad: 25, tipoPase: 'REGULAR' })).toBe(true);
  });

  it('retorna true cuando nombre está vacío (nombre es opcional)', () => {
    expect(esVisitanteValido({ nombre: '', edad: 25, tipoPase: 'REGULAR' })).toBe(true);
  });

  it('retorna true cuando edad es 0 (bebé menor de un año)', () => {
    expect(esVisitanteValido({ nombre: '', edad: 0, tipoPase: 'REGULAR' })).toBe(true);
  });
});

// ─── Ciclo 14.9 – 14.11 ──────────────────────────────────────────────────────
describe('esCantidadValida', () => {
  it('rechaza 0', () => {
    expect(esCantidadValida(0)).toBe(false);
  });

  it('rechaza 11', () => {
    expect(esCantidadValida(11)).toBe(false);
  });

  it('acepta el límite inferior (1)', () => {
    expect(esCantidadValida(1)).toBe(true);
  });

  it('acepta el límite superior (10)', () => {
    expect(esCantidadValida(10)).toBe(true);
  });
});
