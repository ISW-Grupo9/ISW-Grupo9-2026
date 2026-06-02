package com.ecoharmony.compraentradas.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecoharmony.compraentradas.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ParqueController.class)
@Import(GlobalExceptionHandler.class)
@TestPropertySource(properties = "ecoharmony.parque.dias-cerrados=MONDAY")
@DisplayName("ParqueController")
class ParqueControllerTest {

  @Autowired MockMvc mockMvc;

  // ─── Ciclo P.1 ────────────────────────────────────────────────────────────
  @Test
  @DisplayName("GET /api/parque/reglas retorna 200")
  void get_reglas_retorna_ok() throws Exception {
    mockMvc.perform(get("/api/parque/reglas")).andExpect(status().isOk());
  }

  // ─── Ciclo P.2 ────────────────────────────────────────────────────────────
  @Test
  @DisplayName("GET /api/parque/reglas retorna diasCerrados configurados")
  void get_reglas_retorna_dias_cerrados() throws Exception {
    mockMvc
        .perform(get("/api/parque/reglas"))
        .andExpect(jsonPath("$.diasCerrados").isArray())
        .andExpect(jsonPath("$.diasCerrados", hasItem("MONDAY")));
  }

  // ─── Ciclo P.3 ────────────────────────────────────────────────────────────
  @Test
  @DisplayName("GET /api/parque/reglas retorna feriados fijos 25/12 y 01/01")
  void get_reglas_retorna_feriados_fijos() throws Exception {
    mockMvc
        .perform(get("/api/parque/reglas"))
        .andExpect(jsonPath("$.feriadosFijos").isArray())
        .andExpect(jsonPath("$.feriadosFijos", hasItem("12-25")))
        .andExpect(jsonPath("$.feriadosFijos", hasItem("01-01")));
  }
}
