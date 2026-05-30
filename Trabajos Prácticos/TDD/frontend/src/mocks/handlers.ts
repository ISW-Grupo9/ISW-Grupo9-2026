import { http, HttpResponse } from 'msw';
import type { CompraRequest, CompraResponse } from '../types';

export const handlers = [
  http.get('/api/parque/reglas', () => {
    return HttpResponse.json({
      diasCerrados: ['MONDAY'],
      feriadosFijos: ['12-25', '01-01'],
    });
  }),

  http.post<never, CompraRequest>('/api/compras', async ({ request }) => {
    const body = await request.json();

    if (body.formaPago === 'TARJETA') {
      return HttpResponse.json<CompraResponse>({
        id: '1',
        fechaVisita: body.fechaVisita,
        cantidadEntradas: body.visitantes.length,
        montoTotal: body.visitantes.length * 10000,
        formaPago: 'TARJETA',
        estado: 'PENDIENTE',
        urlPago: '/pago/simulado?compraId=1&monto=10000',
      });
    }

    return HttpResponse.json<CompraResponse>(
      {
        id: '1',
        fechaVisita: body.fechaVisita,
        cantidadEntradas: body.visitantes.length,
        montoTotal: body.visitantes.length * 10000,
        formaPago: 'EFECTIVO',
        estado: 'PENDIENTE_BOLETERIA',
      },
      { status: 201 },
    );
  }),

  http.get('/api/compras/:id', ({ params }) => {
    return HttpResponse.json<CompraResponse>({
      id: params.id as string,
      fechaVisita: '2026-06-15',
      cantidadEntradas: 2,
      montoTotal: 20000,
      formaPago: 'EFECTIVO',
      estado: 'PENDIENTE_BOLETERIA',
    });
  }),

  http.post('/api/compras/:id/confirmar', ({ params }) => {
    return HttpResponse.json<CompraResponse>({
      id: params.id as string,
      fechaVisita: '2026-06-15',
      cantidadEntradas: 1,
      montoTotal: 10000,
      formaPago: 'TARJETA',
      estado: 'CONFIRMADA',
    });
  }),
];
