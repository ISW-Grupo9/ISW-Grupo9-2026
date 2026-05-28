package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.exception.CantidadInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ValidadorCantidadService")
class ValidadorCantidadServiceImplTest {

    private ValidadorCantidadService validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorCantidadServiceImpl();
    }

    // ─── Ciclo 5.1 ────────────────────────────────────────────────────────────
    // El límite inferior válido: 1 entrada es la compra mínima posible.
    @Test
    @DisplayName("debe aceptar cantidad 1 (mínimo válido)")
    void debe_aceptar_cantidad_1() {
        assertThatNoException().isThrownBy(() -> validador.validar(1));
    }

    // ─── Ciclo 5.2 ────────────────────────────────────────────────────────────
    // El límite superior válido: 10 entradas es el máximo por compra.
    @Test
    @DisplayName("debe aceptar cantidad 10 (máximo válido)")
    void debe_aceptar_cantidad_10() {
        assertThatNoException().isThrownBy(() -> validador.validar(10));
    }

    // ─── Ciclo 5.3 ────────────────────────────────────────────────────────────
    // Cero no tiene sentido como cantidad de entradas.
    @Test
    @DisplayName("debe rechazar cantidad 0")
    void debe_rechazar_cantidad_0() {
        assertThatThrownBy(() -> validador.validar(0))
                .isInstanceOf(CantidadInvalidaException.class);
    }

    // ─── Ciclo 5.4 ────────────────────────────────────────────────────────────
    // Un valor negativo es claramente inválido.
    @Test
    @DisplayName("debe rechazar cantidad negativa")
    void debe_rechazar_cantidad_negativa() {
        assertThatThrownBy(() -> validador.validar(-1))
                .isInstanceOf(CantidadInvalidaException.class);
    }

    // ─── Ciclo 5.5 ────────────────────────────────────────────────────────────
    // 11 supera el límite máximo de 10 entradas por compra.
    @Test
    @DisplayName("debe rechazar cantidad 11 (supera el máximo)")
    void debe_rechazar_cantidad_11() {
        assertThatThrownBy(() -> validador.validar(11))
                .isInstanceOf(CantidadInvalidaException.class);
    }
}
