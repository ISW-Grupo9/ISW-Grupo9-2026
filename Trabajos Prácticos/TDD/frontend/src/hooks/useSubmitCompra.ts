import { useState, useCallback } from 'react'
import type { CompraRequest, CompraResponse } from '../types'

interface SubmitState {
  isLoading: boolean
  isSuccess: boolean
  data: CompraResponse | null
  error: string | null
}

export function useSubmitCompra(
  navigate: (path: string) => void,
  redirect: (url: string) => void = (url) => { window.location.href = url }
) {
  const [state, setState] = useState<SubmitState>({
    isLoading: false,
    isSuccess: false,
    data: null,
    error: null,
  })

  const submit = useCallback(async (request: CompraRequest) => {
    setState({ isLoading: true, isSuccess: false, data: null, error: null })

    try {
      const res = await fetch('/api/compras', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request),
      })

      if (res.status === 401) {
        navigate('/login')
        setState(prev => ({ ...prev, isLoading: false }))
        return
      }

      if (!res.ok) {
        const body = await res.json().catch(() => ({}))
        setState({ isLoading: false, isSuccess: false, data: null, error: body.error ?? 'Error al procesar la compra' })
        return
      }

      const data: CompraResponse = await res.json()
      setState({ isLoading: false, isSuccess: true, data, error: null })

      if (data.urlPago) {
        if (data.urlPago.startsWith('/')) {
          navigate(data.urlPago)
        } else {
          redirect(data.urlPago)
        }
      }
    } catch {
      setState({ isLoading: false, isSuccess: false, data: null, error: 'Error de conexión' })
    }
  }, [navigate])

  return { ...state, submit }
}
