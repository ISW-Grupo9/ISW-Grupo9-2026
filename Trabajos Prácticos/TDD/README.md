# EcoHarmony Park — Comprar Entradas

**TP6 TDD · Ingeniería de Software · Grupo 9 · 2026**
**User Story #8:** Como usuario registrado quiero comprar entradas al parque.

---

## Stack

| Capa | Tecnología |
|------|-----------|
| Backend | Java 21 · Spring Boot 3.3 · Maven |
| Base de datos | H2 en memoria |
| Tests backend | JUnit 5 · Mockito · AssertJ · MockMvc |
| Frontend | React 19 · TypeScript · Vite · Tailwind CSS 4 |
| Tests frontend | Vitest · React Testing Library · MSW v2 |

---

## Requisitos previos

- **Java 21** (`java -version`)
- **Maven 3.9+** (`mvn -version`)
- **Node.js 20+** (`node -v`)

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

> Los mails de confirmación se pueden ver en `http://localhost:8025` como si fuera una casilla real. No se envía nada al exterior.

### Terminal 2 — Backend

```bash
cd backend
mvn spring-boot:run
```

Levanta en `http://localhost:8080`

> La base de datos H2 se crea en memoria al arrancar con un usuario de demo ya cargado (`id=1`, `demo@ecoharmony.com`).

#### Consola H2 (ver base de datos)

Con el backend corriendo, entrá a `http://localhost:8080/h2-console` y usá estos datos:

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

> El proxy `/api` está configurado en `vite.config.ts` para apuntar al backend en `:8080`.

#### Acceso desde otros dispositivos en la misma red (WiFi)

```bash
npm run dev -- --host
```

Vite mostrará una URL de red tipo `http://192.168.1.XX:5173` accesible desde cualquier dispositivo conectado al mismo WiFi.

#### Acceso desde fuera de la red (ngrok)

Instalar una sola vez (no es dependencia del proyecto):

```bash
npm install -g ngrok
```

Con el frontend ya corriendo, en otra terminal:

```bash
ngrok http 5173
```

