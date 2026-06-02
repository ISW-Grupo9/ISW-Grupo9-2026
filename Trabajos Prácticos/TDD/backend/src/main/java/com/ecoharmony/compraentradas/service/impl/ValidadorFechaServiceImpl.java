package com.ecoharmony.compraentradas.service.impl;

import com.ecoharmony.compraentradas.exception.FechaInvalidaException;
import com.ecoharmony.compraentradas.exception.ParqueCerradoException;
import com.ecoharmony.compraentradas.service.*;
import java.time.*;
import java.util.Set;

public class ValidadorFechaServiceImpl implements ValidadorFechaService {

  // Feriados fijos confirmados por el PO: 25/12 y 01/01
  private static final Set<MonthDay> FERIADOS_FIJOS =
      Set.of(MonthDay.of(12, 25), MonthDay.of(1, 1));

  private final Set<DayOfWeek> diasCerrados;
  private final Clock clock;

  public ValidadorFechaServiceImpl(Set<DayOfWeek> diasCerrados, Clock clock) {
    this.diasCerrados = diasCerrados;
    this.clock = clock;
  }

  @Override
  public void validar(LocalDate fecha) {
    LocalDate hoy = LocalDate.now(clock);

    if (fecha.isBefore(hoy)) {
      throw new FechaInvalidaException("La fecha de visita no puede ser anterior a hoy");
    }

    if (diasCerrados.contains(fecha.getDayOfWeek())) {
      throw new ParqueCerradoException(
          "El parque no abre el " + fecha.getDayOfWeek() + ". SeleccionÃ¡ otro dÃ­a.");
    }

    if (FERIADOS_FIJOS.contains(MonthDay.from(fecha))) {
      throw new ParqueCerradoException(
          "El parque no abre el " + fecha + " (feriado). SeleccionÃ¡ otro dÃ­a.");
    }
  }
}
