package com.ecoharmony.compraentradas.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Compra {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long usuarioId;
  private LocalDate fechaVisita;

  @OneToMany(cascade = CascadeType.ALL)
  private List<Visitante> visitantes;

  @Enumerated(EnumType.STRING)
  private FormaPago formaPago;

  @Enumerated(EnumType.STRING)
  private EstadoCompra estado;

  private BigDecimal montoTotal;

  public Compra(
      Long usuarioId,
      LocalDate fechaVisita,
      List<Visitante> visitantes,
      FormaPago formaPago,
      BigDecimal montoTotal) {
    this.usuarioId = usuarioId;
    this.fechaVisita = fechaVisita;
    this.visitantes = visitantes;
    this.formaPago = formaPago;
    this.montoTotal = montoTotal;
    this.estado = EstadoCompra.PENDIENTE;
  }

  public void confirmar() {
    this.estado = EstadoCompra.CONFIRMADA;
  }

  public void pendienteBoleteria() {
    this.estado = EstadoCompra.PENDIENTE_BOLETERIA;
  }
}
