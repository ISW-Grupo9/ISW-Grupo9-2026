import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { SeleccionEntradas } from './SeleccionEntradas'
import type { Visitante } from '../types'

const visitantes = (n: number): Visitante[] =>
  Array.from({ length: n }, (_, i) => ({ nombre: `V${i}`, edad: 20, tipoPase: 'REGULAR' }))

describe('SeleccionEntradas', () => {
  // ─── Ciclo 17.1 ───────────────────────────────────────────────────────
  it('renderiza el selector de cantidad', () => {
    render(<SeleccionEntradas cantidad={0} visitantes={[]} onCantidadChange={() => {}} onVisitanteChange={() => {}} />)
    expect(screen.getByLabelText('Cantidad de entradas')).toBeTruthy()
  })

  // ─── Ciclo 17.2 ───────────────────────────────────────────────────────
  it('renderiza N filas de visitante según la cantidad', () => {
    render(<SeleccionEntradas cantidad={3} visitantes={visitantes(3)} onCantidadChange={() => {}} onVisitanteChange={() => {}} />)
    expect(screen.getAllByTestId(/^visitante-/)).toHaveLength(3)
  })

  // ─── Ciclo 17.3 ───────────────────────────────────────────────────────
  it('muestra error si se pasa prop error', () => {
    render(<SeleccionEntradas cantidad={0} visitantes={[]} onCantidadChange={() => {}} onVisitanteChange={() => {}} error="Máximo 10" />)
    expect(screen.getByRole('alert')).toHaveTextContent('Máximo 10')
  })

  // ─── Ciclo 17.6 ───────────────────────────────────────────────────────
  it('llama a onCantidadChange al cambiar la cantidad', async () => {
    const onChange = vi.fn()
    render(<SeleccionEntradas cantidad={0} visitantes={[]} onCantidadChange={onChange} onVisitanteChange={() => {}} />)
    await userEvent.selectOptions(screen.getByLabelText('Cantidad de entradas'), '3')
    expect(onChange).toHaveBeenCalledWith(3)
  })

  // ─── Ciclo 17.7 ───────────────────────────────────────────────────────
  it('llama a onVisitanteChange al editar la edad', async () => {
    const onChange = vi.fn()
    render(<SeleccionEntradas cantidad={1} visitantes={visitantes(1)} onCantidadChange={() => {}} onVisitanteChange={onChange} />)
    const edadInput = screen.getByLabelText('Edad visitante 1')
    await userEvent.clear(edadInput)
    await userEvent.type(edadInput, '25')
    expect(onChange).toHaveBeenCalled()
  })

  // ─── Ciclo 17.8 ───────────────────────────────────────────────────────
  it('llama a onVisitanteChange al cambiar el tipo de pase', async () => {
    const onChange = vi.fn()
    render(<SeleccionEntradas cantidad={1} visitantes={visitantes(1)} onCantidadChange={() => {}} onVisitanteChange={onChange} />)
    await userEvent.selectOptions(screen.getByLabelText('Tipo de pase visitante 1'), 'VIP')
    expect(onChange).toHaveBeenCalledWith(0, 'tipoPase', 'VIP')
  })

  // ─── Ciclo 17.9 ───────────────────────────────────────────────────────
  it('llama a onVisitanteChange al editar el nombre', async () => {
    const onChange = vi.fn()
    render(<SeleccionEntradas cantidad={1} visitantes={visitantes(1)} onCantidadChange={() => {}} onVisitanteChange={onChange} />)
    const nombreInput = screen.getByLabelText('Nombre visitante 1')
    await userEvent.clear(nombreInput)
    await userEvent.type(nombreInput, 'Ana')
    expect(onChange).toHaveBeenCalledWith(0, 'nombre', expect.any(String))
  })
})
