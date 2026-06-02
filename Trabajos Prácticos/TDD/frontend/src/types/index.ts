export type TipoPase = 'REGULAR' | 'VIP';
export type FormaPago = 'EFECTIVO' | 'TARJETA';
export type EstadoCompra = 'PENDIENTE' | 'PENDIENTE_BOLETERIA' | 'CONFIRMADA';

export interface Visitante {
  nombre: string;
  edad: number | null;
  tipoPase: TipoPase;
}

export interface CompraRequest {
  usuarioId: string;
  fechaVisita: string;
  visitantes: Visitante[];
  formaPago: FormaPago | null;
}

export interface CompraResponse {
  id: string;
  fechaVisita: string;
  cantidadEntradas: number;
  montoTotal: number;
  formaPago: FormaPago;
  estado: EstadoCompra;
  urlPago?: string;
}

export interface CompraFormState {
  fechaVisita: string;
  visitantes: Visitante[];
  formaPago: FormaPago | null;
}

export interface CompraFormErrors {
  fechaVisita?: string;
  cantidad?: string;
  visitantes?: string[];
  nombresError?: (string | undefined)[];
  edadesError?: (string | undefined)[];
  formaPago?: string;
}
