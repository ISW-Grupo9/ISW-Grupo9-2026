package com.ecoharmony.compraentradas.service;

import static org.assertj.core.api.Assertions.*;

import com.ecoharmony.compraentradas.exception.FechaInvalidaException;
import com.ecoharmony.compraentradas.exception.ParqueCerradoException;
import com.ecoharmony.compraentradas.service.impl.ValidadorFechaServiceImpl;
import java.time.*;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ValidadorFechaService")
class ValidadorFechaServiceImplTest {

  // Clock fijo: martes 10 de junio de 2026
  // Elimina la dependencia en LocalDate.now() — los tests son deterministas sin importar
  // qué día se corran.
  private static final LocalDate HOY = LocalDate.of(2026, 6, 10); // miércoles
  private static final LocalDate AYER = LocalDate.of(2026, 6, 9); // martes (pasado)
  private static final LocalDate LUNES = LocalDate.of(2026, 6, 15); // lunes futuro
  private static final LocalDate MARTES = LocalDate.of(2026, 6, 16); // martes futuro
  private static final LocalDate DOMINGO = LocalDate.of(2026, 6, 14); // domingo futuro

  private static final Clock CLOCK =
      Clock.fixed(HOY.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

  private ValidadorFechaService validador;

  @BeforeEach
  void setUp() {
    // ASUNCION PO: parque cerrado los lunes — actualizar cuando PO responda
    validador = new ValidadorFechaServiceImpl(Set.of(DayOfWeek.MONDAY), CLOCK);
  }

  // ─── Ciclo 4.1 ────────────────────────────────────────────────────────────
  // Verifica que el día actual es una fecha válida para visitar.
  // El visitante puede comprar entrada para el mismo día.
  @Test
  @DisplayName("debe aceptar fecha igual a hoy")
  void debe_aceptar_fecha_igual_a_hoy() {
    assertThatNoException().isThrownBy(() -> validador.validar(HOY));
  }

  // ─── Ciclo 4.2 ────────────────────────────────────────────────────────────
  // Verifica que una fecha en el futuro es válida.
  // El caso más común: el visitante planifica su visita con anticipación.
  @Test
  @DisplayName("debe aceptar fecha futura")
  void debe_aceptar_fecha_futura() {
    assertThatNoException().isThrownBy(() -> validador.validar(MARTES));
  }

  // ─── Ciclo 4.3 ────────────────────────────────────────────────────────────
  // Verifica que una fecha pasada es rechazada.
  // No tiene sentido comprar una entrada para un día que ya pasó.
  @Test
  @DisplayName("debe rechazar fecha pasada")
  void debe_rechazar_fecha_pasada() {
    assertThatThrownBy(() -> validador.validar(AYER))
        .isInstanceOf(FechaInvalidaException.class)
        .hasMessageContaining("anterior");
  }

  // ─── Ciclo 4.4 ────────────────────────────────────────────────────────────
  // Verifica que el lunes es rechazado porque el parque no abre ese día.
  // ASUNCION PO: cierra los lunes — cambiar Set.of(MONDAY) si PO define otros días.
  @Test
  @DisplayName("debe rechazar lunes si el parque cierra ese día")
  void debe_rechazar_lunes_si_parque_cerrado_ese_dia() {
    assertThatThrownBy(() -> validador.validar(LUNES))
        .isInstanceOf(ParqueCerradoException.class)
        .hasMessageContaining("MONDAY");
  }

  // ─── Ciclo 4.5 ────────────────────────────────────────────────────────────
  // Verifica que el domingo es un día hábil válido.
  // Confirma que el único día cerrado es el lunes, no el fin de semana.
  @Test
  @DisplayName("debe aceptar domingo como día hábil")
  void debe_aceptar_domingo_como_dia_habil() {
    assertThatNoException().isThrownBy(() -> validador.validar(DOMINGO));
  }
}
