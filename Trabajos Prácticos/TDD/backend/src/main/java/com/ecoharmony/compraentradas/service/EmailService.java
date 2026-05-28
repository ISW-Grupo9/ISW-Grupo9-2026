package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.model.Compra;

public interface EmailService {
    void enviarConfirmacion(Compra compra);
}
