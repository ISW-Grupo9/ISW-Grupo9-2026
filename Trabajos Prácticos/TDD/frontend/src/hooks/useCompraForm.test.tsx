import { describe, it, expect } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useCompraForm } from './useCompraForm';

const HOY_ISO = (() => {
  const d = new Date();
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
})();

describe('useCompraForm', () => {
  // ─── Ciclo 15.1 ─────────────────────────────────────────────────────────
  it('inicializa con la fecha de hoy y sin visitantes ni pago', () => {
    const { result } = renderHook(() => useCompraForm());
    expect(result.current.fechaVisita).toBe(HOY_ISO);
    expect(result.current.visitantes).toHaveLength(0);
    expect(result.current.formaPago).toBeNull();
  });

  // ─── Ciclo 15.2 ─────────────────────────────────────────────────────────
  it('actualiza la fecha correctamente', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setFechaVisita(HOY_ISO));
    expect(result.current.fechaVisita).toBe(HOY_ISO);
  });

  // ─── Ciclo 15.3 ─────────────────────────────────────────────────────────
  it('agrega visitantes al aumentar la cantidad', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setCantidad(3));
    expect(result.current.visitantes).toHaveLength(3);
  });

  // ─── Ciclo 15.4 ─────────────────────────────────────────────────────────
  it('remueve visitantes al disminuir la cantidad', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setCantidad(3));
    act(() => result.current.setCantidad(2));
    expect(result.current.visitantes).toHaveLength(2);
  });

  // ─── Ciclo 15.5 ─────────────────────────────────────────────────────────
  it('actualiza la edad de un visitante por índice', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setCantidad(2));
    act(() => result.current.setEdadVisitante(0, 25));
    expect(result.current.visitantes[0].edad).toBe(25);
  });

  // ─── Ciclo 15.6 ─────────────────────────────────────────────────────────
  it('actualiza el tipo de pase de un visitante por índice', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setCantidad(1));
    act(() => result.current.setTipoPaseVisitante(0, 'VIP'));
    expect(result.current.visitantes[0].tipoPase).toBe('VIP');
  });

  // ─── Ciclo 15.7 ─────────────────────────────────────────────────────────
  it('muestra error si la fecha es pasada', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setFechaVisita('2020-01-01'));
    expect(result.current.errors.fechaVisita).toBeDefined();
  });

  // ─── Ciclo 15.9 ─────────────────────────────────────────────────────────
  it('muestra error si la cantidad supera 10', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setCantidad(11));
    expect(result.current.errors.cantidad).toBeDefined();
  });

  // ─── Ciclo 15.10 ────────────────────────────────────────────────────────
  it('limpia el error de cantidad al corregirlo', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setCantidad(11));
    act(() => result.current.setCantidad(5));
    expect(result.current.errors.cantidad).toBeUndefined();
  });

  // ─── Ciclo 15.13 ────────────────────────────────────────────────────────
  it('isValid es true aunque el visitante no tenga nombre (campo opcional)', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setFechaVisita(HOY_ISO));
    act(() => result.current.setCantidad(1));
    // nombre queda vacío — no debe bloquear
    act(() => result.current.setFormaPago('EFECTIVO'));
    expect(result.current.isValid).toBe(true);
  });

  // ─── Ciclo 15.14 ────────────────────────────────────────────────────────
  it('isValid es true cuando visitante tiene edad 0 (bebé sin nombre)', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setFechaVisita(HOY_ISO));
    act(() => result.current.setCantidad(1));
    // edad 0, nombre vacío → ambos válidos
    act(() => result.current.setFormaPago('EFECTIVO'));
    expect(result.current.isValid).toBe(true);
  });

  // ─── Ciclo 15.12 ────────────────────────────────────────────────────────
  it('actualiza el nombre de un visitante por índice', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setCantidad(1));
    act(() => result.current.setNombreVisitante(0, 'Ana'));
    expect(result.current.visitantes[0].nombre).toBe('Ana');
  });

  // ─── Ciclo 15.11 ────────────────────────────────────────────────────────
  it('isValid es true cuando todos los datos son correctos', () => {
    const { result } = renderHook(() => useCompraForm());
    act(() => result.current.setFechaVisita(HOY_ISO));
    act(() => result.current.setCantidad(1));
    act(() => result.current.setNombreVisitante(0, 'Ana'));
    act(() => result.current.setFormaPago('EFECTIVO'));
    expect(result.current.isValid).toBe(true);
  });
});
