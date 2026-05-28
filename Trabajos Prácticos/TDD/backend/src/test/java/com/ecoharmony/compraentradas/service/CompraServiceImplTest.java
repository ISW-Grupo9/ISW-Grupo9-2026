package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.dto.CompraRequest;
import com.ecoharmony.compraentradas.dto.CompraResponse;
import com.ecoharmony.compraentradas.dto.VisitanteDto;
import com.ecoharmony.compraentradas.exception.*;
import com.ecoharmony.compraentradas.model.*;
import com.ecoharmony.compraentradas.repository.CompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompraService")
class CompraServiceImplTest {

    @Mock private ValidadorFechaService validadorFecha;
    @Mock private ValidadorCantidadService validadorCantidad;
    @Mock private ValidadorVisitantesService validadorVisitantes;
    @Mock private ValidadorUsuarioService validadorUsuario;
    @Mock private ValidadorFormaPagoService validadorFormaPago;
    @Mock private CalculadorPrecioService calculadorPrecio;
    @Mock private PagoService pagoService;
    @Mock private EmailService emailService;
    @Mock private CompraRepository compraRepository;

    private CompraService compraService;

    private static final LocalDate FECHA_FUTURA = LocalDate.of(2026, 6, 15);
    private static final VisitanteDto VISITANTE_DTO = new VisitanteDto("Ana", 25, TipoPase.REGULAR);
    private static final BigDecimal TOTAL = new BigDecimal("10000");

    @BeforeEach
    void setUp() {
        compraService = new CompraServiceImpl(
                validadorFecha, validadorCantidad, validadorVisitantes,
                validadorUsuario, validadorFormaPago, calculadorPrecio,
                pagoService, emailService, compraRepository);
    }

    private CompraRequest requestEfectivo() {
        return new CompraRequest(1L, FECHA_FUTURA, List.of(VISITANTE_DTO), FormaPago.EFECTIVO);
    }

    private CompraRequest requestTarjeta() {
        return new CompraRequest(1L, FECHA_FUTURA, List.of(VISITANTE_DTO), FormaPago.TARJETA);
    }

    private void stubbearDependenciasBase() {
        when(calculadorPrecio.calcularTotal(any())).thenReturn(TOTAL);
        when(compraRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    // ─── Ciclo 12.1 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("debe crear compra exitosa con pago efectivo")
    void debe_crear_compra_exitosa_con_pago_efectivo() {
        stubbearDependenciasBase();
        when(pagoService.generarUrlPago(any())).thenReturn(null);

        CompraResponse response = compraService.crear(requestEfectivo());

        assertThat(response).isNotNull();
        assertThat(response.estado()).isEqualTo(EstadoCompra.PENDIENTE_BOLETERIA);
        assertThat(response.urlPago()).isNull();
    }

    // ─── Ciclo 12.2 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("debe crear compra exitosa con pago tarjeta y retornar URL de MP")
    void debe_crear_compra_exitosa_con_pago_tarjeta() {
        stubbearDependenciasBase();
        when(pagoService.generarUrlPago(any())).thenReturn("https://mercadopago.com/mock");

        CompraResponse response = compraService.crear(requestTarjeta());

        assertThat(response.urlPago()).isEqualTo("https://mercadopago.com/mock");
    }

    // ─── Ciclo 12.3 ───────────────────────────────────────────────────────────
    // Cubre ambos casos de fecha inválida: la distinción entre FechaInvalidaException
    // y ParqueCerradoException ya está probada en el ciclo 4.
    @Test
    @DisplayName("debe propagar excepción del validador de fecha")
    void debe_propagar_excepcion_del_validador_fecha() {
        doThrow(new FechaInvalidaException("fecha anterior")).when(validadorFecha).validar(any());

        assertThatThrownBy(() -> compraService.crear(requestEfectivo()))
                .isInstanceOf(FechaInvalidaException.class);
    }

    // ─── Ciclo 12.8 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("debe persistir la compra en el repositorio")
    void debe_persistir_compra_en_repositorio() {
        stubbearDependenciasBase();

        compraService.crear(requestEfectivo());

        verify(compraRepository).save(any(Compra.class));
    }

}
