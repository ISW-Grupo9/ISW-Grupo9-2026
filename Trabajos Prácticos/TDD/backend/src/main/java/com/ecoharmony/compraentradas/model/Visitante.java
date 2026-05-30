package com.ecoharmony.compraentradas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Visitante {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private int edad;

  @Enumerated(EnumType.STRING)
  private TipoPase tipoPase;

  public Visitante(String nombre, int edad, TipoPase tipoPase) {
    if (edad < 0) throw new IllegalArgumentException("La edad no puede ser negativa");
    if (tipoPase == null) throw new IllegalArgumentException("El tipo de pase es requerido");
    this.nombre = (nombre == null || nombre.isBlank()) ? null : nombre.trim();
    this.edad = edad;
    this.tipoPase = tipoPase;
  }
}
