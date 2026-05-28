package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.dto.VisitanteDto;
import com.ecoharmony.compraentradas.exception.VisitantesInvalidosException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidadorVisitantesServiceImpl implements ValidadorVisitantesService {

    @Override
    public void validar(int cantidadEntradas, List<VisitanteDto> visitantes) {
        if (visitantes == null || visitantes.isEmpty()) {
            throw new VisitantesInvalidosException("La lista de visitantes no puede ser nula o vacía.");
        }
        if (visitantes.size() != cantidadEntradas) {
            throw new VisitantesInvalidosException(
                    "La cantidad de visitantes (" + visitantes.size() +
                    ") no coincide con las entradas compradas (" + cantidadEntradas + ").");
        }
    }
}
