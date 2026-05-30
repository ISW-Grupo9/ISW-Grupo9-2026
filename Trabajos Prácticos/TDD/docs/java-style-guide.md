# Guía de Estilo de Google para Java

Esta guía define los estándares de codificación y diseño para todos los desarrollos realizados en **Java (Spring Boot)**. Adhiere de forma estricta a la [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

---

## 1. Principios Generales

* **Legibilidad:** El código debe ser autoexplicativo, limpio y fácil de leer para otros miembros del equipo (y agentes de IA).
* **Consistencia:** Todo el código en el backend debe lucir como si hubiera sido escrito por una sola persona.
* **Idempotencia y Robustez:** El diseño debe prever el manejo adecuado de fallos y nulls.

---

## 2. Formato y Reglas de Código

### 2.1 Indentación y Columnas
* **Indentación:** Se deben usar estrictamente **2 espacios** para la indentación de bloques de código. **Prohibido usar tabulaciones (tabs) o 4 espacios.**
* **Límite de Columnas:** Las líneas de código deben tener un límite máximo de **100 caracteres**. Si una línea excede este límite, se debe realizar un salto de línea (Line-wrapping) adecuado.
* **Salto de Línea:** Al envolver código en una nueva línea, se debe indentar con al menos **+4 espacios** adicionales (indentación de continuación).

### 2.2 Llaves (Braces)
* Se utiliza el estilo de **llaves egipcias** (Egyptian Brackets):
  - La llave de apertura `{` va al final de la línea que inicia la estructura.
  - La llave de cierre `}` inicia una nueva línea y se alinea con el inicio de la instrucción correspondiente.
  - Las llaves son **obligatorias** para todas las estructuras `if`, `else`, `for`, `do` y `while`, incluso si solo contienen una única línea de código.

```java
// ✅ CORRECTO
if (condicion) {
  hacerAlgo();
} else {
  hacerOtraCosa();
}

// ❌ INCORRECTO
if (condicion)
  hacerAlgo();
```

---

## 3. Estructura de Archivos e Importaciones

### 3.1 Declaración del Package e Importaciones
1. **Declaración del Package:** Va en la primera línea (sin saltos adicionales antes del package).
2. **Importaciones:**
   - **Prohibido el uso de Wildcards:** No se permiten importaciones con asteriscos (ej: `import java.util.*;`). Cada clase debe importarse explícitamente.
   - **Orden de Importaciones:** Las importaciones deben estar agrupadas de la siguiente manera, separadas por una línea en blanco:
     1. Importaciones estáticas (`import static ...`).
     2. Importaciones de terceros y Java estándar de forma alfabética.

```java
// ✅ CORRECTO
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
```

---

## 4. Convenciones de Nombres (Naming)

| Elemento | Regla de Formato | Ejemplo |
| :--- | :--- | :--- |
| **Packages** | Todo minúsculas, palabras concatenadas (sin guiones/guiones bajos). | `com.tuempresa.proyecto.controller` |
| **Clases / Interfaces** | `PascalCase` (sustantivos). | `MateriaService`, `UsuarioController` |
| **Métodos** | `camelCase` (verbos o frases verbales). | `obtenerMateriasPorId`, `guardar` |
| **Variables / Parámetros**| `camelCase` (nombres descriptivos). | `materiaId`, `nuevaMateria` |
| **Constantes** | `UPPER_SNAKE_CASE` (`static final`). | `MAX_RETRIES`, `DEFAULT_PAGE_SIZE` |
| **Tipos Genéricos** | Una sola letra mayúscula. | `T`, `E`, `K`, `V` |

---

## 5. Buenas Prácticas de Codificación

### 5.1 Anotación `@Override`
* La anotación `@Override` es **obligatoria** cada vez que un método anule o implemente un método definido en una superclase o interfaz.

### 5.2 Estructura de Clases
Los miembros de una clase deben ordenarse de forma lógica para mejorar la lectura:
1. Variables estáticas (constantes primero, luego mutables).
2. Variables de instancia (propiedades de la clase).
3. Constructores.
4. Métodos públicos y de negocio.
5. Métodos privados de soporte (deben ubicarse cerca de los métodos públicos que los llaman).

### 5.3 Excepciones y Nulos
* Nunca silencies excepciones (`catch (Exception e) {}`). Si capturas una excepción, debes registrarla con un Logger (`log.error(...)`) o propagarla.
* Utiliza `Optional<T>` en los retornos de métodos de negocio en lugar de retornar `null` directamente para evitar `NullPointerException`.
