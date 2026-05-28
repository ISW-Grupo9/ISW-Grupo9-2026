package com.ecoharmony.compraentradas.dto;

import com.ecoharmony.compraentradas.model.TipoPase;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VisitanteDto(
        String nombre,
        @Min(0) int edad,
        @NotNull TipoPase tipoPase
) {}
