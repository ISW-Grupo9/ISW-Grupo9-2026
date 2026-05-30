import { useSearchParams } from 'react-router-dom';
import { usePagoMP } from '../hooks/usePagoMP';
import { formatearPrecio } from '../utils/compraUtils';

export function MercadoPagoPage() {
  const [params] = useSearchParams();
  const compraId = params.get('compraId') ?? '';
  const monto = Number(params.get('monto') ?? 0);

  const { isLoading, isSuccess, error, confirmar } = usePagoMP();

  if (isSuccess) {
    return (
      <div className="min-h-screen bg-[#009ee3] flex items-center justify-center">
        <div className="bg-white rounded-2xl shadow-lg p-10 text-center max-w-md w-full">
          <div className="text-6xl mb-4">✅</div>
          <h2 data-testid="pago-exitoso" className="text-2xl font-bold text-gray-800 mb-2">
            ¡Pago exitoso!
          </h2>
          <p className="text-gray-500 mb-6">
            Tu compra fue confirmada. Revisá tu correo para ver el detalle.
          </p>
          <a
            href="/"
            className="inline-block bg-[#009ee3] text-white font-semibold py-3 px-8 rounded-full hover:bg-[#0084c6]"
          >
            Volver al parque
          </a>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#009ee3] flex items-center justify-center">
      <div className="bg-white rounded-2xl shadow-lg p-10 max-w-md w-full">
        {/* Header MP */}
        <div className="flex items-center gap-2 mb-8">
          <span className="text-2xl font-bold text-[#009ee3]">mercado</span>
          <span className="text-2xl font-bold text-gray-800">pago</span>
        </div>

        {/* Comercio */}
        <p className="text-sm text-gray-500 uppercase tracking-wide mb-1">Pagás a</p>
        <p className="text-lg font-semibold text-gray-800 mb-6">EcoHarmony Park</p>

        {/* Monto */}
        <p className="text-sm text-gray-500 mb-1">Total a pagar</p>
        <p data-testid="monto" className="text-4xl font-bold text-gray-900 mb-2">
          {formatearPrecio(monto)}
        </p>
        <p className="text-sm text-gray-400 mb-8">Entradas — compra #{compraId}</p>

        {/* Método simulado */}
        <div className="border rounded-xl p-4 mb-6 flex items-center gap-3 bg-gray-50">
          <span className="text-2xl">💳</span>
          <div>
            <p className="font-medium text-gray-700">Tarjeta de crédito/débito</p>
            <p className="text-sm text-gray-400">**** **** **** 1234</p>
          </div>
        </div>

        {error && (
          <p role="alert" className="text-red-600 text-sm mb-4 text-center">
            {error}
          </p>
        )}

        {/* Botón Pagar */}
        <button
          onClick={() => confirmar(compraId)}
          disabled={isLoading}
          className="w-full bg-[#009ee3] hover:bg-[#0084c6] disabled:bg-gray-300 text-white font-bold py-4 rounded-full text-lg transition-colors"
        >
          {isLoading ? 'Procesando...' : 'Pagar'}
        </button>

        <p className="text-xs text-center text-gray-400 mt-4">
          Simulación de Mercado Pago — EcoHarmony Park TP TDD
        </p>
      </div>
    </div>
  );
}
