package com.ecoharmony.compraentradas.model;

import java.math.BigDecimal;

// Precios son ASUNCION PO — actualizar cuando el PO responda (ver decisiones-de-diseno.md)
public enum TipoPase {
  REGULAR(new BigDecimal("10000")),
  VIP(new BigDecimal("20000"));

  private final BigDecimal precio;

  TipoPase(BigDecimal precio) {
    this.precio = precio;
  }

  public BigDecimal getPrecio() {
    return precio;
  }
}
