## Crear una línea base en Git

## 1. Usando Tags (la forma más común)

```bash
# Crear un tag anotado (recomendado para líneas base)
git tag -a v1.0-baseline -m "comment"

# Subir el tag a GitHub
git push origin v1.0-baseline

# Subir todos los tags
git push origin --tags
```

---

## 2. Usando Releases en GitHub

Los Releases son tags + notas de versión con interfaz visual:

1. Ir a tu repositorio
2. Entrar en **Releases**
3. Hacer clic en **Create a new release**
4. Elegir el tag (o crear uno nuevo como `v1.0-baseline`)
5. Agregar título y descripción de la línea base
6. Publicar el release

## Actualización obligatoria de la documentación

El archivo README.md expone la versión actual del proyecto. Cada determinación de línea base debe actualizar la versión expuesta en el README.

Pasos a seguir

1. Ubicarse en main
Asegúrate de estar en la rama principal y tener los últimos cambios del repositorio remoto:
```bash
git checkout main
git pull origin main
```

2. Crear la branch
Crea y muévete a una nueva rama específica para esta actualización (puedes cambiar docs/actualizar-version por el nombre de tu preferencia):
```bash
git checkout -b docs/actualizar-version
```

3. Hacer la modificación del README
Abre el archivo README.md en tu editor de código, actualiza el número de versión y guarda los cambios. Luego, añade el archivo al área de preparación:
```bash
git add README.md
```

4. Commit del cambio
Crea un commit con un mensaje descriptivo siguiendo las convenciones de tu equipo:
```bash
git commit -m "docs: actualizar versión del proyecto en README"
```

5. Merge de la branch a main
Vuelve a la rama principal e integra los cambios de tu rama de documentación:
```bash
git checkout main
git merge docs/actualizar-version
```

(Opcional) Subir los cambios al repositorio remoto:
```bash
git push origin main
```
