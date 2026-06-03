import { useState, useCallback, useMemo } from 'react';
import type { Visitante, FormaPago, CompraFormErrors } from '../types';
import {
  esFechaValida,
  esCantidadValida,
  calcularTotal,
  esVisitanteValido,
  esNombreValido,
  MAX_EDAD,
} from '../utils/compraUtils';

const visitanteVacio = (): Visitante => ({ nombre: '', edad: null, tipoPase: 'REGULAR' });

export function useCompraForm() {
  const [fechaVisita, setFechaVisitaState] = useState(() => {
    const d = new Date();
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  });
  const [visitantes, setVisitantes] = useState<Visitante[]>([]);
  const [formaPago, setFormaPago] = useState<FormaPago | null>(null);
  const [errors, setErrors] = useState<CompraFormErrors>({});

  const setFechaVisita = useCallback((fecha: string) => {
    setFechaVisitaState(fecha);
    let fechaError: string | undefined;
    if (fecha && !esFechaValida(fecha)) {
      fechaError = 'La fecha no puede ser en el pasado';
    }
    setErrors((prev) => ({ ...prev, fechaVisita: fechaError }));
  }, []);

  const setCantidad = useCallback((cantidad: number) => {
    setVisitantes((prev) => {
      if (cantidad > prev.length) {
        return [
          ...prev,
          ...Array(cantidad - prev.length)
            .fill(null)
            .map(visitanteVacio),
        ];
      }
      return prev.slice(0, cantidad);
    });
    setErrors((prev) => ({
      ...prev,
      cantidad: !esCantidadValida(cantidad) ? `La cantidad debe ser entre 1 y 10` : undefined,
    }));
  }, []);

  const setEdadVisitante = useCallback((index: number, edad: number | null) => {
    setVisitantes((prev) => prev.map((v, i) => (i === index ? { ...v, edad } : v)));
    setErrors((prev) => {
      const edadesError = [...(prev.edadesError ?? [])];
      edadesError[index] =
      edad === null || edad < 0 || edad > MAX_EDAD
        ? `Ingrese una edad válida (0-${MAX_EDAD})`
        : undefined;
      return { ...prev, edadesError };
    });
  }, []);

  const validateEdades = useCallback((): boolean => {
    const edadesError = visitantes.map((v) =>
      v.edad === null || v.edad < 0 || v.edad > MAX_EDAD
        ? `Ingrese una edad válida (0-${MAX_EDAD})`
        : undefined,
    );
    const hasError = edadesError.some(Boolean);
    if (hasError) setErrors((prev) => ({ ...prev, edadesError }));
    return !hasError;
  }, [visitantes]);

  const setNombreVisitante = useCallback((index: number, nombre: string) => {
    setVisitantes((prev) => prev.map((v, i) => (i === index ? { ...v, nombre } : v)));
    setErrors((prev) => {
      const nombresError = [...(prev.nombresError ?? [])];
      nombresError[index] = esNombreValido(nombre)
        ? undefined
        : 'El nombre solo puede contener letras';
      return { ...prev, nombresError };
    });
  }, []);

  const setTipoPaseVisitante = useCallback((index: number, tipoPase: Visitante['tipoPase']) => {
    setVisitantes((prev) => prev.map((v, i) => (i === index ? { ...v, tipoPase } : v)));
  }, []);

  const total = useMemo(() => calcularTotal(visitantes), [visitantes]);

  const isValid = useMemo(() => {
    return (
      !!fechaVisita &&
      esFechaValida(fechaVisita) &&
      esCantidadValida(visitantes.length) &&
      visitantes.every(esVisitanteValido) &&
      !!formaPago &&
      !errors.fechaVisita &&
      !errors.cantidad
    );
  }, [fechaVisita, visitantes, formaPago, errors]);

  return {
    fechaVisita,
    visitantes,
    formaPago,
    errors,
    total,
    isValid,
    setFechaVisita,
    setCantidad,
    setFormaPago,
    setNombreVisitante,
    setEdadVisitante,
    setTipoPaseVisitante,
    validateEdades,
  };
}
