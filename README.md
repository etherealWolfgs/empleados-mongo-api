# Empleados API — MongoDB

API REST para administrar empleados, construida en la Academia Java CDMX: primero con **MySQL** (Semana 3, del
24 al 26 de septiembre de 2026; está en la etiqueta `v1-mysql`) y después **migrada a MongoDB** (Semana 4, 28 y 29
de septiembre).

**Alumno:** Genaro Salvador Morales Paoli

## Tecnologías

Java 17 · Spring Boot 4.1.1 · Spring Data MongoDB · Bean Validation · MongoDB 8.2 y DbGate en Docker ·
springdoc-openapi (Swagger) · WSL2 con Ubuntu 24.04

## Cómo levantarla

```bash
docker compose up -d            # MongoDB y DbGate; espera a que docker compose ps diga (healthy)
./mvnw spring-boot:run          # la API en http://localhost:8080
```

- Swagger: http://localhost:8080/swagger-ui.html
- DbGate (ver los documentos): http://localhost:3000
- Datos de ejemplo (30 empleados, una sola vez):
  `docker exec -i empleados-mongo mongoimport --maintainInsertionOrder -u academia -p academia123 --authenticationDatabase admin -d empleados_db -c empleados < datos/semilla-empleados.json`

## Endpoints

| Verbo | Ruta | Qué hace |
|---|---|---|
| GET | `/api/empleados?page=0&size=10&sort=id,asc` | Lista por páginas |
| GET | `/api/empleados/{id}` | Un empleado (404 si no existe) |
| POST | `/api/empleados` | Crea (201 + Location; 400 datos inválidos; 409 email repetido) |
| PUT | `/api/empleados/{id}` | Modifica (200; 400; 404; 409) |
| DELETE | `/api/empleados/{id}` | Borra (204; 404) |
| GET | `/api/empleados/buscar?departamento=&texto=&activo=&salarioMinimo=&salarioMaximo=&ciudad=&habilidad=` | Búsqueda con filtros opcionales, sin importar acentos, por páginas |
| GET | `/api/empleados/departamento/{departamento}` | Los de un departamento, por apellidos |
| GET | `/api/empleados/salarios?minimo=&maximo=` | Los de un rango de salario, del mayor al menor |
| GET | `/api/empleados/estadisticas/departamentos` | Por departamento: empleados, activos y salario promedio, mínimo y máximo (agregación) |

## De MySQL a MongoDB

| | MySQL (`v1-mysql`) | MongoDB (ahora) |
|---|---|---|
| Dónde vive un empleado | una fila de la tabla `empleados` | un documento de la colección `empleados` |
| id | `Long` consecutivo (1, 2, 3…) | `String`: un ObjectId de 24 caracteres |
| Repositorio | `JpaRepository` + `@Query` JPQL | `MongoRepository` + `MongoTemplate`/`Criteria` |
| Email único | `@Column(unique = true)` | `@Indexed(unique = true)` + `auto-index-creation` |
| Dirección y habilidades | serían 2 tablas más y un JOIN | dentro del mismo documento |
| Transacciones | `@Transactional` | no hay (un solo servidor); cada documento se guarda completo o nada |

`git diff v1-mysql --stat` muestra exactamente qué archivos cambiaron.

## Evidencia

| Día | Archivos |
|---|---|
| Semana 3 (MySQL) | `evidencia/dia1/` · `evidencia/dia2/` · `evidencia/dia3/` |
| Lunes 28 — la migración | `evidencia/s4-dia1/crud.txt` · `mongo.txt` |
| Martes 29 — lo que Mongo hace distinto | `evidencia/s4-dia2/comparacion.txt` · `busquedas.txt` · `mongo.txt` · `tipos-y-agregacion.txt` |

## Qué aprendí y qué me costó

Esta semana entendí con más claridad la diferencia de fondo entre una base de datos relacional y
una documental: MongoDB no trabaja con tablas y filas, sino con documentos tipo JSON (BSON), lo
que resulta más flexible cuando el esquema puede cambiar con el tiempo, ya que no depende de una
estructura fija como MySQL.

Al migrar el proyecto también noté un par de diferencias que al inicio no consideré importantes.
Por ejemplo, el salario: en MySQL el tipo `DECIMAL` guarda los números con precisión exacta,
mientras que en MongoDB, si no se especifica `Decimal128`, se almacena como `double`, lo que puede
generar pequeñas imprecisiones en operaciones con decimales. Algo similar pasa con las fechas:
MongoDB siempre las guarda como `ISODate`, con hora y en UTC, aunque en el modelo original solo
necesitáramos el día.

También entendí la ventaja práctica de trabajar con documentos cuando se manejan datos que en SQL
estarían separados en varias tablas. Por ejemplo, la dirección (calle, colonia, estado, etc.) o las
habilidades de un empleado: en MySQL esto habría requerido dos tablas adicionales y hacer un JOIN
para obtener toda la información, mientras que en MongoDB todo vive dentro del mismo documento y se
lee de una sola vez. Además, aprendí que estos datos embebidos no tienen que quedarse como simples
cadenas de texto: se les puede aplicar validación (Bean Validation) para asegurar que, por ejemplo,
una dirección o una fecha realmente tengan un formato válido antes de guardarse.

Otro punto que me pareció interesante fue el método `sinAcentos`, usado en las búsquedas: convierte
lo que escribe el usuario en una expresión regular que acepta cada vocal con o sin acento, para que
la búsqueda no falle solo porque a alguien se le olvidó poner una tilde.

Por otro lado, ya me quedó claro para qué sirve Swagger: es una herramienta para documentar y
probar los endpoints de una API directamente desde el navegador, similar a lo que hace Postman,
pero integrada al propio proyecto.

En general, la migración en sí no representó mayor dificultad, más allá de adaptar el repositorio
y los filtros de búsqueda al nuevo modelo; lo más valioso fue entender por qué se elige una base de
datos u otra según el tipo de proyecto.