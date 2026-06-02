package com.ecoharmony.compraentradas.service.impl;

import com.ecoharmony.compraentradas.dto.VisitanteDto;
import com.ecoharmony.compraentradas.service.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CalculadorPrecioServiceImpl implements CalculadorPrecioService {

  @Override
  public BigDecimal calcularTotal(List<VisitanteDto> visitantes) {
    return visitantes.stream().map(this::precioVisitante).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal precioVisitante(VisitanteDto visitante) {
    int edad = visitante.edad();
    BigDecimal precioBase = visitante.tipoPase().getPrecio();

    if (edad <= 3) {
      return BigDecimal.ZERO;
    }
    if (edad <= 15 || edad >= 60) {
      return precioBase.divide(new BigDecimal("2"), 0, RoundingMode.HALF_UP);
    }
    return precioBase;
  }
}
