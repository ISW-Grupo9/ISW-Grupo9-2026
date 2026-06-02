package com.ecoharmony.compraentradas.service;

import static org.assertj.core.api.Assertions.*;

import com.ecoharmony.compraentradas.exception.FormaPagoRequeridaException;
import com.ecoharmony.compraentradas.model.FormaPago;
import com.ecoharmony.compraentradas.service.impl.ValidadorFormaPagoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ValidadorFormaPagoService")
class ValidadorFormaPagoServiceImplTest {

  private ValidadorFormaPagoService validador;

  @BeforeEach
  void setUp() {
    validador = new ValidadorFormaPagoServiceImpl();
  }

  // ─── Ciclo 9.1 ────────────────────────────────────────────────────────────
  // Cualquier forma de pago no nula es válida; la lógica solo rechaza null.
  @Test
  @DisplayName("debe aceptar una forma de pago válida")
  void debe_aceptar_forma_pago_valida() {
    assertThatNoException().isThrownBy(() -> validador.validar(FormaPago.EFECTIVO));
  }

  // ─── Ciclo 9.2 ────────────────────────────────────────────────────────────
  // Sin forma de pago no se puede procesar la compra.
  @Test
  @DisplayName("debe rechazar forma de pago null")
  void debe_rechazar_forma_pago_null() {
    assertThatThrownBy(() -> validador.validar(null))
        .isInstanceOf(FormaPagoRequeridaException.class);
  }
}
