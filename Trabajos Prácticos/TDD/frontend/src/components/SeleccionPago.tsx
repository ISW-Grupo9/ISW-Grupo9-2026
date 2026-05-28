import type { FormaPago } from '../types'

interface Props {
  selectedPago: FormaPago | null
  onPagoSeleccionado: (pago: FormaPago) => void
  error?: string
}

const OPCIONES: { value: FormaPago; label: string; desc: string }[] = [
  { value: 'EFECTIVO', label: 'Efectivo', desc: 'Abonás en boletería' },
  { value: 'TARJETA', label: 'Tarjeta', desc: 'Crédito o débito' },
]

export function SeleccionPago({ selectedPago, onPagoSeleccionado, error }: Props) {
  return (
    <div className="space-y-3">
      <div className="grid grid-cols-2 gap-3">
        {OPCIONES.map(({ value, label, desc }) => {
          const active = selectedPago === value
          return (
            <button
              key={value}
              onClick={() => onPagoSeleccionado(value)}
              aria-pressed={active}
              className={`py-4 px-4 rounded-xl border-2 text-left transition-all ${
                active
                  ? 'border-forest-900 bg-forest-900 text-white'
                  : 'border-cream-300 bg-cream-50 text-forest-800 hover:border-forest-600'
              }`}
            >
              <p className={`font-medium text-sm ${active ? 'activo text-white' : 'text-forest-900'}`}>{label}</p>
              <p className={`text-xs mt-0.5 ${active ? 'text-white/60' : 'text-forest-800/40'}`}>{desc}</p>
            </button>
          )
        })}
      </div>
      {error && (
        <span role="alert" className="text-xs text-red-500">{error}</span>
      )}
    </div>
  )
}
