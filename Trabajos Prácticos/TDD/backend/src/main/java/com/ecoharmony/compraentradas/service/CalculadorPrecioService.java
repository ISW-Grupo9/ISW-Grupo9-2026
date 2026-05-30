package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.dto.VisitanteDto;
import java.math.BigDecimal;
import java.util.List;

public interface CalculadorPrecioService {
  BigDecimal calcularTotal(List<VisitanteDto> visitantes);
}
