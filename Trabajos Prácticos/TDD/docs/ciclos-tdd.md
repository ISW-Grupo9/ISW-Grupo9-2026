# Ciclos TDD — Comprar Entradas (US #8)

Organización: del test más granular al más completo.
Cada ciclo sigue: RED → GREEN → REFACTOR.

**Estado general:** ✅ Todos los ciclos implementados — 148 tests GREEN (69 backend / 79 frontend)

---

## BACKEND

### Ciclo 1 — Entidad `Visitante` (modelo) ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 1.1 | `debe_crearse_con_nombre_edad_y_tipo_pase` | constructor válido |
| 1.2 | `debe_rechazar_edad_negativa` | edad < 0 lanza excepción |
| 1.3 | `debe_rechazar_edad_cero` | edad = 0 lanza excepción |
| 1.4 | `debe_rechazar_nombre_nulo` | nombre null lanza excepción |
| 1.5 | `debe_rechazar_nombre_vacio` | nombre "" lanza excepción |

---

### Ciclo 2 — Enum `TipoPase` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 2.1 | `REGULAR_debe_tener_precio_configurado` | precio > 0 |
| 2.2 | `VIP_debe_tener_precio_configurado` | precio > 0 |
| 2.3 | `VIP_debe_costar_mas_que_REGULAR` | precio VIP > precio REGULAR |

---

### Ciclo 3 — Entidad `Compra` (modelo) ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 3.1 | `debe_crearse_con_estado_PENDIENTE` | estado inicial = PENDIENTE |
| 3.2 | `debe_asociar_usuario_al_crearse` | compra.getUsuario() no null |
| 3.3 | `debe_asociar_lista_de_visitantes` | compra.getVisitantes() size correcto |
| 3.4 | `debe_almacenar_fecha_de_visita` | compra.getFechaVisita() = fecha ingresada |
| 3.5 | `debe_almacenar_forma_de_pago` | compra.getFormaPago() = forma ingresada |

---

### Ciclo 4 — `ValidadorFechaService` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 4.1 | `debe_aceptar_fecha_igual_a_hoy` | fecha = LocalDate.now() → válida |
| 4.2 | `debe_aceptar_fecha_futura` | fecha = hoy + 7 días → válida |
| 4.3 | `debe_rechazar_fecha_pasada` | fecha = ayer → lanza FechaInvalidaException |
| 4.4 | `debe_rechazar_lunes_si_parque_cerrado_ese_dia` | lunes → lanza ParqueCerradoException |
| 4.5 | `debe_aceptar_martes_como_dia_habil` | martes → válido |
| 4.6 | `debe_aceptar_domingo_como_dia_habil` | domingo → válido |

---

### Ciclo 4-bis — `ValidadorFechaService` — Feriados fijos ✅
> Agregado tras respuesta del PO: parque cerrado el 25/12 y 01/01

| # | Test | Qué verifica |
|---|------|-------------|
| 4b.1 | `debe_rechazar_navidad` | 25/12 → lanza ParqueCerradoException ("feriado") |
| 4b.2 | `debe_rechazar_anio_nuevo` | 01/01 → lanza ParqueCerradoException ("feriado") |
| 4b.3 | `debe_aceptar_vispera_navidad` | 24/12 → válido |
| 4b.4 | `debe_aceptar_dos_de_enero` | 02/01 → válido |

---

### Ciclo 5 — `ValidadorCantidadService` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 5.1 | `debe_aceptar_cantidad_1` | cantidad = 1 → válida |
| 5.2 | `debe_aceptar_cantidad_10` | cantidad = 10 → válida (límite exacto) |
| 5.3 | `debe_rechazar_cantidad_0` | cantidad = 0 → lanza CantidadInvalidaException |
| 5.4 | `debe_rechazar_cantidad_negativa` | cantidad = -1 → lanza CantidadInvalidaException |
| 5.5 | `debe_rechazar_cantidad_11` | cantidad = 11 → lanza CantidadInvalidaException |

