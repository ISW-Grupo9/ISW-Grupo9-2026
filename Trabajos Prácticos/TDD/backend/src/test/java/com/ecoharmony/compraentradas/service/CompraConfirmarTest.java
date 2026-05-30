package com.ecoharmony.compraentradas.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.ecoharmony.compraentradas.dto.CompraResponse;
import com.ecoharmony.compraentradas.model.*;
import com.ecoharmony.compraentradas.repository.CompraRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompraService — confirmar pago")
class CompraConfirmarTest {

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

  private Compra compraPendiente() {
    Visitante v = new Visitante("Ana", 25, TipoPase.REGULAR);
    Compra c =
        new Compra(
            1L, LocalDate.of(2026, 6, 15), List.of(v), FormaPago.TARJETA, new BigDecimal("10000"));
    return c;
  }

  @BeforeEach
  void setUp() {
    compraService =
        new CompraServiceImpl(
            validadorFecha,
            validadorCantidad,
            validadorVisitantes,
            validadorUsuario,
            validadorFormaPago,
            calculadorPrecio,
            pagoService,
            emailService,
            compraRepository);
  }

  // ─── Confirmar 1 ──────────────────────────────────────────────────────────
  @Test
  @DisplayName("debe confirmar la compra y cambiar su estado a CONFIRMADA")
  void debe_confirmar_compra_y_cambiar_estado() {
    Compra compra = compraPendiente();
    when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));
    when(compraRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    CompraResponse response = compraService.confirmar(1L);

    assertThat(response.estado()).isEqualTo(EstadoCompra.CONFIRMADA);
  }

  // ─── Confirmar 2 ──────────────────────────────────────────────────────────
  @Test
  @DisplayName("debe lanzar excepción si la compra no existe")
  void debe_fallar_si_compra_no_existe() {
    when(compraRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> compraService.confirmar(99L))
        .isInstanceOf(NoSuchElementException.class);
  }
}
