package com.ecoharmony.compraentradas.dto;

import com.ecoharmony.compraentradas.model.FormaPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CompraRequest(
        @NotNull Long usuarioId,
        @NotNull LocalDate fechaVisita,
        @NotEmpty @Valid List<VisitanteDto> visitantes,
        @NotNull FormaPago formaPago
) {}