---

### Ciclo 6 — `ValidadorVisitantesService` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 6.1 | `debe_aceptar_cuando_cantidad_coincide` | 3 entradas = 3 visitantes |
| 6.2 | `debe_rechazar_mas_visitantes_que_entradas` | 3 entradas, 4 visitantes → excepción |
| 6.3 | `debe_rechazar_menos_visitantes_que_entradas` | 3 entradas, 2 visitantes → excepción |
| 6.4 | `debe_rechazar_lista_null` | null → excepción |
| 6.5 | `debe_rechazar_lista_vacia` | [] → excepción |

---

### Ciclo 7 — `CalculadorPrecioService` (precio base) ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 7.1 | `debe_calcular_precio_1_regular` | 1 × $10.000 = $10.000 |
| 7.2 | `debe_calcular_precio_1_vip` | 1 × $20.000 = $20.000 |
| 7.3 | `debe_calcular_precio_mix_regular_y_vip` | 2 REGULAR + 1 VIP = $40.000 |

---

### Ciclo 7-bis — `CalculadorPrecioService` — Descuentos por edad ✅
> Agregado tras respuesta del PO: descuentos por edad confirmados

| # | Test | Qué verifica |
|---|------|-------------|
| 7b.1 | `visitante_3_anios_no_paga` | ≤ 3 años → $0 (gratis) |
| 7b.2 | `visitante_15_anios_paga_mitad_regular` | ≤ 15 años, REGULAR → $5.000 |
| 7b.3 | `visitante_60_anios_paga_mitad_regular` | ≥ 60 años, REGULAR → $5.000 |
| 7b.4 | `visitante_15_anios_paga_mitad_vip` | ≤ 15 años, VIP → $10.000 |
| 7b.5 | `visitante_adulto_paga_precio_completo` | 16–59 años → precio completo |
| 7b.6 | `total_correcto_para_grupo_mixto` | bebé + niño + adulto + jubilado → $20.000 |

---

### Ciclo 8 — `ValidadorUsuarioService` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 8.1 | `debe_aceptar_usuario_registrado` | usuario existe en repo → válido |
| 8.2 | `debe_rechazar_usuario_no_registrado` | usuario no existe → lanza UsuarioNoRegistradoException |
| 8.3 | `debe_rechazar_id_nulo` | null → lanza UsuarioNoRegistradoException |

---

### Ciclo 9 — `ValidadorFormaPagoService` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 9.1 | `debe_aceptar_forma_pago_valida` | valor no nulo → válido |
| 9.2 | `debe_rechazar_forma_pago_null` | null → lanza FormaPagoRequeridaException |

---

### Ciclo 10 — `PagoService` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 10.1 | `pago_efectivo_retorna_null` | EFECTIVO → null (sin URL, pago en boletería) |
| 10.2 | `url_mercado_pago_contiene_el_monto_total` | TARJETA → URL contiene el monto |
| 10.3 | `url_mercado_pago_contiene_id_de_compra` | TARJETA → URL contiene el ID de la compra |

---

### Ciclo 11 — `EmailService` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 11.1 | `debe_enviar_mail_al_finalizar_compra_efectivo` | estado PENDIENTE_BOLETERIA → mailSender.send() llamado |
| 11.2 | `debe_enviar_mail_al_finalizar_compra_tarjeta` | estado CONFIRMADA → mailSender.send() llamado |
| 11.3 | `asunto_incluye_numero_de_orden_y_parque` | asunto contiene "EcoHarmony Park" |
| 11.4 | `mail_debe_enviarse_al_email_del_usuario_registrado` | destinatario = email del usuario en BD |
| 11.5 | `no_debe_enviar_mail_si_compra_no_finalizo` | estado PENDIENTE → no envía |

---

