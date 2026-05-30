package com.ecoharmony.compraentradas.controller;

import com.ecoharmony.compraentradas.dto.CompraRequest;
import com.ecoharmony.compraentradas.dto.CompraResponse;
import com.ecoharmony.compraentradas.model.FormaPago;
import com.ecoharmony.compraentradas.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

  private final CompraService compraService;

  public CompraController(CompraService compraService) {
    this.compraService = compraService;
  }

  @PostMapping
  public ResponseEntity<CompraResponse> crear(@RequestBody @Valid CompraRequest request) {
    CompraResponse response = compraService.crear(request);
    if (response.formaPago() == FormaPago.TARJETA) {
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.status(201).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CompraResponse> buscarPorId(@PathVariable Long id) {
    return ResponseEntity.ok(compraService.buscarPorId(id));
  }

  @PostMapping("/{id}/confirmar")
  public ResponseEntity<CompraResponse> confirmar(@PathVariable Long id) {
    return ResponseEntity.ok(compraService.confirmar(id));
  }
}
