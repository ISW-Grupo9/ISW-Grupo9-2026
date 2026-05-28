package com.ecoharmony.compraentradas.dto;

import com.ecoharmony.compraentradas.model.EstadoCompra;
import com.ecoharmony.compraentradas.model.FormaPago;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CompraResponse(
        Long id,
        LocalDate fechaVisita,
        int cantidadEntradas,
        BigDecimal montoTotal,
        FormaPago formaPago,
        EstadoCompra estado,
        String urlPago
) {}
