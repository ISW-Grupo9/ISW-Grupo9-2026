# EcoHarmony Park — Comprar Entradas

**TP6 TDD · Ingeniería de Software · Grupo 9 · 2026**
**User Story #8:** Como usuario registrado quiero comprar entradas al parque.

---

## Índice

1. [Stack tecnológico](#stack-tecnológico)
2. [Decisiones de diseño justificadas](#decisiones-de-diseño-justificadas)
3. [Arquitectura](#arquitectura)
4. [Ciclos TDD implementados](#ciclos-tdd-implementados)
5. [Reglas de negocio confirmadas por el PO](#reglas-de-negocio-confirmadas-por-el-po)
6. [Levantar el proyecto](#levantar-el-proyecto)
7. [Correr los tests](#correr-los-tests)
8. [Documentación adicional](#documentación-adicional)

---

## Stack tecnológico

| Capa | Tecnología | Justificación |
|---|---|---|
| Backend | Java 21 · Spring Boot 3.3 · Maven | Ecosistema maduro para REST + JPA |
| Base de datos | H2 en memoria | Elimina dependencia externa en desarrollo y tests; se reinicia con cada arranque (aceptado por PO) |
| Tests backend | JUnit 5 · Mockito · AssertJ · MockMvc | Stack canónico de TDD en Java; AssertJ mejora legibilidad de aserciones |
| Frontend | React 19 · TypeScript · Vite · Tailwind CSS 4 | TypeScript fuerza el contrato de tipos entre capas; Vite reduce tiempo de arranque |
| Tests frontend | Vitest · React Testing Library · MSW v2 | Vitest corre en el mismo proceso que Vite; RTL testea comportamiento visible, no implementación; MSW intercepta fetch sin modificar código de producción |
| Mail local | MailHog (Docker) | Simula SMTP sin enviar correos reales; visible en el browser |

---

## Decisiones de diseño justificadas

Esta sección documenta cada decisión técnica relevante, su motivación y su impacto directo en el código.

### 1. Separación en servicios validadores independientes

**Decisión:** Cada validación de negocio tiene su propia interfaz e implementación (`ValidadorFechaService`, `ValidadorCantidadService`, `ValidadorVisitantesService`, `ValidadorUsuarioService`, `ValidadorFormaPagoService`).

**Por qué:** Cada validador tiene exactamente una razón de cambio (Single Responsibility). Si el PO modifica la regla de fechas, solo cambia `ValidadorFechaServiceImpl` sin tocar el resto. Además, permite testear cada regla en aislamiento con mocks.

**Alternativa descartada:** Una clase `ValidadorCompraService` que centralice todo. Se descartó porque crece acoplada: agregar una nueva regla requiere modificar una clase ya probada y puede romper tests existentes.

**Evidencia en el código:**
- `service/ValidadorFechaService.java` + `service/impl/ValidadorFechaServiceImpl.java`
- `service/ValidadorCantidadService.java` + `service/impl/ValidadorCantidadServiceImpl.java`
- Tests: `ValidadorFechaServiceImplTest`, `ValidadorFechaFeriadosTest`, `ValidadorCantidadServiceImplTest`, etc.

---

### 2. Interfaz + implementación para todos los servicios

**Decisión:** Cada servicio expone una interfaz Java (`CompraService`, `CalculadorPrecioService`, etc.) con una implementación en el subpaquete `impl/`.

**Por qué:** Las interfaces permiten inyectar mocks de Mockito en los tests sin levantar Spring. `CompraServiceImplTest` mockea los seis servicios dependientes y solo verifica el comportamiento de orquestación. Sin interfaces, habría que usar `@SpringBootTest` con costo de arranque.

**Impacto medible:** Los 69 tests de backend corren sin levantar servidor ni base de datos (salvo los de `CompraControllerTest` que usan `MockMvc`).

---

### 3. Feriados fijos modelados como `Set<MonthDay>`

**Decisión:** `ValidadorFechaServiceImpl` recibe un `Set<DayOfWeek>` para días cerrados semanales y un `Set<MonthDay>` para feriados fijos (inyectados por `AppConfig`).

**Por qué:** Usar `MonthDay` de la API de Java evita comparar strings ("25/12") o enteros propensos a errores. La inyección desde `AppConfig` hace que la configuración sea testeable: los tests de `ValidadorFechaFeriadosTest` instancian el validador pasando directamente los sets, sin necesidad de un archivo de properties.

**Respuesta del PO (2026-05-28):** Días cerrados = lunes, 25 de diciembre, 1 de enero.

---

### 4. Lógica de descuentos por edad en `CalculadorPrecioServiceImpl`

**Decisión:** Los descuentos se calculan en el backend, en `CalculadorPrecioServiceImpl.calcularTotal()`. El frontend replica la misma lógica en `compraUtils.calcularPrecio()` solo para mostrar el resumen visual antes de confirmar.

**Por qué:** El precio autorizado es el que devuelve el backend. Si el frontend calculara el total y lo enviara, un usuario malintencionado podría modificarlo. El frontend solo muestra una estimación; el monto persistido en `Compra.montoTotal` siempre lo calcula el servidor.

**Respuesta del PO (2026-05-28):** Confirmó los rangos etarios:

| Rango | Precio |
|---|---|
| ≤ 3 años | Gratis |
| 4–15 años | 50% de descuento |
| ≥ 60 años | 50% de descuento |
| 16–59 años | Precio completo |

**Cambio respecto a la asunción inicial:** Se asumía que la edad no modificaba el precio hasta confirmación del PO.

---

### 5. Precios encapsulados en el enum `TipoPase`

**Decisión:** `TipoPase.REGULAR` y `TipoPase.VIP` llevan su precio como campo del enum.

**Por qué:** El precio es una propiedad intrínseca del tipo de pase en esta US ("para esta US, la diferencia entre VIP y Regular es solo el precio" — PO). Poner los precios en el enum evita tablas de lookup o constantes dispersas. Si el PO cambia los precios, hay un único lugar para actualizar.

**Respuesta del PO (2026-05-28):** REGULAR = $10.000 / VIP = $20.000. Ambos valores difirieron de las asunciones iniciales ($5.000 y $8.000 respectivamente); los tests se actualizaron automáticamente porque referencian el enum, no literales.

---

### 6. DTOs de entrada y salida desacoplados del modelo

**Decisión:** El endpoint recibe `CompraRequest` y devuelve `CompraResponse`. La entidad `Compra` nunca se serializa directamente.

**Por qué:** Exponer la entidad JPA en la API acoplaría el contrato HTTP al esquema de base de datos. Si se agrega un campo interno a `Compra` (ej: `fechaCreacion`), el contrato público no cambia. `CompraResponse` incluye `urlPago` solo cuando el método de pago es `TARJETA`, campo que no existe en la entidad.

---

### 7. Excepciones de negocio + `GlobalExceptionHandler`

**Decisión:** Cada validación fallida lanza una excepción de negocio específica (`FechaInvalidaException`, `CantidadInvalidaException`, etc.). `GlobalExceptionHandler` las intercepta y retorna HTTP 400 con un mensaje legible.

**Por qué:** El controller no tiene lógica de validación, lo que lo hace testeable en dos niveles independientes: tests de servicio (lógica) y tests de controller (HTTP). El handler centraliza el mapeo de error sin duplicar `try/catch` en cada endpoint.

---

### 8. H2 en memoria con `data.sql` para usuario de demo

**Decisión:** La base de datos es H2 en memoria. Al arrancar, Spring ejecuta `data.sql` que inserta un usuario demo (`id=1`, `demo@ecoharmony.com`).

**Por qué:** Elimina la necesidad de PostgreSQL o MySQL en el entorno de desarrollo y tests. El PO confirmó que el login/sesión no es parte de esta US ("pueden comenzar con la sesión mockeada"), por lo tanto H2 es suficiente para validar `usuarioId` contra el repositorio.

**Trade-off documentado:** Los datos se pierden al reiniciar el backend. Esto es esperado y aceptable para la demo.

---

### 9. MailHog como servidor SMTP local

**Decisión:** El backend envía mails a `localhost:1025` (MailHog). Si MailHog no está corriendo, `EmailServiceImpl` loguea el intento y continúa sin crashear.

**Por qué:** El PO confirmó que el mail de confirmación incluye número de orden, detalle de visitantes, subtotales y QR simulado. MailHog permite verificar visualmente el contenido del mail en `http://localhost:8025` sin enviar nada al exterior. La resiliencia ante falta de SMTP evita que una dependencia de infraestructura bloquee el flujo de compra en desarrollo.

---

### 10. Custom hooks para separar estado de presentación (Frontend)

**Decisión:** El estado del formulario vive en `useCompraForm`, la llamada a la API en `useSubmitCompra`, y la confirmación de pago en `usePagoMP`. `CompraPage` solo compone componentes y hooks.

**Por qué:** Los componentes React que mezclan estado, side effects y JSX son difíciles de testear. Con hooks extraídos, `useCompraForm.test.tsx` verifica la lógica del formulario sin renderizar nada; `CompraPage.test.tsx` verifica la integración visual usando los hooks reales o MSW para las llamadas HTTP.

---

### 11. MSW (Mock Service Worker) para tests de frontend

**Decisión:** Los tests de componentes y hooks usan MSW v2 para interceptar las llamadas `fetch` a `/api/compras` sin modificar el código de producción.

**Por qué:** Alternativas como mockear `fetch` globalmente o usar `axios` con adaptadores requieren cambios en el código de producción o acoplan los tests a detalles de implementación. MSW intercepta en la capa de red, exactamente como lo haría un Service Worker en producción, lo que hace que los tests sean más fieles al comportamiento real.

---

### 12. Simulación de Mercado Pago como componente interno

**Decisión:** El pago con tarjeta redirige a `MercadoPagoPage.tsx`, un componente interno que simula la UI de Mercado Pago. Al confirmar, llama a `POST /api/compras/{id}/confirmar`.

**Por qué:** El PO autorizó la simulación ("simular una interfaz parecida a Mercado Pago que muestre 'Pago exitoso'"). Integrar con la API real de MP requeriría credenciales y un servidor con HTTPS, fuera del alcance de esta US. `PagoServiceImpl` ya genera una URL interna `/pago/simulado/{id}` que el frontend intercepta para mostrar el componente.

---

### 13. Usuario mockeado en el frontend (`usuarioId = 1`)

**Decisión:** El frontend envía `usuarioId: 1` hardcodeado en `CompraRequest`.

**Por qué:** El PO explícitamente confirmó que el registro/login no forma parte de esta US. Hardcodear el id del usuario de demo es la implementación mínima que satisface la regla de negocio ("el usuario debe estar registrado") sin construir un sistema de autenticación completo. El backend valida que el id exista en `UsuarioRepository`, por lo que la regla se cumple.

---

## Arquitectura

### Backend

```
src/main/java/com/ecoharmony/compraentradas/
├── controller/
│   └── CompraController          # POST /api/compras
│                                 # GET  /api/compras/{id}
│                                 # POST /api/compras/{id}/confirmar
├── service/
│   ├── [Interfaces de servicio]
│   └── impl/
│       ├── CompraServiceImpl         # Orquesta: valida → calcula → persiste → notifica
│       ├── ValidadorFechaServiceImpl # Fechas pasadas, lunes, feriados fijos
│       ├── ValidadorCantidadServiceImpl
│       ├── ValidadorVisitantesServiceImpl
│       ├── ValidadorUsuarioServiceImpl   # Consulta UsuarioRepository (H2)
│       ├── ValidadorFormaPagoServiceImpl
│       ├── CalculadorPrecioServiceImpl   # Precio base + descuentos por edad
│       ├── PagoServiceImpl              # Genera URL interna o externa
│       └── EmailServiceImpl             # Construye y envía mail de confirmación
├── model/
│   ├── Compra, Visitante, Usuario
│   ├── TipoPase       (REGULAR $10.000 / VIP $20.000)
│   ├── FormaPago      (EFECTIVO / TARJETA)
│   └── EstadoCompra   (PENDIENTE / PENDIENTE_BOLETERIA / CONFIRMADA)
├── repository/
│   ├── CompraRepository
│   └── UsuarioRepository
├── dto/
│   ├── CompraRequest, CompraResponse, VisitanteDto, ParqueReglasResponse
├── exception/
│   └── GlobalExceptionHandler    # Mapea excepciones de negocio → HTTP 400/404
└── config/
    └── AppConfig                 # Beans: días cerrados, feriados fijos
```

**Flujo de una compra:**

```
POST /api/compras
  → CompraController
    → CompraServiceImpl
        ├── ValidadorFechaService.validar(fecha)
        ├── ValidadorCantidadService.validar(cantidad)
        ├── ValidadorVisitantesService.validar(visitantes, cantidad)
        ├── ValidadorUsuarioService.validar(usuarioId)
        ├── ValidadorFormaPagoService.validar(formaPago)
        ├── CalculadorPrecioService.calcularTotal(visitantes)
        ├── CompraRepository.save(compra)
        ├── EmailService.enviarConfirmacion(compra)
        └── PagoService.generarUrlPago(compra)
  ← CompraResponse { id, montoTotal, estado, urlPago? }
```

### Frontend

```
src/
├── components/
│   ├── CompraPage.tsx            # Formulario principal (compone los demás)
│   ├── SeleccionFecha.tsx        # Date picker (react-day-picker)
│   ├── SeleccionEntradas.tsx     # Inputs de visitantes (nombre, edad, tipo de pase)
│   ├── ResumenCompra.tsx         # Muestra subtotales y total estimado
│   ├── SeleccionPago.tsx         # Radio buttons: EFECTIVO / TARJETA
│   └── MercadoPagoPage.tsx       # UI simulada de Mercado Pago (Ciclo 22)
├── hooks/
│   ├── useCompraForm.ts          # Estado y validaciones del formulario
│   ├── useSubmitCompra.ts        # POST /api/compras → redirige según formaPago
│   ├── usePagoMP.ts              # POST /api/compras/{id}/confirmar
│   └── useParqueReglas.ts        # GET /api/parque/reglas (días cerrados, precios)
├── utils/
│   └── compraUtils.ts            # Funciones puras: calcularPrecio, formatearPrecio
├── types/
│   └── index.ts                  # Tipos compartidos (Visitante, CompraRequest, etc.)
└── mocks/
    ├── handlers.ts               # Handlers MSW (interceptan /api/* en tests)
    └── server.ts                 # Setup del servidor MSW para Vitest
```

---

## Ciclos TDD implementados

148 tests en total. Cada ciclo sigue el ciclo Red → Green → Refactor.

| Ciclos | Descripción | Tests | Decisión de diseño aplicada |
|---|---|---|---|
| 1–3 | Modelos: Visitante, TipoPase, Compra | 14 | Enum con precio (#5), validaciones en constructor (#1) |
| 4, 4-bis | Validación de fecha + feriados fijos (25/12, 01/01) | 10 | `Set<MonthDay>` inyectable (#3) |
| 5–6 | Validación de cantidad y visitantes | 10 | Servicios validadores independientes (#1) |
| 7, 7-bis | Cálculo de precio + descuentos por edad | 9 | Cálculo en backend (#4), precios en enum (#5) |
| 8–9 | Validación de usuario y forma de pago | 5 | Interfaces + mocks (#2) |
| 10–12 | PagoService, EmailService, orquestación CompraServiceImpl | 15 | MailHog resiliente (#9), flujo de orquestación (#6) |
| 13 | CompraController HTTP + confirmar pago | 8 | DTOs desacoplados (#6), GlobalExceptionHandler (#7) |
| 14–21 | Frontend: utils, hooks, componentes, integración | 74 | Custom hooks (#10), MSW (#11) |
| 22 | MercadoPagoPage (UI simulada) | 5 | Simulación interna de MP (#12) |
| **Total** | | **148** | |

---

## Reglas de negocio confirmadas por el PO

Todas las reglas fueron confirmadas por el PO el **2026-05-28** (constanzagarnero@gmail.com, jsbarbera4@gmail.com). Ver detalle completo en `docs/decisiones-de-diseno.md`.

### Días de apertura
El parque abre todos los días **excepto**:
- Lunes
- 25 de diciembre
- 1 de enero

### Precios base

| Tipo de pase | Precio |
|---|---|
| Regular | $10.000 |
| VIP | $20.000 |

> Para esta US, la única diferencia entre VIP y Regular es el precio (confirmado por PO).

### Descuentos por edad

| Rango | Descuento |
|---|---|
| ≤ 3 años | **Gratis** |
| 4–15 años | **50% off** |
| ≥ 60 años | **50% off** |
| 16–59 años | Precio completo |

### Otras reglas

- Mínimo 1 entrada, máximo 10 por compra
- La cantidad de visitantes debe coincidir exactamente con la cantidad de entradas
- El usuario debe estar registrado en el sistema
- No hay cupo máximo de visitantes por día (confirmado: sin validación de aforo)
- El registro/login no es parte de esta US (PO autorizó sesión mockeada)

---

## Levantar el proyecto

Necesitás **tres terminales**.

### Terminal 1 — MailHog (servidor de mail local)

```bash
docker compose up
```

| Servicio | URL |
|---|---|
| SMTP (Spring Boot lo usa automáticamente) | `localhost:1025` |
| Bandeja de entrada web | `http://localhost:8025` |

> Los mails de confirmación se ven en `http://localhost:8025`. No se envía nada al exterior. Si no levantás MailHog, el backend loguea el intento y continúa normalmente.

### Terminal 2 — Backend

```bash
cd backend
mvn spring-boot:run
```

Levanta en `http://localhost:8080`

> H2 en memoria: los datos se pierden al reiniciar (esperado). Usuario de demo: `id=1`, `demo@ecoharmony.com`, cargado automáticamente desde `data.sql`.

**Consola H2** — con el backend corriendo: `http://localhost:8080/h2-console`

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:ecoharmony` |
| User Name | `SA` |
| Password | *(vacío)* |

### Terminal 3 — Frontend

```bash
cd frontend
npm install      # solo la primera vez
npm run dev
```

Levanta en **`http://localhost:5173`**

> El proxy `/api` en `vite.config.ts` redirige al backend en `:8080`.

**Acceso desde la misma red WiFi:**
```bash
npm run dev -- --host
```

**Acceso externo con ngrok** (instalar una vez, no es dependencia del proyecto):
```bash
npm install -g ngrok
ngrok http 5173
```

### Flujo de uso

**Pago en efectivo:**
1. Completar fecha, visitantes y seleccionar **Efectivo**
2. Confirmar compra → pantalla de confirmación

**Pago con tarjeta (simulado):**
1. Completar fecha, visitantes y seleccionar **Tarjeta**
2. Confirmar compra → redirige a pantalla simulada de Mercado Pago
3. Click en **Pagar** → ¡Pago exitoso!

---

## Correr los tests

### Backend (69 tests)

```bash
cd backend
mvn test
```

### Frontend (79 tests)

```bash
cd frontend
npx vitest run
```

### Cobertura frontend

```bash
cd frontend
npx vitest run --coverage
```

---

## Documentación adicional

| Archivo | Contenido |
|---|---|
| `docs/decisiones-de-diseno.md` | Preguntas al PO, respuestas con fecha, asunciones iniciales vs. confirmadas, impacto en código |
| `docs/ciclos-tdd.md` | Los 24 ciclos TDD planificados e implementados con detalle de cada test |
| `docs/java-style-guide.md` | Google Java Style Guide aplicado (2 espacios, 100 cols, llaves egipcias) |
| `docs/typescript-style-guide.md` | Google TypeScript Style Guide aplicado |
