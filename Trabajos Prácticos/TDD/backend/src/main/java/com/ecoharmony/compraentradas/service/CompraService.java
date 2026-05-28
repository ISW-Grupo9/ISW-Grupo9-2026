package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.dto.CompraRequest;
import com.ecoharmony.compraentradas.dto.CompraResponse;

public interface CompraService {
    CompraResponse crear(CompraRequest request);
    CompraResponse buscarPorId(Long id);
    CompraResponse confirmar(Long id);
}
