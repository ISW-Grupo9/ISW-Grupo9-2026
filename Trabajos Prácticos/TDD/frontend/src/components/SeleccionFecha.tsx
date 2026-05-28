import { DayPicker } from 'react-day-picker'
import type { Matcher } from 'react-day-picker'
import { es } from 'date-fns/locale'

const DAY_MAP: Record<string, number> = {
  SUNDAY: 0, MONDAY: 1, TUESDAY: 2, WEDNESDAY: 3,
  THURSDAY: 4, FRIDAY: 5, SATURDAY: 6,
}

function buildDisabled(diasCerrados: string[], feriadosFijos: string[]): Matcher[] {
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  const matchers: Matcher[] = [{ before: today }]

  const weekdays = diasCerrados.map(d => DAY_MAP[d]).filter(n => n !== undefined)
  if (weekdays.length) matchers.push({ dayOfWeek: weekdays })

  const year = today.getFullYear()
  for (const f of feriadosFijos) {
    const [mm, dd] = f.split('-').map(Number)
    for (let y = year; y <= year + 2; y++) {
      matchers.push(new Date(y, mm - 1, dd))
    }
  }

  return matchers
}

function toISO(date: Date): string {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

interface Props {
  value: string
  onChange: (fecha: string) => void
  error?: string
  diasCerrados?: string[]
  feriadosFijos?: string[]
}

export function SeleccionFecha({
  value,
  onChange,
  error,
  diasCerrados = [],
  feriadosFijos = [],
}: Props) {
  const hoy = new Date().toISOString().split('T')[0]
  const selected = value ? new Date(value + 'T00:00:00') : undefined
  const disabled = buildDisabled(diasCerrados, feriadosFijos)

  return (
    <div className="space-y-2">
      {/* Input oculto — mantiene compatibilidad con tests existentes */}
      <input
        type="date"
        value={value}
        min={hoy}
        onChange={e => onChange(e.target.value)}
        style={{ position: 'absolute', opacity: 0, width: 1, height: 1, overflow: 'hidden' }}
        tabIndex={-1}
      />

      {/* Calendario visual */}
      <div data-testid="calendar-picker" className="flex justify-center">
        <DayPicker
          mode="single"
          selected={selected}
          onSelect={date => date && onChange(toISO(date))}
          disabled={disabled}
          defaultMonth={selected ?? new Date()}
          showOutsideDays
          locale={es}
        />
      </div>

      {/* Nota de apertura */}
      <p className="text-xs text-forest-800/40 text-center">
        Abre todos los días excepto lunes, 25 de diciembre y 1 de enero.
      </p>

      {error && (
        <span role="alert" className="text-xs text-red-500 text-center block">
          {error}
        </span>
      )}
    </div>
  )
}