### Ciclo 12 — `CompraService` (orquestación completa) ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 12.1 | `debe_crear_compra_exitosa_con_pago_efectivo` | flujo OK, estado PENDIENTE_BOLETERIA, sin URL |
| 12.2 | `debe_crear_compra_exitosa_con_pago_tarjeta` | flujo OK, retorna URL de MP |
| 12.3 | `debe_propagar_excepcion_del_validador_fecha` | excepción de fecha se propaga |
| 12.4 | `debe_fallar_si_cantidad_supera_10` | delega a ValidadorCantidad, propaga excepción |
| 12.5 | `debe_fallar_si_visitantes_no_coinciden_con_entradas` | delega a ValidadorVisitantes |
| 12.6 | `debe_fallar_si_usuario_no_registrado` | delega a ValidadorUsuario |
| 12.7 | `debe_fallar_sin_forma_de_pago` | delega a ValidadorFormaPago |
| 12.8 | `debe_persistir_compra_en_repositorio` | compraRepository.save() fue llamado |
| 12.9 | `debe_enviar_mail_despues_de_persistir` | orden: save → enviarConfirmacion |

---

### Ciclo 13 — `CompraController` (integración HTTP) ✅

| # | Test | HTTP | Qué verifica |
|---|------|------|-------------|
| 13.1 | `post_retorna_201_con_pago_efectivo` | POST | 201 Created, estado PENDIENTE_BOLETERIA |
| 13.2 | `post_retorna_200_con_url_mp_pago_tarjeta` | POST | 200 OK, urlPago en body |
| 13.3 | `post_retorna_400_si_validacion_falla` | POST | 400 + campo "error" en body |
| 13.4 | *(fuera de scope: autenticación corresponde a otra US)* | POST | 401 sin autenticación |
| 13.5 | `get_retorna_200_con_compra_existente` | GET | 200, id y cantidadEntradas en body |
| 13.6 | `get_retorna_404_si_compra_no_existe` | GET | 404 |

---

## FRONTEND

### Ciclo 14 — Funciones puras / utils ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 14.1 | `formatearFecha formatea a dd/mm/yyyy` | "2026-06-15" → "15/06/2026" |
| 14.2 | `formatearPrecio formatea con signo pesos` | 5000 → "$5.000" |
| 14.3 | `calcularTotal una entrada regular` | 1 REGULAR → $10.000 |
| 14.4 | `calcularTotal una entrada VIP` | 1 VIP → $20.000 |
| 14.5 | `calcularTotal mix de tipos` | 2 REGULAR + 1 VIP → $40.000 |
| 14.6 | `esFechaValida rechaza fecha pasada` | "2020-01-01" → false |
| 14.7 | `esFechaValida acepta hoy` | hoy → true |
| 14.8 | `esFechaValida acepta fecha futura` | hoy + 7 → true |
| 14.9 | `esCantidadValida rechaza 0` | 0 → false |
| 14.10 | `esCantidadValida rechaza 11` | 11 → false |
| 14.11 | `esCantidadValida acepta límite inferior` | 1 → true |
| 14.12 | `esCantidadValida acepta límite superior` | 10 → true |

**Ciclo 14-bis — `precioVisitante` (descuentos por edad)**

| # | Test | Qué verifica |
|---|------|-------------|
| 14b.1 | `≤ 3 años → gratis` | edad 3 → 0 |
| 14b.2 | `≤ 15 años → 50% del precio base` | edad 15, REGULAR → $5.000 |
| 14b.3 | `≥ 60 años → 50% del precio base` | edad 60, REGULAR → $5.000 |
| 14b.4 | `adulto → precio completo` | edad 30 → $10.000 |
| 14b.5 | `descuento aplica a VIP` | edad 10, VIP → $10.000 |

---

