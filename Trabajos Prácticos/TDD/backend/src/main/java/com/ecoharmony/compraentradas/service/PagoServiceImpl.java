package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.model.Compra;
import com.ecoharmony.compraentradas.model.FormaPago;
import org.springframework.stereotype.Service;

@Service
public class PagoServiceImpl implements PagoService {

    private static final String BASE_URL = "/pago/simulado";

    @Override
    public String generarUrlPago(Compra compra) {
        if (compra.getFormaPago() == FormaPago.EFECTIVO) {
            return null;
        }
        return BASE_URL + "?compraId=" + compra.getId() + "&monto=" + compra.getMontoTotal();
    }
}
