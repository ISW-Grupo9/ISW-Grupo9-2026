package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.dto.CompraRequest;
import com.ecoharmony.compraentradas.dto.CompraResponse;
import com.ecoharmony.compraentradas.model.*;
import com.ecoharmony.compraentradas.repository.CompraRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class CompraServiceImpl implements CompraService {

  private final ValidadorFechaService validadorFecha;
  private final ValidadorCantidadService validadorCantidad;
  private final ValidadorVisitantesService validadorVisitantes;
  private final ValidadorUsuarioService validadorUsuario;
  private final ValidadorFormaPagoService validadorFormaPago;
  private final CalculadorPrecioService calculadorPrecio;
  private final PagoService pagoService;
  private final EmailService emailService;
  private final CompraRepository compraRepository;

  public CompraServiceImpl(
      ValidadorFechaService validadorFecha,
      ValidadorCantidadService validadorCantidad,
      ValidadorVisitantesService validadorVisitantes,
      ValidadorUsuarioService validadorUsuario,
      ValidadorFormaPagoService validadorFormaPago,
      CalculadorPrecioService calculadorPrecio,
      PagoService pagoService,
      EmailService emailService,
      CompraRepository compraRepository) {
    this.validadorFecha = validadorFecha;
    this.validadorCantidad = validadorCantidad;
    this.validadorVisitantes = validadorVisitantes;
    this.validadorUsuario = validadorUsuario;
    this.validadorFormaPago = validadorFormaPago;
    this.calculadorPrecio = calculadorPrecio;
    this.pagoService = pagoService;
    this.emailService = emailService;
    this.compraRepository = compraRepository;
  }

  @Override
  public CompraResponse crear(CompraRequest request) {
    int cantidad = request.visitantes().size();

    validadorFecha.validar(request.fechaVisita());
    validadorCantidad.validar(cantidad);
    validadorVisitantes.validar(cantidad, request.visitantes());
    validadorUsuario.validar(request.usuarioId());
    validadorFormaPago.validar(request.formaPago());

    BigDecimal total = calculadorPrecio.calcularTotal(request.visitantes());

    List<Visitante> visitantes =
        request.visitantes().stream()
            .map(dto -> new Visitante(dto.nombre(), dto.edad(), dto.tipoPase()))
            .toList();

    Compra compra =
        new Compra(
            request.usuarioId(), request.fechaVisita(), visitantes, request.formaPago(), total);

    if (request.formaPago() == FormaPago.EFECTIVO) {
      compra.pendienteBoleteria();
    }

    Compra saved = compraRepository.save(compra);
    String urlPago = pagoService.generarUrlPago(saved);
    emailService.enviarConfirmacion(saved);

    return toResponse(saved, urlPago);
  }

  @Override
  public CompraResponse buscarPorId(Long id) {
    Compra compra =
        compraRepository
            .findById(id)
            .orElseThrow(() -> new NoSuchElementException("Compra no encontrada: " + id));
    return toResponse(compra, null);
  }

  @Override
  public CompraResponse confirmar(Long id) {
    Compra compra =
        compraRepository
            .findById(id)
            .orElseThrow(() -> new NoSuchElementException("Compra no encontrada: " + id));
    compra.confirmar();
    Compra saved = compraRepository.save(compra);
    emailService.enviarConfirmacion(saved);
    return toResponse(saved, null);
  }

  private CompraResponse toResponse(Compra compra, String urlPago) {
    return new CompraResponse(
        compra.getId(),
        compra.getFechaVisita(),
        compra.getVisitantes().size(),
        compra.getMontoTotal(),
        compra.getFormaPago(),
        compra.getEstado(),
        urlPago);
  }
}