### Ciclo 15 — Hook `useCompraForm` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 15.1 | `inicializa con estado vacío` | fecha null, visitantes [], formaPago null |
| 15.2 | `actualiza fecha correctamente` | setFecha → state.fecha cambia |
| 15.3 | `agrega visitantes al aumentar cantidad` | setCantidad(3) → visitantes.length = 3 |
| 15.4 | `remueve visitantes al disminuir cantidad` | de 3 a 2 → visitantes.length = 2 |
| 15.5 | `actualiza edad de visitante por índice` | setEdadVisitante(0, 25) → visitantes[0].edad = 25 |
| 15.6 | `actualiza tipo de pase por índice` | setTipoPase(0, VIP) → visitantes[0].tipo = VIP |
| 15.7 | `recalcula total al cambiar tipo de pase` | REGULAR → $10.000, VIP → $20.000 |
| 15.8 | `muestra error si fecha pasada` | setFecha("2020-01-01") → errors.fecha definido |
| 15.9 | `muestra error si cantidad supera 10` | setCantidad(11) → errors.cantidad definido |
| 15.10 | `limpia error al corregir dato inválido` | setCantidad(11) → (5) → error limpiado |
| 15.11 | `isValid cuando todos los datos son correctos` | fecha + cantidad + formaPago → true |

---

### Ciclo 16 — Componente `SeleccionFecha` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 16.1 | `renderiza input de tipo date` | input[type=date] en DOM |
| 16.2 | `llama onChange con fecha seleccionada` | user types → onChange called |
| 16.3 | `muestra mensaje de error si hay prop error` | prop error → alerta visible |
| 16.4 | `no muestra error si no hay prop error` | sin prop error → sin alerta |
| 16.5 | `deshabilita fechas pasadas con min attribute` | input.min = hoy |

---

### Ciclo 17 — Componente `SeleccionEntradas` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 17.1 | `renderiza selector de cantidad` | select de cantidad en DOM |
| 17.2 | `renderiza N filas de visitante` | cantidad=3 → 3 filas |
| 17.3 | `cada fila tiene campo de edad` | input edad por visitante |
| 17.4 | `cada fila tiene selector de tipo de pase` | select VIP/REGULAR por visitante |
| 17.5 | `muestra error si hay prop error` | prop error → alerta visible |
| 17.6 | `llama onCantidadChange al cambiar cantidad` | user changes → callback called |
| 17.7 | `llama onVisitanteChange al editar edad` | user types age → callback |
| 17.8 | `llama onVisitanteChange al cambiar tipo pase` | user selects VIP → callback |

---

### Ciclo 18 — Componente `ResumenCompra` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 18.1 | `muestra cantidad de entradas` | "3 entradas" visible |
| 18.2 | `muestra fecha formateada` | "15/06/2026" visible |
| 18.3 | `muestra monto total` | "$40.000" visible |
| 18.4 | `muestra desglose por tipo de pase` | "2 regular + 1 VIP" visible |

---

### Ciclo 19 — Componente `SeleccionPago` ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 19.1 | `renderiza opción efectivo` | botón "Efectivo" en DOM |
| 19.2 | `renderiza opción tarjeta` | botón "Tarjeta" en DOM |
| 19.3 | `llama onPagoSeleccionado con EFECTIVO` | click efectivo → callback |
| 19.4 | `llama onPagoSeleccionado con TARJETA` | click tarjeta → callback |
| 19.5 | `muestra error si no se seleccionó pago` | prop error → alerta visible |
| 19.6 | `resalta opción seleccionada` | selectedPago=EFECTIVO → clase "activo" |

---

### Ciclo 20 — Hook `useSubmitCompra` (integración con API via MSW) ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 20.1 | `envía datos correctos a POST /api/compras` | request body matchea payload esperado |
| 20.2 | `isLoading true mientras hace fetch` | isLoading = true durante request |
| 20.3 | `pago efectivo → isSuccess true` | isSuccess = true, estado PENDIENTE_BOLETERIA |
| 20.4 | `pago tarjeta → llama a redirect con URL de MP` | redirect("https://mercadopago.com/...") |
| 20.5 | `error 400 → muestra mensaje de error` | error.message = mensaje del servidor |
| 20.6 | `error 401 → redirige a /login` | navigate("/login") |
| 20.7 | `error de red → muestra error genérico` | error = "Error de conexión" |

