# Decisiones de Diseño — Comprar Entradas (US #8)

## Estado: Confirmado por el PO — 2026-05-28

---

## Respuestas del PO

### 1. ¿Qué días está abierto el parque?

**Estado:** ✅ Confirmado por el PO

**Respuesta:** El parque abre todos los días **excepto**:
- Lunes
- 25 de diciembre
- 1 de enero

**Impacto en el código:**
- `ValidadorFechaServiceImpl` recibe `Set<DayOfWeek>` para días cerrados por semana → ya implementado con `MONDAY`.
- Agregar validación de **feriados fijos** (`MonthDay`): 25/12 y 01/01.
- Crear nuevo ciclo TDD que cubra los casos de feriado.
- Los tests existentes del ciclo 4 no se rompen (siguen validando lunes y fechas pasadas).

---

### 2. ¿Cuánto cuesta la entrada regular?

**Estado:** ✅ Confirmado por el PO

**Respuesta:** Entrada común: **$10.000 por persona**

**⚠️ Cambio respecto a la asunción anterior:** Se asumía $5.000 — el PO confirma $10.000.

**Impacto en el código:**
- Actualizar `TipoPase.REGULAR` de `5000` a `10000`.
- Actualizar `compraUtils.ts` frontend: `PRECIO_REGULAR = 10000`.
- Los tests del ciclo 7 (`CalculadorPrecioServiceImplTest`) usan los valores del enum, se actualizan automáticamente.
- Los tests frontend del ciclo 14 usan las constantes del util, se actualizan automáticamente.

---

### 3. ¿Cuánto cuesta la entrada VIP?

**Estado:** ✅ Confirmado por el PO

**Respuesta:** Entrada VIP: **$20.000 por persona**

**⚠️ Cambio respecto a la asunción anterior:** Se asumía $8.000 — el PO confirma $20.000.

**Impacto en el código:**
- Actualizar `TipoPase.VIP` de `8000` a `20000`.
- Actualizar `compraUtils.ts` frontend: `PRECIO_VIP = 20000`.

---

### 4. ¿Qué diferencia al pase VIP del regular?

**Estado:** ✅ Confirmado por el PO

**Respuesta:** Para esta US, **solo el precio**.

**Impacto en el código:** Ninguno. El modelo actual ya refleja esto.

---

### 5. ¿La edad del visitante modifica el precio?

**Estado:** ✅ Confirmado por el PO

**Respuesta:** Sí, hay descuentos por edad:

| Rango | Precio |
|-------|--------|
| ≤ 3 años | **Gratis** (no pagan) |
| ≤ 15 años | **50% de descuento** |
| ≥ 60 años | **50% de descuento** |
| Resto | Precio completo |

**⚠️ Cambio respecto a la asunción anterior:** Se asumía que la edad no modificaba el precio.

**Impacto en el código:**
- `CalculadorPrecioServiceImpl.calcularTotal()` debe aplicar la lógica de descuento por edad.
- `VisitanteDto` ya tiene campo `edad`, se puede usar directamente.
- Agregar nuevos tests al ciclo 7 cubriendo cada franja etaria (o crear ciclo 7-bis).
- Frontend: `calcularTotal()` en `compraUtils.ts` debe recibir la edad y aplicar el descuento.

**Lógica de cálculo:**
```
precioFinal(visitante) =
  si edad ≤ 3  → 0
  si edad ≤ 15 → tipoPase.precio * 0.5
  si edad ≥ 60 → tipoPase.precio * 0.5
  sino         → tipoPase.precio
```

---

### 6. ¿Qué incluye el mail de confirmación?

**Estado:** ✅ Confirmado por el PO

**Respuesta:** El mail debe incluir:
- Número de orden
- Detalle de visitantes
- QR de acceso (simulado)
- Subtotales por visitante
- Total

Se puede simular: mostrar el cuerpo del mail como **modal o página aparte**.

**Impacto en el código:**
- `EmailServiceImpl.enviarConfirmacion()` debe construir un cuerpo más detallado.
- Frontend: al confirmar la compra, mostrar un modal o página de "bandeja de entrada" con el contenido del mail.
- El QR se puede simular con texto o imagen placeholder.

---

### 7. ¿Se puede simular Mercado Pago?

**Estado:** ✅ Confirmado por el PO

**Respuesta:** Sí. Simular una **interfaz parecida a Mercado Pago** que refleje la transacción y muestre "Pago exitoso".

**Impacto en el código:**
- Crear componente `MercadoPagoSimulado.tsx` que recibe el monto y muestra una pantalla estilo MP.
- Al hacer click en "Pagar", simular la confirmación y redirigir de vuelta con estado `CONFIRMADA`.
- `PagoServiceImpl` ya genera una URL mock, el frontend la intercepta y muestra el componente.

---

### 8. ¿El registro/login es parte de esta US?

**Estado:** ✅ Confirmado por el PO

**Respuesta:** No es necesario. Se puede comenzar con la **sesión y el usuario mockeados**.

**Impacto en el código:** Ninguno. El diseño actual ya asume `usuarioId` del contexto de sesión.

---

### 9. ¿Hay cupo máximo de visitantes por día?

**Estado:** ✅ Confirmado por el PO

**Respuesta:** **No hay máximo** de visitantes por día.

**Impacto en el código:** Ninguno. No se implementa validación de cupo.

---

## Registro de cambios

| Fecha | Cambio |
|---|---|
| 2026-05-27 | Documento creado con asunciones iniciales |
| 2026-05-28 | Todas las dudas respondidas por el PO (constanzagarnero@gmail.com, jsbarbera4@gmail.com). Precios actualizados, descuento por edad confirmado, feriados agregados |
