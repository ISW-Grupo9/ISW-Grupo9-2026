package com.ecoharmony.compraentradas.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecoharmony.compraentradas.model.Compra;
import com.ecoharmony.compraentradas.model.FormaPago;
import com.ecoharmony.compraentradas.model.TipoPase;
import com.ecoharmony.compraentradas.model.Visitante;
import com.ecoharmony.compraentradas.repository.CompraRepository;
import com.ecoharmony.compraentradas.service.impl.PagoServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("PagoService")
class PagoServiceImplTest {

  @Autowired private CompraRepository compraRepository;

  private PagoService pagoService;

  private static final BigDecimal MONTO = new BigDecimal("5000");

  @BeforeEach
  void setUp() {
    pagoService = new PagoServiceImpl();
  }

  private Compra compraGuardada(FormaPago formaPago) {
    Visitante v = new Visitante("Ana", 25, TipoPase.REGULAR);
    Compra compra = new Compra(1L, LocalDate.now().plusDays(1), List.of(v), formaPago, MONTO);
    return compraRepository.save(compra);
  }

  // ─── Ciclo 10.1 ───────────────────────────────────────────────────────────
  // Para efectivo no se genera URL: el pago es presencial en boletería.
  @Test
  @DisplayName("pago efectivo retorna null (sin URL, se paga en boletería)")
  void pago_efectivo_retorna_null() {
    Compra compra = compraGuardada(FormaPago.EFECTIVO);
    assertThat(pagoService.generarUrlPago(compra)).isNull();
  }

  // ─── Ciclo 10.2 ───────────────────────────────────────────────────────────
  // La URL debe incluir el monto para que Mercado Pago pueda mostrarlo al usuario.
  @Test
  @DisplayName("URL de Mercado Pago contiene el monto total")
  void url_mercado_pago_contiene_el_monto_total() {
    Compra compra = compraGuardada(FormaPago.TARJETA);
    assertThat(pagoService.generarUrlPago(compra)).contains("5000");
  }

  // ─── Ciclo 10.4 ───────────────────────────────────────────────────────────
  // La URL incluye el ID de la compra para que el callback de MP identifique qué pago confirmar.
  @Test
  @DisplayName("URL de Mercado Pago contiene el ID de la compra")
  void url_mercado_pago_contiene_id_de_compra() {
    Compra compra = compraGuardada(FormaPago.TARJETA);
    assertThat(pagoService.generarUrlPago(compra)).contains(compra.getId().toString());
  }
}
