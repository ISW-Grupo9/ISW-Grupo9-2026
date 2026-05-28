package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.model.Compra;

public interface PagoService {
    String generarUrlPago(Compra compra);
}
