package com.ecoharmony.compraentradas.model;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TipoPase")
class TipoPaseTest {

  // ─── Ciclo 2.1 ────────────────────────────────────────────────────────────
  // Verifica que el tipo de pase REGULAR tiene un precio positivo definido.
  // Sin precio no se puede calcular el monto total de la compra.
  @Test
  @DisplayName("REGULAR debe tener precio mayor a cero")
  void REGULAR_debe_tener_precio_configurado() {
    assertThat(TipoPase.REGULAR.getPrecio()).isGreaterThan(BigDecimal.ZERO);
  }

  // ─── Ciclo 2.2 ────────────────────────────────────────────────────────────
  // Verifica que el tipo de pase VIP tiene un precio positivo definido.
  // Misma razón que REGULAR — necesario para el cálculo del total.
  @Test
  @DisplayName("VIP debe tener precio mayor a cero")
  void VIP_debe_tener_precio_configurado() {
    assertThat(TipoPase.VIP.getPrecio()).isGreaterThan(BigDecimal.ZERO);
  }

  // ─── Ciclo 2.3 ────────────────────────────────────────────────────────────
  // Verifica que VIP cuesta más que REGULAR.
  // El pase VIP debe representar una categoría premium con mayor precio.
  @Test
  @DisplayName("VIP debe costar más que REGULAR")
  void VIP_debe_costar_mas_que_REGULAR() {
    assertThat(TipoPase.VIP.getPrecio()).isGreaterThan(TipoPase.REGULAR.getPrecio());
  }
}