---

### Ciclo 21 — Flujo completo (`CompraPage` + MSW) ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 21.1 | `compra exitosa con efectivo` | fecha → entradas → pago → confirmación visible |
| 21.2 | `compra exitosa con tarjeta` | fecha → entradas → tarjeta → redirect a MP |
| 21.3 | `error de servidor → muestra mensaje` | 400 del server → error-global visible, no avanza |
| 21.4 | `submit sin forma de pago → error cliente` | sin pago → alerta, no envía request |
| 21.5 | `confirmación con efectivo muestra botón volver` | link "volver" visible en confirmación |
| 21.6 | `error 401 → redirige a /login` | MSW retorna 401 → navigate("/login") |
| 21.7 | `submit con fecha en el pasado no envía request` | fecha pasada → no fetch |
| 21.8 | `submit sin visitantes muestra error` | sin cantidad → alerta visible |

---

### Ciclo 22 — `MercadoPagoPage` (simulación MP) ✅

| # | Test | Qué verifica |
|---|------|-------------|
| 22.1 | `muestra el monto formateado` | monto en query param → visible formateado |
| 22.2 | `muestra el botón Pagar` | button "Pagar" en DOM |
| 22.3 | `muestra estado de carga al hacer click` | click → "Procesando..." visible |
| 22.4 | `muestra ¡Pago exitoso! después de confirmar` | POST confirmar OK → pago-exitoso visible |
| 22.5 | `muestra error si la confirmación falla` | POST 500 → role="alert" visible |

---

## Resumen de ciclos

| Capa | Ciclos | Tests |
|------|--------|-------|
| Modelo (backend) | 1–3 | 13 |
| Servicios de validación | 4, 4-bis, 5, 6 | 15 |
| Servicios de cálculo | 7, 7-bis | 9 |
| Servicios de negocio | 8, 9, 10, 11, 12 | 22 |
| Controller (integración HTTP) | 13 | 10 |
| **Total backend** | | **69** |
| Utils / funciones puras | 14, 14-bis | 17 |
| Hooks | 15, 20 | 18 |
| Componentes | 16–19, 22 | 28 |
| Flujo completo | 21 | 8 |
| Hooks de negocio | 20 | 7 |
| **Total frontend** | | **79** |
| **TOTAL** | **24 ciclos** | **148** |

---

## Registro de cambios

| Fecha | Cambio |
|---|---|
| 2026-05-27 | Documento creado — plan inicial de 21 ciclos (~126 tests) |
| 2026-05-27 | Ciclos 1–13 backend implementados |
| 2026-05-27 | Ciclos 14–21 frontend implementados |
| 2026-05-28 | Respuesta del PO: precios actualizados (REGULAR $10.000 / VIP $20.000) |
| 2026-05-28 | Ciclo 4-bis agregado: feriados fijos 25/12 y 01/01 |
| 2026-05-28 | Ciclo 7-bis agregado: descuentos por edad (≤3 gratis, ≤15 y ≥60 → 50% off) |
| 2026-05-28 | Limpieza de tests: eliminados 13 tests redundantes sin pérdida de cobertura |
| 2026-05-28 | Ciclo 22 agregado: MercadoPagoPage (simulación de Mercado Pago) |
| 2026-05-28 | EmailService reescrito: HTML con QR ZXing, tabla de visitantes, subtotales, fecha dd/MM/yyyy |
| 2026-05-28 | Ciclo 13.4 marcado fuera de scope: autenticación es responsabilidad de otra US |
| 2026-05-28 | Conteos actualizados: 148 tests (69 backend / 79 frontend) |
