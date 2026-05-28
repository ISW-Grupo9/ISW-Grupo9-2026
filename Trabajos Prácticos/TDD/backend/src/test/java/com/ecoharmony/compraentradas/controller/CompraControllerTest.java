package com.ecoharmony.compraentradas.controller;

import com.ecoharmony.compraentradas.dto.CompraRequest;
import com.ecoharmony.compraentradas.dto.CompraResponse;
import com.ecoharmony.compraentradas.dto.VisitanteDto;
import com.ecoharmony.compraentradas.exception.*;
import com.ecoharmony.compraentradas.model.*;
import com.ecoharmony.compraentradas.service.CompraService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompraController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("CompraController")
class CompraControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean  CompraService compraService;

    private ObjectMapper mapper;

    private static final LocalDate FECHA = LocalDate.of(2026, 6, 15);
    private static final VisitanteDto VISITANTE = new VisitanteDto("Ana", 25, TipoPase.REGULAR);

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    private String json(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }

    private CompraRequest requestEfectivo() {
        return new CompraRequest(1L, FECHA, List.of(VISITANTE), FormaPago.EFECTIVO);
    }

    private CompraRequest requestTarjeta() {
        return new CompraRequest(1L, FECHA, List.of(VISITANTE), FormaPago.TARJETA);
    }

    // ─── Ciclo 13.1 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("POST /api/compras retorna 201 con pago efectivo")
    void post_retorna_201_con_pago_efectivo() throws Exception {
        CompraResponse response = new CompraResponse(1L, FECHA, 1, new BigDecimal("10000"),
                FormaPago.EFECTIVO, EstadoCompra.PENDIENTE_BOLETERIA, null);
        when(compraService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requestEfectivo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE_BOLETERIA"));
    }

    // ─── Ciclo 13.2 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("POST /api/compras retorna 200 con URL de MP para pago tarjeta")
    void post_retorna_200_con_url_mp_pago_tarjeta() throws Exception {
        CompraResponse response = new CompraResponse(1L, FECHA, 1, new BigDecimal("10000"),
                FormaPago.TARJETA, EstadoCompra.PENDIENTE, "https://mercadopago.com/mock");
        when(compraService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requestTarjeta())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.urlPago").value("https://mercadopago.com/mock"));
    }

    // ─── Ciclo 13.3 ───────────────────────────────────────────────────────────
    // Verifica que cualquier excepción de negocio mapeada en GlobalExceptionHandler
    // retorna 400. La distinción entre tipos de excepción está probada en los ciclos
    // 4, 5, 6, etc. — no es responsabilidad del controller.
    @Test
    @DisplayName("POST /api/compras retorna 400 si la validación de negocio falla")
    void post_retorna_400_si_validacion_falla() throws Exception {
        when(compraService.crear(any())).thenThrow(new FechaInvalidaException("fecha anterior"));

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(requestEfectivo())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // ─── Ciclo 13.4 ───────────────────────────────────────────────────────────
    // TODO: requiere Spring Security — fuera del alcance de este TP
    // @Test void post_retorna_401_sin_autenticacion() { ... }

    // ─── Ciclo 13.5 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("GET /api/compras/{id} retorna 200 con la compra existente")
    void get_retorna_200_con_compra_existente() throws Exception {
        CompraResponse response = new CompraResponse(1L, FECHA, 2, new BigDecimal("20000"),
                FormaPago.EFECTIVO, EstadoCompra.PENDIENTE_BOLETERIA, null);
        when(compraService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/compras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cantidadEntradas").value(2));
    }

    // ─── Ciclo 13.6 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("GET /api/compras/{id} retorna 404 si la compra no existe")
    void get_retorna_404_si_compra_no_existe() throws Exception {
        when(compraService.buscarPorId(99L)).thenThrow(new NoSuchElementException("no encontrada"));

        mockMvc.perform(get("/api/compras/99"))
                .andExpect(status().isNotFound());
    }

    // ─── Ciclo 13.7 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("POST /api/compras/{id}/confirmar retorna 200 con estado CONFIRMADA")
    void post_confirmar_retorna_200_con_estado_confirmada() throws Exception {
        CompraResponse response = new CompraResponse(1L, FECHA, 1, new BigDecimal("10000"),
                FormaPago.TARJETA, EstadoCompra.CONFIRMADA, null);
        when(compraService.confirmar(1L)).thenReturn(response);

        mockMvc.perform(post("/api/compras/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    // ─── Ciclo 13.8 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("POST /api/compras/{id}/confirmar retorna 404 si la compra no existe")
    void post_confirmar_retorna_404_si_compra_no_existe() throws Exception {
        when(compraService.confirmar(99L)).thenThrow(new NoSuchElementException("no encontrada"));

        mockMvc.perform(post("/api/compras/99/confirmar"))
                .andExpect(status().isNotFound());
    }
}
