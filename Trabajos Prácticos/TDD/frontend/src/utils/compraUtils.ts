import type { TipoPase, Visitante } from '../types';

// ASUNCION PO — pendiente confirmacion (ver decisiones-de-diseno.md)
export const PRECIO_REGULAR = 10000;
export const PRECIO_VIP = 20000;
export const MAX_ENTRADAS = 10;
export const MAX_EDAD = 120;

// ASUNCION PO — parque cerrado los lunes (dayOfWeek: 1 = lunes en JS)
export const DIAS_CERRADOS = [1];

export function getPrecio(tipoPase: TipoPase): number {
  return tipoPase === 'VIP' ? PRECIO_VIP : PRECIO_REGULAR;
}

export function precioVisitante(visitante: Visitante): number {
  if (visitante.edad === null) return 0;
  const base = getPrecio(visitante.tipoPase);
  if (visitante.edad <= 3) return 0;
  if (visitante.edad <= 15 || visitante.edad >= 60) return Math.round(base / 2);
  return base;
}

export function calcularTotal(visitantes: Visitante[]): number {
  return visitantes.reduce((acc, v) => acc + precioVisitante(v), 0);
}

export function formatearPrecio(valor: number): string {
  return `$${valor.toLocaleString('es-AR')}`;
}

export function formatearFecha(iso: string): string {
  const [year, month, day] = iso.split('-');
  return `${day}/${month}/${year}`;
}

export function esFechaValida(iso: string): boolean {
  if (!iso) return false;
  const fecha = new Date(iso + 'T00:00:00');
  const hoy = new Date();
  hoy.setHours(0, 0, 0, 0);
  return fecha >= hoy;
}

export function esDiaHabil(iso: string): boolean {
  const fecha = new Date(iso + 'T00:00:00');
  return !DIAS_CERRADOS.includes(fecha.getDay());
}

export function esCantidadValida(cantidad: number): boolean {
  return cantidad >= 1 && cantidad <= MAX_ENTRADAS;
}

export function esVisitanteValido(v: Visitante): boolean {
  return v.edad !== null && v.edad >= 0 && v.edad <= MAX_EDAD;
}

export function esNombreValido(nombre: string): boolean {
  if (!nombre || nombre.trim() === '') return true;
  return /^[a-zA-ZÀ-ÿ\s\-']+$/.test(nombre);
}

// Feriados fijos confirmados por el PO: 25/12 y 01/01
const FERIADOS_FIJOS_LOCAL: [number, number][] = [
  [12, 25],
  [1, 1],
];

export function esFeriadoFijo(iso: string): boolean {
  const fecha = new Date(iso + 'T00:00:00');
  return FERIADOS_FIJOS_LOCAL.some(
    ([month, day]) => fecha.getMonth() + 1 === month && fecha.getDate() === day,
  );
}
