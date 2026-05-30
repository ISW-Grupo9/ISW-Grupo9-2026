package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.dto.VisitanteDto;
import java.util.List;

public interface ValidadorVisitantesService {
  void validar(int cantidadEntradas, List<VisitanteDto> visitantes);
}
