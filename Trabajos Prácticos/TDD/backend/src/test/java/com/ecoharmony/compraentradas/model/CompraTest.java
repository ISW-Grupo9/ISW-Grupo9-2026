package com.ecoharmony.compraentradas.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Compra")
class CompraTest {

    // ─── Helper ───────────────────────────────────────────────────────────────
    private Compra compraValida() {
        var visitantes = List.of(new Visitante("Ana García", 28, TipoPase.REGULAR));
        return new Compra(1L, LocalDate.now().plusDays(3), visitantes,
                FormaPago.EFECTIVO, new BigDecimal("5000"));
    }

    // ─── Ciclo 3.1 ────────────────────────────────────────────────────────────
    // Verifica que toda compra nace con estado PENDIENTE.
    // Una compra recién creada todavía no fue procesada ni confirmada.
    @Test
    @DisplayName("debe crearse con estado PENDIENTE")
    void debe_crearse_con_estado_PENDIENTE() {
        var compra = compraValida();

        assertThat(compra.getEstado()).isEqualTo(EstadoCompra.PENDIENTE);
    }

    // ─── Ciclo 3.2 ────────────────────────────────────────────────────────────
    // Verifica que la compra almacena el id del usuario que la realizó.
    // Necesario para enviar el mail de confirmación y para auditoría.
    @Test
    @DisplayName("debe asociar el usuario al crearse")
    void debe_asociar_usuario_al_crearse() {
        var compra = compraValida();

        assertThat(compra.getUsuarioId()).isEqualTo(1L);
    }

    // ─── Ciclo 3.3 ────────────────────────────────────────────────────────────
    // Verifica que la compra almacena la lista de visitantes completa.
    // Cada visitante corresponde a una entrada — la cantidad debe coincidir.
    @Test
    @DisplayName("debe asociar la lista de visitantes")
    void debe_asociar_lista_de_visitantes() {
        var visitantes = List.of(
                new Visitante("Ana García", 28, TipoPase.REGULAR),
                new Visitante("Luis Pérez", 35, TipoPase.VIP)
        );
        var compra = new Compra(1L, LocalDate.now().plusDays(3), visitantes,
                FormaPago.EFECTIVO, new BigDecimal("13000"));

        assertThat(compra.getVisitantes()).hasSize(2);
    }

    // ─── Ciclo 3.4 ────────────────────────────────────────────────────────────
    // Verifica que la fecha de visita ingresada queda guardada en la compra.
    // Es la fecha que se muestra en la confirmación y se imprime en la entrada.
    @Test
    @DisplayName("debe almacenar la fecha de visita")
    void debe_almacenar_fecha_de_visita() {
        var fechaEsperada = LocalDate.now().plusDays(5);
        var visitantes = List.of(new Visitante("Ana García", 28, TipoPase.REGULAR));
        var compra = new Compra(1L, fechaEsperada, visitantes,
                FormaPago.EFECTIVO, new BigDecimal("5000"));

        assertThat(compra.getFechaVisita()).isEqualTo(fechaEsperada);
    }

    // ─── Ciclo 3.5 ────────────────────────────────────────────────────────────
    // Verifica que la forma de pago elegida queda registrada en la compra.
    // Determina si se genera URL de Mercado Pago o se reserva para boletería.
    @Test
    @DisplayName("debe almacenar la forma de pago")
    void debe_almacenar_forma_de_pago() {
        var compra = compraValida();

        assertThat(compra.getFormaPago()).isEqualTo(FormaPago.EFECTIVO);
    }

    // ─── Ciclo 3.6 ────────────────────────────────────────────────────────────
    // Verifica que el monto total calculado queda persistido en la compra.
    // Es el valor que se muestra en el resumen y se envía a Mercado Pago.
    @Test
    @DisplayName("debe almacenar el monto total")
    void debe_almacenar_monto_total() {
        var compra = compraValida();

        assertThat(compra.getMontoTotal()).isEqualByComparingTo(new BigDecimal("5000"));
    }
}
