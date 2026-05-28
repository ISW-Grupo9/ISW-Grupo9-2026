import { describe, it, expect, vi, beforeEach } from 'vitest'
import { renderHook, act, waitFor } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { server } from '../mocks/server'
import { useSubmitCompra } from './useSubmitCompra'
import type { CompraRequest, CompraResponse } from '../types'

const REQUEST: CompraRequest = {
  usuarioId: '1',
  fechaVisita: '2026-06-15',
  visitantes: [{ nombre: 'Ana', edad: 25, tipoPase: 'REGULAR' }],
  formaPago: 'EFECTIVO',
}

const RESPONSE_EFECTIVO: CompraResponse = {
  id: '1',
  fechaVisita: '2026-06-15',
  cantidadEntradas: 1,
  montoTotal: 5000,
  formaPago: 'EFECTIVO',
  estado: 'PENDIENTE_BOLETERIA',
}

const RESPONSE_TARJETA: CompraResponse = {
  ...RESPONSE_EFECTIVO,
  formaPago: 'TARJETA',
  estado: 'PENDIENTE',
  urlPago: '/pago/simulado?compraId=1&monto=10000',
}

describe('useSubmitCompra', () => {
  const navigate = vi.fn()

  beforeEach(() => {
    navigate.mockClear()
  })

  // ─── Ciclo 20.1 ───────────────────────────────────────────────────────
  it('envía los datos correctos a POST /api/compras', async () => {
    let capturedBody: unknown
    server.use(
      http.post('/api/compras', async ({ request }) => {
        capturedBody = await request.json()
        return HttpResponse.json(RESPONSE_EFECTIVO, { status: 201 })
      })
    )
    const { result } = renderHook(() => useSubmitCompra(navigate))
    await act(async () => result.current.submit(REQUEST))
    expect(capturedBody).toMatchObject({ fechaVisita: '2026-06-15' })
  })

  // ─── Ciclo 20.2 ───────────────────────────────────────────────────────
  it('isLoading es true mientras se realiza el fetch', async () => {
    server.use(
      http.post('/api/compras', async () => {
        await new Promise(r => setTimeout(r, 50))
        return HttpResponse.json(RESPONSE_EFECTIVO, { status: 201 })
      })
    )
    const { result } = renderHook(() => useSubmitCompra(navigate))
    act(() => { result.current.submit(REQUEST) })
    expect(result.current.isLoading).toBe(true)
    await waitFor(() => expect(result.current.isLoading).toBe(false))
  })

  // ─── Ciclo 20.3 ───────────────────────────────────────────────────────
  it('pago efectivo → isSuccess true con datos de la compra', async () => {
    const { result } = renderHook(() => useSubmitCompra(navigate))
    await act(async () => result.current.submit(REQUEST))
    expect(result.current.isSuccess).toBe(true)
    expect(result.current.data?.estado).toBe('PENDIENTE_BOLETERIA')
  })

  // ─── Ciclo 20.5 ───────────────────────────────────────────────────────
  // urlPago interna (/pago/simulado) → usa navigate (React Router), no redirect externo
  it('pago tarjeta → navega a la ruta interna del MP simulado', async () => {
    server.use(
      http.post('/api/compras', () => HttpResponse.json(RESPONSE_TARJETA))
    )
    const { result } = renderHook(() => useSubmitCompra(navigate))
    await act(async () => result.current.submit({ ...REQUEST, formaPago: 'TARJETA' }))
    expect(navigate).toHaveBeenCalledWith('/pago/simulado?compraId=1&monto=10000')
  })

  // ─── Ciclo 20.6 ───────────────────────────────────────────────────────
  it('error 400 → muestra mensaje de error', async () => {
    server.use(
      http.post('/api/compras', () =>
        HttpResponse.json({ error: 'Fecha inválida' }, { status: 400 })
      )
    )
    const { result } = renderHook(() => useSubmitCompra(navigate))
    await act(async () => { await result.current.submit(REQUEST) })
    await waitFor(() => expect(result.current.isLoading).toBe(false))
    expect(result.current.error).toBe('Fecha inválida')
  })

  // ─── Ciclo 20.7 ───────────────────────────────────────────────────────
  it('error 401 → redirige a /login', async () => {
    server.use(
      http.post('/api/compras', () => new HttpResponse(null, { status: 401 }))
    )
    const { result } = renderHook(() => useSubmitCompra(navigate))
    await act(async () => { await result.current.submit(REQUEST) })
    await waitFor(() => expect(result.current.isLoading).toBe(false))
    expect(navigate).toHaveBeenCalledWith('/login')
  })

  // ─── Ciclo 20.8 ───────────────────────────────────────────────────────
  it('error de red → muestra error genérico', async () => {
    server.use(
      http.post('/api/compras', () => HttpResponse.error())
    )
    const { result } = renderHook(() => useSubmitCompra(navigate))
    await act(async () => result.current.submit(REQUEST))
    expect(result.current.error).toBe('Error de conexión')
  })
})
