package com.ecoharmony.compraentradas.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecoharmony.compraentradas.dto.VisitanteDto;
import com.ecoharmony.compraentradas.model.TipoPase;
import com.ecoharmony.compraentradas.service.impl.CalculadorPrecioServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CalculadorPrecioService — descuentos por edad")
class CalculadorPrecioEdadTest {

  // Precios base confirmados por el PO
  private static final BigDecimal REGULAR = new BigDecimal("10000");
  private static final BigDecimal MITAD_REGULAR = new BigDecimal("5000");
  private static final BigDecimal VIP = new BigDecimal("20000");
  private static final BigDecimal MITAD_VIP = new BigDecimal("10000");

  private CalculadorPrecioService calculador;

  @BeforeEach
  void setUp() {
    calculador = new CalculadorPrecioServiceImpl();
  }

  // ─── Ciclo 7-bis.1 ────────────────────────────────────────────────────────
  // Bebés de hasta 3 años no pagan.
  @Test
  @DisplayName("visitante de 3 años no paga (gratis)")
  void visitante_3_anios_no_paga() {
    List<VisitanteDto> visitantes = List.of(new VisitanteDto("Bebé", 3, TipoPase.REGULAR));
    assertThat(calculador.calcularTotal(visitantes)).isEqualByComparingTo(BigDecimal.ZERO);
  }

  // ─── Ciclo 7-bis.2 ────────────────────────────────────────────────────────
  // Niños de hasta 15 años pagan el 50%.
  @Test
  @DisplayName("visitante de 15 años paga 50% del precio REGULAR")
  void visitante_15_anios_paga_mitad_regular() {
    List<VisitanteDto> visitantes = List.of(new VisitanteDto("Niño", 15, TipoPase.REGULAR));
    assertThat(calculador.calcularTotal(visitantes)).isEqualByComparingTo(MITAD_REGULAR);
  }

  // ─── Ciclo 7-bis.3 ────────────────────────────────────────────────────────
  // Jubilados (60+) pagan el 50%.
  @Test
  @DisplayName("visitante de 60 años paga 50% del precio REGULAR")
  void visitante_60_anios_paga_mitad_regular() {
    List<VisitanteDto> visitantes = List.of(new VisitanteDto("Jubilado", 60, TipoPase.REGULAR));
    assertThat(calculador.calcularTotal(visitantes)).isEqualByComparingTo(MITAD_REGULAR);
  }

  // ─── Ciclo 7-bis.4 ────────────────────────────────────────────────────────
  // El descuento aplica también al pase VIP.
  @Test
  @DisplayName("visitante de 15 años paga 50% del precio VIP")
  void visitante_15_anios_paga_mitad_vip() {
    List<VisitanteDto> visitantes = List.of(new VisitanteDto("Niño VIP", 15, TipoPase.VIP));
    assertThat(calculador.calcularTotal(visitantes)).isEqualByComparingTo(MITAD_VIP);
  }

  // ─── Ciclo 7-bis.5 ────────────────────────────────────────────────────────
  // Un adulto (16–59) paga el precio completo.
  @Test
  @DisplayName("visitante de 30 años paga precio completo")
  void visitante_adulto_paga_precio_completo() {
    List<VisitanteDto> visitantes = List.of(new VisitanteDto("Adulto", 30, TipoPase.REGULAR));
    assertThat(calculador.calcularTotal(visitantes)).isEqualByComparingTo(REGULAR);
  }

  // ─── Ciclo 7-bis.6 ────────────────────────────────────────────────────────
  // Compra mixta: bebé (gratis) + niño (50%) + adulto (completo) + jubilado (50%)
  @Test
  @DisplayName("total correcto para grupo mixto con diferentes edades")
  void total_correcto_para_grupo_mixto() {
    List<VisitanteDto> visitantes =
        List.of(
            new VisitanteDto("Bebé", 3, TipoPase.REGULAR), //     $0
            new VisitanteDto("Niño", 10, TipoPase.REGULAR), //  $5000
            new VisitanteDto("Adulto", 35, TipoPase.REGULAR), // $10000
            new VisitanteDto("Jubilado", 65, TipoPase.REGULAR) //  $5000
            );
    // Total esperado: 0 + 5000 + 10000 + 5000 = $20000
    assertThat(calculador.calcularTotal(visitantes)).isEqualByComparingTo(new BigDecimal("20000"));
  }
}
