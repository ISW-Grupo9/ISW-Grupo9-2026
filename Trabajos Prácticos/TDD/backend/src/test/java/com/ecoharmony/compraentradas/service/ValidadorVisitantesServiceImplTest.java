package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.dto.VisitanteDto;
import com.ecoharmony.compraentradas.exception.VisitantesInvalidosException;
import com.ecoharmony.compraentradas.model.TipoPase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ValidadorVisitantesService")
class ValidadorVisitantesServiceImplTest {

    private ValidadorVisitantesService validador;

    private static final VisitanteDto VISITANTE_REGULAR =
            new VisitanteDto("Ana", 25, TipoPase.REGULAR);
    private static final VisitanteDto VISITANTE_VIP =
            new VisitanteDto("Luis", 30, TipoPase.VIP);

    @BeforeEach
    void setUp() {
        validador = new ValidadorVisitantesServiceImpl();
    }

    // ─── Ciclo 6.1 ────────────────────────────────────────────────────────────
    // Caso normal: la cantidad de visitantes coincide exactamente con las entradas.
    @Test
    @DisplayName("debe aceptar cuando la cantidad de visitantes coincide con las entradas")
    void debe_aceptar_cuando_cantidad_coincide() {
        List<VisitanteDto> visitantes = List.of(VISITANTE_REGULAR, VISITANTE_VIP);
        assertThatNoException().isThrownBy(() -> validador.validar(2, visitantes));
    }

    // ─── Ciclo 6.2 ────────────────────────────────────────────────────────────
    // Más visitantes que entradas compradas: situación inválida.
    @Test
    @DisplayName("debe rechazar cuando hay más visitantes que entradas")
    void debe_rechazar_mas_visitantes_que_entradas() {
        List<VisitanteDto> visitantes = List.of(VISITANTE_REGULAR, VISITANTE_VIP);
        assertThatThrownBy(() -> validador.validar(1, visitantes))
                .isInstanceOf(VisitantesInvalidosException.class);
    }

    // ─── Ciclo 6.3 ────────────────────────────────────────────────────────────
    // Menos visitantes que entradas: sobran entradas sin asignar, inválido.
    @Test
    @DisplayName("debe rechazar cuando hay menos visitantes que entradas")
    void debe_rechazar_menos_visitantes_que_entradas() {
        List<VisitanteDto> visitantes = List.of(VISITANTE_REGULAR);
        assertThatThrownBy(() -> validador.validar(3, visitantes))
                .isInstanceOf(VisitantesInvalidosException.class);
    }

    // ─── Ciclo 6.4 ────────────────────────────────────────────────────────────
    // Lista null: no se puede procesar una compra sin datos de visitantes.
    @Test
    @DisplayName("debe rechazar lista de visitantes null")
    void debe_rechazar_lista_null() {
        assertThatThrownBy(() -> validador.validar(1, null))
                .isInstanceOf(VisitantesInvalidosException.class);
    }

    // ─── Ciclo 6.5 ────────────────────────────────────────────────────────────
    // Lista vacía: igualmente inválido, no hay visitantes registrados.
    @Test
    @DisplayName("debe rechazar lista de visitantes vacía")
    void debe_rechazar_lista_vacia() {
        assertThatThrownBy(() -> validador.validar(1, List.of()))
                .isInstanceOf(VisitantesInvalidosException.class);
    }
}
