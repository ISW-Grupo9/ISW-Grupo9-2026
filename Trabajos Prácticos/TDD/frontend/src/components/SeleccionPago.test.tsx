import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { SeleccionPago } from './SeleccionPago'

describe('SeleccionPago', () => {
  // ─── Ciclo 19.1 ───────────────────────────────────────────────────────
  it('llama a onPagoSeleccionado con EFECTIVO al hacer click', async () => {
    const onPago = vi.fn()
    render(<SeleccionPago selectedPago={null} onPagoSeleccionado={onPago} />)
    await userEvent.click(screen.getByText('Efectivo'))
    expect(onPago).toHaveBeenCalledWith('EFECTIVO')
  })

  // ─── Ciclo 19.4 ───────────────────────────────────────────────────────
  it('llama a onPagoSeleccionado con TARJETA al hacer click', async () => {
    const onPago = vi.fn()
    render(<SeleccionPago selectedPago={null} onPagoSeleccionado={onPago} />)
    await userEvent.click(screen.getByText('Tarjeta'))
    expect(onPago).toHaveBeenCalledWith('TARJETA')
  })

  // ─── Ciclo 19.5 ───────────────────────────────────────────────────────
  it('muestra error si se pasa prop error', () => {
    render(<SeleccionPago selectedPago={null} onPagoSeleccionado={() => {}} error="Seleccioná una forma de pago" />)
    expect(screen.getByRole('alert')).toHaveTextContent('Seleccioná una forma de pago')
  })

  // ─── Ciclo 19.6 ───────────────────────────────────────────────────────
  it('marca como activo el botón de la opción seleccionada', () => {
    render(<SeleccionPago selectedPago="EFECTIVO" onPagoSeleccionado={() => {}} />)
    expect(screen.getByText('Efectivo')).toHaveClass('activo')
  })
})
