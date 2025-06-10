# Tarea: Endpoint de Búsqueda Avanzada de Productos (Spring WebFlux + R2DBC)

## Objetivo
Construir un endpoint REST `/api/v1/products/search` que permita buscar productos usando múltiples filtros combinados, soportando paginación y devolviendo la respuesta en formato reactivo.

## Requisitos
- El endpoint debe aceptar parámetros opcionales:
  - `name` (String, búsqueda parcial por nombre)
  - `category` (String)
  - `minPrice` (Double)
  - `maxPrice` (Double)
  - `available` (Boolean)
  - `minSales` (Integer)
  - `page` (Integer, default 1)
  - `size` (Integer, default 20)
- Debe devolver los productos que cumplan con todos los filtros aplicados.
- La respuesta debe incluir paginación (igual que el endpoint principal de productos).
- El endpoint debe ser reactivo y eficiente (no usar `.collectList()` salvo para paginación).
- Documentar el endpoint y agregar un ejemplo de cURL en el README.

## Ejemplo de request
```
GET /api/v1/products/search?name=camisa&category=ropa&minPrice=10&maxPrice=100&available=true&minSales=5&page=1&size=10
```

## Ejemplo de respuesta
```json
{
  "data": [
    { "id": 1, "name": "Camisa Polo", ... }
  ],
  "pagination": {
    "total_records": 1,
    "current_page": 1,
    "total_pages": 1,
    "next_page": null,
    "prev_page": null
  }
}
```

## Realizado los puntos logramos
- Uso de filtros dinámicos en consultas reactivas.
- Paginación y manejo eficiente de grandes volúmenes de datos.
- Buenas prácticas de diseño de APIs RESTful reactivas.
- Documentación y pruebas de endpoints.

## Bonus
- Permitir búsqueda insensible a mayúsculas/minúsculas en el nombre.