Ngrok genera una URL pública tipo `https://abc123.ngrok.io` accesible desde cualquier lugar. La URL cambia cada vez que reiniciás ngrok (plan gratuito). Requiere crear una cuenta gratuita en [ngrok.com](https://ngrok.com) sin tarjeta.

---

## Flujo de uso

### Pago en efectivo
1. Completá la fecha de visita, cantidad de entradas y datos de cada visitante
2. Seleccioná **Efectivo**
3. Hacé click en **Confirmar compra**
4. Aparece la pantalla de confirmación

### Pago con tarjeta (simulado)
1. Completá el formulario
2. Seleccioná **Tarjeta**
3. Hacé click en **Confirmar compra**
4. El sistema redirige a la pantalla simulada de **Mercado Pago**
5. Hacé click en **Pagar**
6. Aparece **¡Pago exitoso!**

---

## Reglas de negocio confirmadas por el PO

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

> Para esta US, la diferencia entre VIP y Regular es solo el precio.

### Descuentos por edad
| Rango | Descuento |
|---|---|
| ≤ 3 años | **Gratis** |
| ≤ 15 años | **50% off** |
| ≥ 60 años | **50% off** |
| 16–59 años | Precio completo |

### Otras reglas
- Mínimo 1 entrada, máximo 10 por compra
- La cantidad de visitantes debe coincidir con la cantidad de entradas
- El usuario debe estar registrado en el sistema
- No hay cupo máximo de visitantes por día

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

### Ver cobertura frontend

```bash
cd frontend
npx vitest run --coverage
```

---

## Arquitectura backend

```
src/main/java/com/ecoharmony/compraentradas/
├── controller/
│   └── CompraController          # POST /api/compras, GET /api/compras/{id}
│                                 # POST /api/compras/{id}/confirmar
├── service/
│   ├── CompraServiceImpl         # Orquestación del flujo de compra
│   ├── ValidadorFechaServiceImpl # Valida fecha (pasada, lunes, feriados)
│   ├── ValidadorCantidadServiceImpl
│   ├── ValidadorVisitantesServiceImpl
│   ├── ValidadorUsuarioServiceImpl  # Consulta UsuarioRepository (H2)
│   ├── ValidadorFormaPagoServiceImpl
│   ├── CalculadorPrecioServiceImpl  # Precio base + descuentos por edad
│   ├── PagoServiceImpl           # Genera URL interna /pago/simulado
│   └── EmailServiceImpl          # Envía mail (loguea si no hay SMTP)
├── model/
│   ├── Compra, Visitante, Usuario
│   ├── TipoPase (REGULAR $10.000 / VIP $20.000)
│   ├── FormaPago (EFECTIVO / TARJETA)
│   └── EstadoCompra (PENDIENTE / PENDIENTE_BOLETERIA / CONFIRMADA)
├── repository/
│   ├── CompraRepository
│   └── UsuarioRepository
├── dto/
│   ├── CompraRequest, CompraResponse, VisitanteDto
└── exception/
    └── GlobalExceptionHandler    # Mapea excepciones de negocio a HTTP 400/404
```

---

## Arquitectura frontend

```
src/
├── components/
│   ├── CompraPage.tsx            # Formulario principal de compra
│   ├── SeleccionFecha.tsx
│   ├── SeleccionEntradas.tsx
│   ├── ResumenCompra.tsx
│   ├── SeleccionPago.tsx
│   └── MercadoPagoPage.tsx       # UI simulada de Mercado Pago
├── hooks/
│   ├── useCompraForm.ts          # Estado y validaciones del formulario
│   ├── useSubmitCompra.ts        # Llama a POST /api/compras
│   └── usePagoMP.ts              # Llama a POST /api/compras/{id}/confirmar
├── utils/
│   └── compraUtils.ts            # Cálculo de precios, formateo, validaciones puras
├── types/
│   └── index.ts                  # Tipos compartidos
└── mocks/
    ├── handlers.ts               # Handlers MSW para tests
    └── server.ts                 # Setup del servidor MSW
```

---

## Documentación adicional

| Archivo | Contenido |
|---|---|
| `docs/ciclos-tdd.md` | Los 24 ciclos TDD planificados e implementados (148 tests) |
| `docs/decisiones-de-diseno.md` | Decisiones técnicas y respuestas del PO con fecha |

---

## Ciclos TDD implementados

| Ciclos | Descripción | Tests |
|---|---|---|
| 1–3 | Modelos: Visitante, TipoPase, Compra | 14 |
| 4, 4-bis | Validación de fecha + feriados fijos | 10 |
| 5–6 | Validación de cantidad y visitantes | 10 |
| 7, 7-bis | Cálculo de precio + descuentos por edad | 9 |
| 8–9 | Validación de usuario y forma de pago | 5 |
| 10–12 | Pago, Email, orquestación CompraService | 15 |
| 13 | CompraController (HTTP) + confirmar pago | 8 |
| 14–21 | Frontend: utils, hooks, componentes, flujo completo | 74 |
| 22 | MercadoPagoPage (UI simulada de MP) | 5 |
| **Total** | | **148** |

---

## Notas para la presentación

- **Usuario de demo:** `id=1` · `demo@ecoharmony.com` (cargado automáticamente al iniciar el backend)
- **Mail:** Se usa MailHog (`docker compose up`). Los mails se ven en `http://localhost:8025`. Si no levantás MailHog, el backend loguea el intento y continúa normalmente (no crashea)
- **BD:** H2 en memoria — los datos se pierden al reiniciar el backend, lo cual es esperado
- **Spring Security:** El endpoint `POST /api/compras` no tiene autenticación real (el `usuarioId` se toma como prop del frontend — decisión del PO: "pueden comenzar con la sesión mockeada")
