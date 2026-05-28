import { useState, useCallback } from 'react'

interface PagoMPState {
  isLoading: boolean
  isSuccess: boolean
  error: string | null
}

export function usePagoMP() {
  const [state, setState] = useState<PagoMPState>({
    isLoading: false,
    isSuccess: false,
    error: null,
  })

  const confirmar = useCallback(async (compraId: string) => {
    setState({ isLoading: true, isSuccess: false, error: null })

    try {
      const res = await fetch(`/api/compras/${compraId}/confirmar`, { method: 'POST' })

      if (!res.ok) {
        setState({ isLoading: false, isSuccess: false, error: 'Error al procesar el pago' })
        return
      }

      setState({ isLoading: false, isSuccess: true, error: null })
    } catch {
      setState({ isLoading: false, isSuccess: false, error: 'Error de conexión' })
    }
  }, [])

  return { ...state, confirmar }
}
