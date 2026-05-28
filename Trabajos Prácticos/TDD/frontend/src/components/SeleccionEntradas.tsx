import type { Visitante } from '../types'

interface Props {
  cantidad: number
  visitantes: Visitante[]
  onCantidadChange: (n: number) => void
  onVisitanteChange: (index: number, campo: keyof Visitante, valor: string | number) => void
  error?: string
}

export function SeleccionEntradas({ cantidad, visitantes, onCantidadChange, onVisitanteChange, error }: Props) {
  return (
    <div className="space-y-5">
      <div>
        <label className="block text-xs font-medium text-forest-800/50 uppercase tracking-widest mb-2">
          Cantidad de entradas
        </label>
        <select
          value={cantidad}
          onChange={e => onCantidadChange(Number(e.target.value))}
          aria-label="Cantidad de entradas"
          className="w-full px-4 py-3 bg-cream-50 border border-cream-300 rounded-xl text-forest-900 text-sm focus:outline-none focus:ring-2 focus:ring-forest-800/20 focus:border-forest-800 transition-colors cursor-pointer"
        >
          {[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map(n => (
            <option key={n} value={n}>
              {n === 0 ? 'Seleccionar cantidad' : `${n} entrada${n > 1 ? 's' : ''}`}
            </option>
          ))}
        </select>
        {error && (
          <span role="alert" className="text-xs text-red-500 mt-1 block">{error}</span>
        )}
      </div>

      {visitantes.length > 0 && (
        <div className="space-y-2">
          <p className="text-xs font-medium text-forest-800/50 uppercase tracking-widest">
            Detalle de visitantes
          </p>
          {visitantes.map((v, i) => (
            <div
              key={i}
              data-testid={`visitante-${i}`}
              className="flex items-center gap-3 bg-cream-50 border border-cream-200 rounded-xl px-4 py-3"
            >
              <span className="text-xs text-forest-800/40 w-16 shrink-0">
                N.° {i + 1}
              </span>
              <div className="flex-1 flex flex-col gap-0.5">
                <span className="text-[10px] text-forest-800/40 uppercase tracking-wider px-1">Nombre</span>
                <input
                  type="text"
                  value={v.nombre}
                  aria-label={`Nombre visitante ${i + 1}`}
                  onChange={e => onVisitanteChange(i, 'nombre', e.target.value)}
                  placeholder="Nombre"
                  className="w-full px-3 py-2 bg-white border border-cream-300 rounded-lg text-sm text-forest-900 focus:outline-none focus:border-forest-800 transition-colors"
                />
              </div>
              <div className="shrink-0 flex flex-col gap-0.5">
                <span className="text-[10px] text-forest-800/40 uppercase tracking-wider px-1">Edad</span>
                <input
                  type="number"
                  value={v.edad}
                  aria-label={`Edad visitante ${i + 1}`}
                  onChange={e => onVisitanteChange(i, 'edad', Number(e.target.value))}
                  min={0}
                  className="w-16 px-3 py-2 bg-white border border-cream-300 rounded-lg text-sm text-forest-900 focus:outline-none focus:border-forest-800 transition-colors"
                />
              </div>
              <div className="flex-1 flex flex-col gap-0.5">
                <span className="text-[10px] text-forest-800/40 uppercase tracking-wider px-1">Tipo de pase</span>
                <select
                  value={v.tipoPase}
                  aria-label={`Tipo de pase visitante ${i + 1}`}
                  onChange={e => onVisitanteChange(i, 'tipoPase', e.target.value)}
                  className="w-full px-3 py-2 bg-white border border-cream-300 rounded-lg text-sm text-forest-900 focus:outline-none focus:border-forest-800 transition-colors cursor-pointer"
                >
                  <option value="REGULAR">Regular</option>
                  <option value="VIP">VIP</option>
                </select>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
