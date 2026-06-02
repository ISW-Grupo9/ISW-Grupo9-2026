import { useState } from 'react';
import { useCompraForm } from '../hooks/useCompraForm';
import { useSubmitCompra } from '../hooks/useSubmitCompra';
import { SeleccionFecha } from './SeleccionFecha';
import { SeleccionEntradas } from './SeleccionEntradas';
import { SeleccionPago } from './SeleccionPago';
import { ResumenCompra } from './ResumenCompra';
import { formatearPrecio } from '../utils/compraUtils';
import { useParqueReglas } from '../hooks/useParqueReglas';
import type { Visitante } from '../types';

interface Props {
  usuarioId: string;
  usuarioEmail?: string;
  navigate?: (path: string) => void;
  redirect?: (url: string) => void;
}

export function CompraPage({ usuarioId, usuarioEmail, navigate = () => {}, redirect }: Props) {
  const form = useCompraForm();
  const reglas = useParqueReglas();
  const { isLoading, isSuccess, data, error, submit } = useSubmitCompra(navigate, redirect);
  const [fechaError, setFechaError] = useState<string | undefined>();
  const [cantidadError, setCantidadError] = useState<string | undefined>();
  const [formaPagoError, setFormaPagoError] = useState<string | undefined>();

  const handleSubmit = () => {
    const nextFechaError = !form.fechaVisita
      ? 'La fecha de visita es requerida'
      : form.errors.fechaVisita;
    const nextCantidadError = form.visitantes.length === 0 ? 'Seleccioná visitantes' : undefined;
    const nextFormaPagoError = !form.formaPago ? 'Seleccioná una forma de pago' : undefined;
    setFechaError(nextFechaError);
    setCantidadError(nextCantidadError);
    setFormaPagoError(nextFormaPagoError);

    if (nextFechaError || nextCantidadError || nextFormaPagoError) return;

    submit({
      usuarioId,
      fechaVisita: form.fechaVisita,
      visitantes: form.visitantes,
      formaPago: form.formaPago,
    });
  };

  if (isSuccess && data) {
    return (
      <div className="min-h-screen bg-cream-100 flex items-center justify-center px-4 py-12">
        <div
          data-testid="confirmacion"
          className="bg-white rounded-3xl shadow-lg p-10 max-w-md w-full text-center animate-fade-up"
        >
          <div className="w-16 h-16 bg-forest-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <svg
              className="w-8 h-8 text-forest-800"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 13l4 4L19 7"
              />
            </svg>
          </div>
          <h2 className="font-display text-4xl text-forest-900 mb-2">¡Compra confirmada!</h2>
          <p className="text-forest-800/50 mb-8 text-sm">Tu reserva fue registrada exitosamente.</p>
          <div className="bg-cream-100 rounded-2xl p-6 text-left space-y-3">
            <div className="flex justify-between items-center">
              <span className="text-xs text-forest-800/50 uppercase tracking-widest">Fecha</span>
              <span className="font-medium text-forest-900 text-sm">{data.fechaVisita}</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-xs text-forest-800/50 uppercase tracking-widest">Entradas</span>
              <span className="font-medium text-forest-900 text-sm">{data.cantidadEntradas}</span>
            </div>
            <div className="flex justify-between items-center border-t border-cream-200 pt-3">
              <span className="text-xs font-semibold text-forest-900 uppercase tracking-widest">
                Total
              </span>
              <span className="font-display text-2xl text-forest-900">
                {formatearPrecio(data.montoTotal)}
              </span>
            </div>
          </div>
          <a
            href="/"
            className="mt-6 inline-block w-full bg-forest-900 hover:bg-forest-800 text-white font-medium py-3 rounded-xl text-center transition-colors text-sm tracking-wide"
          >
            Volver al parque
          </a>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-cream-100">
      {/* Header */}
      <header className="bg-forest-900 py-10 px-4">
        <div className="max-w-2xl mx-auto flex justify-between items-start">
          <div className="flex items-center gap-4">
            <img
              src="/logo.jpg"
              alt="EcoHarmony Park"
              className="w-16 h-16 rounded-full object-cover border-2 border-forest-600 shadow-md"
            />
            <div>
              <p className="text-forest-600 text-xs font-medium tracking-[0.2em] uppercase mb-1">
                Reserva tu visita
              </p>
              <h1 className="font-display text-5xl text-white leading-tight">EcoHarmony Park</h1>
            </div>
          </div>
          {usuarioEmail && (
            <div
              className="bg-forest-800/50 border border-forest-700/50 rounded-xl p-3 flex items-center gap-4 shadow-sm"
              data-testid="usuario-logueado"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-forest-700 rounded-full flex items-center justify-center text-white">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                    />
                  </svg>
                </div>
                <div className="flex flex-col">
                  <span className="text-white text-sm font-semibold">
                    {usuarioEmail.split('@')[0]}
                  </span>
                  <span className="text-forest-300 text-xs">{usuarioEmail}</span>
                </div>
              </div>
              <div className="w-px h-8 bg-forest-700/50 mx-1"></div>
              <button
                type="button"
                title="Cerrar sesión"
                className="p-2 text-forest-300 hover:text-white hover:bg-forest-700 rounded-lg transition-colors cursor-pointer"
                onClick={(e) => e.preventDefault()}
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
                  />
                </svg>
              </button>
            </div>
          )}
        </div>
      </header>

      {/* Form */}
      <main className="max-w-2xl mx-auto px-4 py-8 space-y-4">
        {/* Fecha */}
        <section
          className="bg-white rounded-2xl border border-cream-200 shadow-sm p-6 animate-fade-up"
          style={{ animationDelay: '0ms' }}
        >
          <h2 className="font-display text-xl text-forest-900 mb-4">Fecha de visita</h2>
          <SeleccionFecha
            value={form.fechaVisita}
            onChange={form.setFechaVisita}
            error={form.errors.fechaVisita ?? (!form.fechaVisita ? fechaError : undefined)}
            diasCerrados={reglas.diasCerrados}
            feriadosFijos={reglas.feriadosFijos}
          />
        </section>

        {/* Visitantes */}
        <section
          className="bg-white rounded-2xl border border-cream-200 shadow-sm p-6 animate-fade-up"
          style={{ animationDelay: '60ms' }}
        >
          <h2 className="font-display text-xl text-forest-900 mb-4">Visitantes</h2>
          <SeleccionEntradas
            cantidad={form.visitantes.length}
            visitantes={form.visitantes}
            onCantidadChange={form.setCantidad}
            onVisitanteChange={(i, campo, valor) => {
              if (campo === 'nombre') form.setNombreVisitante(i, valor as string);
              if (campo === 'edad') form.setEdadVisitante(i, valor as number);
              if (campo === 'tipoPase')
                form.setTipoPaseVisitante(i, valor as Visitante['tipoPase']);
            }}
            error={
              form.errors.cantidad ?? (form.visitantes.length === 0 ? cantidadError : undefined)
            }
            nombresError={form.errors.nombresError}
          />
        </section>

        {/* Resumen */}
        <section
          className="bg-forest-900 rounded-2xl p-6 animate-fade-up"
          style={{ animationDelay: '120ms' }}
        >
          <h2 className="font-display text-xl text-white/70 mb-4">Resumen</h2>
          <ResumenCompra fechaVisita={form.fechaVisita} visitantes={form.visitantes} />
        </section>

        {/* Pago */}
        <section
          className="bg-white rounded-2xl border border-cream-200 shadow-sm p-6 animate-fade-up"
          style={{ animationDelay: '180ms' }}
        >
          <h2 className="font-display text-xl text-forest-900 mb-4">Forma de pago</h2>
          <SeleccionPago
            selectedPago={form.formaPago}
            onPagoSeleccionado={form.setFormaPago}
            error={formaPagoError}
          />
        </section>

        {/* Errores de validación local */}
        {/* Error global de la API */}
        {error && (
          <p
            role="alert"
            data-testid="error-global"
            className="text-red-600 text-sm bg-red-50 border border-red-200 rounded-xl px-4 py-3"
          >
            {error}
          </p>
        )}

        {/* Submit */}
        <button
          onClick={handleSubmit}
          disabled={isLoading}
          className="w-full bg-forest-900 hover:bg-forest-800 active:bg-forest-950 disabled:opacity-50 disabled:cursor-not-allowed text-white font-medium py-4 rounded-2xl transition-colors text-base tracking-wide"
        >
          {isLoading ? 'Procesando...' : 'Confirmar compra'}
        </button>

        <p className="text-center text-xs text-forest-800/30 pb-8">
          EcoHarmony Park — Sistema de reservas
        </p>
      </main>
    </div>
  );
}
