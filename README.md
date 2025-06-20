# Ecommerce API - Spring Boot WebFlux + R2DBC + GraphQL

## Requisitos
- JDK 17+
- Maven 3.9+
- MySQL 8+ (o Docker)

## Arranque rápido

```bash
# 1. Base de datos (opcional, si no tienes MySQL local)
docker run -d --name mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=ecommerce_db \
  -p 3306:3306 mysql:8

# 2. Configura credenciales en src/main/resources/application.yml si es necesario

# 3. Compilación y ejecución
mvn clean package
mvn spring-boot:run        # o java -jar target/*.jar

# 4. Pruebas
mvn test
```

## Endpoints REST principales

| Método | Path                                   | Descripción                        |
|--------|----------------------------------------|------------------------------------|
| GET    | /api/v1/products                      | Listar productos (paginado, filtro)|
| GET    | /api/v1/products/top-selling          | Top productos más vendidos         |
| GET    | /api/v1/products/{id}                 | Obtener producto por ID            |
| POST   | /api/v1/products                      | Crear producto                     |
| PATCH  | /api/v1/products/{id}                 | Actualizar parcialmente producto   |
| DELETE | /api/v1/products/{id}                 | Eliminar producto                  |
| POST   | /api/v1/products/bulk                | Carga masiva de productos           |
| GET    | /api/v1/products/stream               | Streaming reactivo de productos (SSE, soporta backpressure) |
| GET    | /api/v1/products/search               | Búsqueda avanzada de productos con filtros y paginación |

### Ejemplos cURL

- **Listar productos (paginado, filtrado):**
  ```bash
  curl "http://localhost:8080/api/v1/products?page=1&size=20&name=camisa&category=ropa&minPrice=10&maxPrice=100&available=true"
  ```
- **Obtener producto por ID:**
  ```bash
  curl http://localhost:8080/api/v1/products/1
  ```
- **Crear producto:**
  ```bash
  curl -X POST http://localhost:8080/api/v1/products \
    -H "Content-Type: application/json" \
    -d '{
      "name": "Camisa Polo",
      "description": "Camisa de algodón premium",
      "category": "ropa",
      "price": 29.99,
      "available": true,
      "sales": 100
    }'
  ```
- **Actualizar parcialmente producto (PATCH):**
  ```bash
  curl -X PATCH http://localhost:8080/api/v1/products/1 \
    -H "Content-Type: application/json" \
    -d '{ "price": 24.99, "sales": 80 }'
  ```
- **Eliminar producto:**
  ```bash
  curl -X DELETE http://localhost:8080/api/v1/products/1
  ```
- **Top ventas:**
  ```bash
  curl "http://localhost:8080/api/v1/products/top-selling?page=1&size=10"
  ```
- **Carga masiva de productos:**
  ```bash
  curl -X POST http://localhost:8080/api/v1/products/bulk \
    -H "Content-Type: application/json" \
    -d '[
      {"name": "Producto 1", "description": "Desc 1", "category": "cat1", "price": 10.0, "available": true, "sales": 5},
      {"name": "Producto 2", "description": "Desc 2", "category": "cat2", "price": 20.0, "available": true, "sales": 10}
    ]'
  ```
- **Streaming reactivo de productos (SSE):**
  ```bash
  curl http://localhost:8080/api/v1/products/stream
  ```
  Este endpoint devuelve los productos uno a uno en tiempo real y soporta backpressure (ideal para grandes volúmenes o clientes que procesan lento).
- **Búsqueda avanzada de productos:**
  ```bash
  curl "http://localhost:8080/api/v1/products/search?name=camisa&category=ropa&minPrice=10&maxPrice=100&available=true&minSales=5&page=1&size=10"
  ```

## Endpoints GraphQL

- Endpoint: `POST http://localhost:8080/graphql`
- No hay interfaz web integrada (como GraphiQL o Playground). Usa herramientas externas como [Altair](https://altair.sirmuel.design/), [Insomnia](https://insomnia.rest/), [Postman](https://www.postman.com/) o la extensión GraphQL de VS Code para probar queries.
- Ejemplo de query soportada:
  ```graphql
  query {
    products {
      id
      name
      price
      available
    }
  }
  ```
- Ejemplo de request con curl:
  ```bash
  curl -X POST http://localhost:8080/graphql \
    -H "Content-Type: application/json" \
    -d '{ "query": "{ products { id name price available } }" }'
  ```
- Ejemplo de mutation (si tu API lo soporta):
  ```graphql
  mutation {
    createProduct(input: {
      name: "Zapato deportivo",
      description: "Zapatilla running",
      category: "calzado",
      price: 59.99,
      available: true,
      sales: 50
    }) {
      id
      name
      price
    }
  }
  ```

## Paginación y filtrado
- Los endpoints REST y GraphQL soportan paginación 1-based (`page`, `size`).
- Filtros disponibles: `name`, `category`, `minPrice`, `maxPrice`, `available`.
- Respuesta incluye resumen de paginación: `pagination` con `total_records`, `current_page`, `total_pages`, `next_page`, `prev_page`.

## Manejo global de errores
- Errores de validación y negocio se devuelven en formato JSON:
  ```json
  {
    "timestamp": "2024-06-01T12:00:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "El campo name es obligatorio"
  }
  ```

## Pruebas
- Pruebas unitarias y de integración con WebTestClient.
- Ejecuta `mvn test` para validar los endpoints reactivos.

## Estructura recomendada
- `config/` - configuración general y beans
- `controller/` - controladores REST y GraphQL
- `service/` - lógica de negocio
- `repository/` - acceso a datos reactivo
- `model/` - entidades de dominio
- `dto/` - objetos de transferencia
- `exception/` - manejo global de errores

## Notas de escalabilidad
- WebFlux + R2DBC permite manejo no-bloqueante y alta concurrencia.
- Ajusta el pool de conexiones R2DBC en `application.yml` según la carga esperada.

## JavaDoc y pruebas
- Incluye JavaDoc breve en servicios y controladores.
- Pruebas con WebTestClient para endpoints reactivos.
