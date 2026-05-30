# Guía de Estilo de Google para TypeScript y React

Esta guía define las directrices y estándares de codificación para todos los desarrollos de **TypeScript y React** (Frontend y BFF). Adhiere de forma estricta a la [Google TypeScript Style Guide](https://google.github.io/styleguide/tsguide.html).

---

## 1. Principios Generales

* **Tipado Estricto:** TypeScript debe usarse para proveer seguridad en tiempo de compilación. El uso de `any` está estrictamente prohibido.
* **Consistencia:** Mantener una estructura uniforme en componentes React, servicios y hooks.
* **Modernidad:** Utilizar características nativas de ES6+ y evitar constructos obsoletos.

---

## 2. Formato y Reglas de Código

### 2.1 Indentación y Columnas
* **Indentación:** Se deben usar estrictamente **2 espacios** para la indentación de bloques de código. **Prohibido usar tabulaciones (tabs) o 4 espacios.**
* **Límite de Columnas:** Las líneas de código deben tener un límite máximo de **100 caracteres** (incluyendo código y comentarios).
* **Punto y Coma (Semicolons):** El uso de punto y coma `;` es **OBLIGATORIO** al final de cada declaración.

### 2.2 Comillas (Quotes)
* **Código General (TS/JS):** Usar **comillas simples (`'`)** de forma obligatoria para literales de cadena de texto (strings).
* **JSX / TSX (React):** Usar **comillas dobles (`"`)** para los atributos JSX de los componentes React.
* **Plantillas (Template Strings):** Usar comillas invertidas (backticks `` ` ``) únicamente cuando se requiera interpolación de variables o cadenas multilínea.

```typescript
// ✅ CORRECTO
const mensaje = 'Hola Mundo';
const apiPath = `/api/v1/usuarios/${id}`;
return <GlassCard variant="transparent" />;

// ❌ INCORRECTO
const mensaje = "Hola Mundo";
const apiPath = '/api/v1/usuarios/' + id;
return <GlassCard variant='transparent' />;
```

---

## 3. Convenciones de Nombres (Naming)

| Elemento | Regla de Formato | Ejemplo |
| :--- | :--- | :--- |
| **Componentes React** | `PascalCase` (sustantivos). | `GlassCard.tsx`, `LoginForm.tsx` |
| **Archivos de Código (TS)**| `kebab-case` (todo minúsculas separado por guiones). | `auth-service.ts`, `use-fetch.ts` |
| **Clases / Tipos / Enums**| `PascalCase`. | `UserSession`, `AppState` |
| **Interfaces** | `PascalCase` **(Sin el prefijo "I")**. | `UserService` (en lugar de `IUserService`) |
| **Variables / Funciones** | `camelCase`. | `obtenerDatos`, `usuarioLogueado` |
| **Constantes** | `UPPER_SNAKE_CASE` (solo para constantes globales inmutables). | `API_BASE_URL`, `MAX_RETRIES` |

---

## 4. Reglas Específicas de TypeScript

### 4.1 Tipos de Datos y el tipo `any`
* **Prohibido `any`:** El uso de `any` deshabilita el compilador. Si el tipo de datos verdaderamente se desconoce, se debe usar `unknown` y estrechar el tipo (type narrowing) mediante `typeof` o `instanceof` antes de operar.
* **Declaración de Arrays:** Usar la sintaxis compacta `Type[]` en lugar de `Array<Type>`.
* **Aseveraciones de Tipo (Type Assertions):** Utilizar la sintaxis `as Type` en lugar de `<Type>`.

```typescript
// ✅ CORRECTO
const lista: string[] = ['a', 'b'];
const datos = respuesta as UserData;

// ❌ INCORRECTO
const lista: Array<string> = ['a', 'b'];
const datos = <UserData>respuesta;
```

### 4.2 Importaciones y Exportaciones
* Utilizar únicamente la sintaxis de módulos ES6 (`import` y `export`). **Prohibido usar `require()`.**
* Importar siempre los tipos utilizando la directiva explícita `type` para mejorar la optimización en el bundle final (tree-shaking):
  ```typescript
  import { useState } from 'react';
  import type { UserInfo } from '../types';
  ```

---

## 5. Estándares para React (TSX)

* **Estructura del Componente:**
  1. Importaciones (React, componentes comunes, estilos/iconos, tipos).
  2. Tipado de Props (usando `interface` o `type`).
  3. Declaración del Componente (como función flecha `const Component: React.FC<Props> = ...` o función regular `export function Component(...)`).
  4. Lógica del Componente (Hooks de estado, hooks de efectos, callbacks/handlers).
  5. Retorno JSX estructurado y limpio.
* **Componentes Enfocados:** Si un componente excede las 200 líneas de JSX, debe dividirse en componentes hijos más pequeños y reutilizables.
