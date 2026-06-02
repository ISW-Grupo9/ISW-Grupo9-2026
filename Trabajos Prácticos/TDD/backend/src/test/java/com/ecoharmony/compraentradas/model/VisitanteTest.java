package com.ecoharmony.compraentradas.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Visitante")
class VisitanteTest {

  // ─── Ciclo 1.1 ────────────────────────────────────────────────────────────
  // Verifica que el constructor acepta datos válidos y que cada campo
  // queda almacenado correctamente en el objeto creado.
  @Test
  @DisplayName("debe crearse con nombre, edad y tipo de pase válidos")
  void debe_crearse_con_nombre_edad_y_tipo_pase() {
    var visitante = new Visitante("Juan Pérez", 30, TipoPase.REGULAR);

    assertThat(visitante.getNombre()).isEqualTo("Juan Pérez");
    assertThat(visitante.getEdad()).isEqualTo(30);
    assertThat(visitante.getTipoPase()).isEqualTo(TipoPase.REGULAR);
  }

  // ─── Ciclo 1.2 ────────────────────────────────────────────────────────────
  // Verifica que una edad negativa es un estado inválido para un visitante.
  // Un visitante real no puede tener −1 años; el sistema debe rechazarlo.
  @Test
  @DisplayName("debe rechazar edad negativa")
  void debe_rechazar_edad_negativa() {
    assertThatThrownBy(() -> new Visitante("Juan Pérez", -1, TipoPase.REGULAR))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("edad");
  }

  // ─── Ciclo 1.3 ────────────────────────────────────────────────────────────
  // Bebés menores de un año tienen edad = 0 y deben poder comprar entrada
  // (entran gratis según la regla de precios por edad).
  @Test
  @DisplayName("debe aceptar edad cero para bebés menores de un año")
  void debe_aceptar_edad_cero_para_bebes() {
    var visitante = new Visitante("Bebé García", 0, TipoPase.REGULAR);
    assertThat(visitante.getEdad()).isZero();
  }

  // ─── Ciclo 1.4 ────────────────────────────────────────────────────────────
  // El nombre es opcional: null o vacío se almacena como null.
  @Test
  @DisplayName("debe aceptar nombre nulo (campo opcional)")
  void debe_aceptar_nombre_nulo() {
    var visitante = new Visitante(null, 25, TipoPase.VIP);
    assertThat(visitante.getNombre()).isNull();
  }

  // ─── Ciclo 1.5 ────────────────────────────────────────────────────────────
  // Nombre con solo espacios también se trata como ausente (null).
  @Test
  @DisplayName("debe normalizar nombre vacío a null")
  void debe_normalizar_nombre_vacio_a_null() {
    var visitante = new Visitante("   ", 25, TipoPase.VIP);
    assertThat(visitante.getNombre()).isNull();
  }
}
