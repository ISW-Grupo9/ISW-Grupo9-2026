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
