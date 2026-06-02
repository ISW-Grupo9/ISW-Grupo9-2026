package com.ecoharmony.compraentradas.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ecoharmony.compraentradas.exception.ParqueCerradoException;
import com.ecoharmony.compraentradas.service.impl.ValidadorFechaServiceImpl;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ValidadorFechaService — feriados fijos")
class ValidadorFechaFeriadosTest {

  // Clock fijo: martes 10 de junio de 2026 (misma base que el ciclo 4)
  private static final LocalDate HOY = LocalDate.of(2026, 6, 10);
  private static final Clock CLOCK =
      Clock.fixed(HOY.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

  private ValidadorFechaService validador;

  @BeforeEach
  void setUp() {
    validador = new ValidadorFechaServiceImpl(Set.of(DayOfWeek.MONDAY), CLOCK);
  }

  // ─── Ciclo 4-bis.1 ────────────────────────────────────────────────────────
  // El 25 de diciembre es feriado fijo: el parque no abre.
  @Test
  @DisplayName("debe rechazar el 25 de diciembre (feriado fijo)")
  void debe_rechazar_navidad() {
    LocalDate navidad = LocalDate.of(2026, 12, 25);
    assertThatThrownBy(() -> validador.validar(navidad))
        .isInstanceOf(ParqueCerradoException.class)
        .hasMessageContaining("feriado");
  }

  // ─── Ciclo 4-bis.2 ────────────────────────────────────────────────────────
  // El 1 de enero es feriado fijo: el parque no abre.
  @Test
  @DisplayName("debe rechazar el 1 de enero (feriado fijo)")
  void debe_rechazar_anio_nuevo() {
    LocalDate anioNuevo = LocalDate.of(2027, 1, 1);
    assertThatThrownBy(() -> validador.validar(anioNuevo))
        .isInstanceOf(ParqueCerradoException.class)
        .hasMessageContaining("feriado");
  }

  // ─── Ciclo 4-bis.3 ────────────────────────────────────────────────────────
  // Verifica que el 24 de diciembre (víspera) sí es un día hábil.
  @Test
  @DisplayName("debe aceptar el 24 de diciembre (día hábil)")
  void debe_aceptar_vispera_navidad() {
    LocalDate vispera = LocalDate.of(2026, 12, 24);
    assertThatNoException().isThrownBy(() -> validador.validar(vispera));
  }

  // ─── Ciclo 4-bis.4 ────────────────────────────────────────────────────────
  // Verifica que el 2 de enero sí es un día hábil.
  @Test
  @DisplayName("debe aceptar el 2 de enero (día hábil)")
  void debe_aceptar_dos_de_enero() {
    LocalDate dosEnero = LocalDate.of(2027, 1, 2);
    assertThatNoException().isThrownBy(() -> validador.validar(dosEnero));
  }
}
