package com.ecoharmony.compraentradas.dto;

import java.util.List;

public record ParqueReglasResponse(
        List<String> diasCerrados,
        List<String> feriadosFijos
) {}
