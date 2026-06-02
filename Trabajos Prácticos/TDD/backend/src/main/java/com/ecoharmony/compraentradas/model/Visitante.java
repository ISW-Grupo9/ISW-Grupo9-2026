package com.ecoharmony.compraentradas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Visitante {

  private static final int MAX_EDAD = 120;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private int edad;

  @Enumerated(EnumType.STRING)
  private TipoPase tipoPase;

  public Visitante(String nombre, int edad, TipoPase tipoPase) {
    if (edad < 0 || edad > MAX_EDAD) {
      throw new IllegalArgumentException("La edad debe estar entre 0 y " + MAX_EDAD);
    }
    if (tipoPase == null) {
      throw new IllegalArgumentException("El tipo de pase es requerido");
    }
    this.nombre = (nombre == null || nombre.isBlank()) ? null : nombre.trim();
    this.edad = edad;
    this.tipoPase = tipoPase;
  }
}
