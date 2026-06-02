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

@DisplayName("CalculadorPrecioService")
class CalculadorPrecioServiceImplTest {

  private CalculadorPrecioService calculador;

  @BeforeEach
  void setUp() {
    calculador = new CalculadorPrecioServiceImpl();
  }

  // ─── Ciclo 7.1 ────────────────────────────────────────────────────────────
  // Mix de tipos verifica que la suma funciona correctamente entre ambos tipos de pase.
  @Test
  @DisplayName("debe calcular $40000 para 2 REGULAR y 1 VIP")
  void debe_calcular_precio_mix_regular_y_vip() {
    List<VisitanteDto> visitantes =
        List.of(
            new VisitanteDto("Ana", 25, TipoPase.REGULAR),
            new VisitanteDto("Luis", 30, TipoPase.VIP),
            new VisitanteDto("Eva", 22, TipoPase.REGULAR));
    assertThat(calculador.calcularTotal(visitantes)).isEqualByComparingTo(new BigDecimal("40000"));
  }
}
