import type { Visitante } from '../types';
import { formatearFecha, formatearPrecio, calcularTotal } from '../utils/compraUtils';

interface Props {
  fechaVisita: string;
  visitantes: Visitante[];
}

export function ResumenCompra({ fechaVisita, visitantes }: Props) {
  const total = calcularTotal(visitantes);
  const regulares = visitantes.filter((v) => v.tipoPase === 'REGULAR').length;
  const vips = visitantes.filter((v) => v.tipoPase === 'VIP').length;

  return (
    <div className="space-y-3">
      <div className="flex justify-between items-center">
        <span className="text-white/50 text-xs uppercase tracking-widest">Entradas</span>
        <span data-testid="cantidad" className="text-white text-sm font-medium">
          {visitantes.length} entradas
        </span>
      </div>
      <div className="flex justify-between items-center">
        <span className="text-white/50 text-xs uppercase tracking-widest">Fecha</span>
        <span data-testid="fecha" className="text-white text-sm font-medium">
          {fechaVisita ? formatearFecha(fechaVisita) : '—'}
        </span>
      </div>
      <div className="flex justify-between items-center">
        <span className="text-white/50 text-xs uppercase tracking-widest">Composición</span>
        <span data-testid="desglose" className="text-white text-sm font-medium">
          {regulares} regular + {vips} VIP
        </span>
      </div>
      <div className="border-t border-white/15 pt-4 flex justify-between items-end">
        <span className="text-white/50 text-xs uppercase tracking-widest">Total</span>
        <span data-testid="total" className="font-display text-3xl text-white leading-none">
          {formatearPrecio(total)}
        </span>
      </div>
    </div>
  );
}
