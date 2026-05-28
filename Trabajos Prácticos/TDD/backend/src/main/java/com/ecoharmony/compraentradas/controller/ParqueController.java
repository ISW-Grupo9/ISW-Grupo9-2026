package com.ecoharmony.compraentradas.controller;

import com.ecoharmony.compraentradas.dto.ParqueReglasResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/parque")
public class ParqueController {

    // Feriados fijos confirmados por el PO: 25/12 y 01/01
    private static final List<String> FERIADOS_FIJOS = List.of("12-25", "01-01");

    private final List<String> diasCerrados;

    public ParqueController(@Value("${ecoharmony.parque.dias-cerrados}") String diasCerradosStr) {
        this.diasCerrados = Arrays.stream(diasCerradosStr.split(","))
                .map(String::trim)
                .toList();
    }

    @GetMapping("/reglas")
    public ResponseEntity<ParqueReglasResponse> getReglas() {
        return ResponseEntity.ok(new ParqueReglasResponse(diasCerrados, FERIADOS_FIJOS));
    }
}
