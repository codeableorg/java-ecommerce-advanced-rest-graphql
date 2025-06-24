# Ecommerce API - Spring Boot WebFlux + R2DBC + GraphQL

## Overview

Modern ecommerce monolith built with Spring Boot WebFlux, R2DBC, and GraphQL. Designed as a workshop-ready application demonstrating microservices preparation, clean architecture, and reactive programming patterns.

## Features

- **Reactive Architecture**: WebFlux + R2DBC for non-blocking operations
- **Dual API**: REST and GraphQL endpoints
- **Complete Ecommerce**: Products, Users, Orders, and Inventory management
- **Professional Database Management**: Flyway migrations
- **Business Logic**: Automatic order total calculations, inventory management
- **Clean Code**: Modular structure ready for microservices extraction

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

## Database Management

The project uses **Flyway** for professional database management:

- `V1__Create_base_tables.sql` - Initial schema creation
- `V2__Insert_sample_data.sql` - Sample data for workshop
- `V3__Add_keycloak_integration.sql.workshop` - Future Keycloak integration (workshop demo)

## API Endpoints

### Products API

| Método | Path                  | Descripción                       |
| ------ | --------------------- | --------------------------------- |
| GET    | /api/v1/products      | List all products                 |
| GET    | /api/v1/products/{id} | Get product by ID                 |
| POST   | /api/v1/products      | Create new product                |
| PUT    | /api/v1/products/{id} | Update product (full replacement) |
| PATCH  | /api/v1/products/{id} | Update product (partial)          |
| DELETE | /api/v1/products/{id} | Delete product                    |

### Users API

| Método | Path                              | Descripción                    |
| ------ | --------------------------------- | ------------------------------ |
| GET    | /api/v1/users                     | List all users                 |
| GET    | /api/v1/users/{id}                | Get user by ID                 |
| GET    | /api/v1/users/username/{username} | Get user by username           |
| GET    | /api/v1/users/email/{email}       | Get user by email              |
| POST   | /api/v1/users                     | Create new user                |
| PUT    | /api/v1/users/{id}                | Update user (full replacement) |
| PATCH  | /api/v1/users/{id}                | Update user (partial)          |
| DELETE | /api/v1/users/{id}                | Delete user                    |

### Orders API

| Método | Path                                 | Descripción                       |
| ------ | ------------------------------------ | --------------------------------- |
| GET    | /api/orders                          | List all orders                   |
| GET    | /api/orders/{id}                     | Get order by ID                   |
| GET    | /api/orders/user/{userId}            | Get orders by user ID             |
| GET    | /api/orders/{orderId}/items          | Get order items                   |
| POST   | /api/orders                          | Create order (with/without items) |
| POST   | /api/orders/{orderId}/items          | Add item to order                 |
| DELETE | /api/orders/{orderId}/items/{itemId} | Remove item from order            |
| PUT    | /api/orders/{orderId}/recalculate    | Recalculate order total           |

### Inventory API

| Método | Path                               | Descripción                 |
| ------ | ---------------------------------- | --------------------------- |
| GET    | /api/inventory                     | List all inventory          |
| GET    | /api/inventory/{id}                | Get inventory by ID         |
| GET    | /api/inventory/product/{productId} | Get inventory by product ID |
| POST   | /api/inventory                     | Create inventory record     |
| PUT    | /api/inventory/{id}                | Update inventory            |
| DELETE | /api/inventory/{id}                | Delete inventory record     |

### API Examples

#### Product Operations

```bash
# Get all products
curl http://localhost:8080/api/v1/products

# Get product by ID
curl http://localhost:8080/api/v1/products/1

# Create product
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Pro",
    "description": "High-performance laptop",
    "price": 1299.99,
    "category": "Electronics",
    "imageUrl": "https://example.com/laptop.jpg"
  }'
```

#### User Operations

```bash
# Get all users
curl http://localhost:8080/api/v1/users

# Create user
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "fullName": "John Doe"
  }'

# Get user by username
curl http://localhost:8080/api/v1/users/username/johndoe

# Update user (partial)
curl -X PATCH http://localhost:8080/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newemail@example.com"
  }'
```

#### Order Operations

```bash
# Create order with items (automatic total calculation)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "status": "PENDING",
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "unitPrice": 149.99
      },
      {
        "productId": 2,
        "quantity": 1,
        "unitPrice": 89.99
      }
    ]
  }'

# Add item to existing order (automatic total recalculation)
curl -X POST http://localhost:8080/api/orders/1/items \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 3,
    "quantity": 1,
    "unitPrice": 49.99
  }'

# Get order with items
curl http://localhost:8080/api/orders/1
curl http://localhost:8080/api/orders/1/items
```

#### Inventory Operations

```bash
# Get all inventory
curl http://localhost:8080/api/inventory

# Get inventory for specific product
curl http://localhost:8080/api/inventory/product/1

# Update inventory
curl -X PUT http://localhost:8080/api/inventory/1 \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "stockQuantity": 100
  }'
```

## GraphQL API

- **Endpoint**: `POST http://localhost:8080/graphql`
- **Tools**: Use [Altair](https://altair.sirmuel.design/), [Insomnia](https://insomnia.rest/), [Postman](https://www.postman.com/) or VS Code GraphQL extension

### Supported Queries

```graphql
# Get all products
query {
  products {
    id
    name
    description
    price
    category
    imageUrl
  }
}

# Get all users
query {
  users {
    id
    username
    email
    fullName
  }
}

# Get all orders
query {
  orders {
    id
    userId
    status
    totalAmount
    orderDate
  }
}

# Get inventory
query {
  inventory {
    id
    productId
    stockQuantity
  }
}

# Get specific items
query {
  productById(id: "1") {
    id
    name
    price
  }

  userById(id: "1") {
    id
    username
    email
  }

  orderById(id: "1") {
    id
    userId
    totalAmount
    status
  }
}
```

### Supported Mutations

```graphql
# Create product
mutation {
  createProduct(
    input: {
      name: "New Product"
      description: "Product description"
      price: 99.99
      category: "Electronics"
    }
  ) {
    id
    name
    price
  }
}

# Create user
mutation {
  createUser(
    input: {
      username: "newuser"
      email: "user@example.com"
      fullName: "New User"
    }
  ) {
    id
    username
    email
  }
}

# Update user
mutation {
  updateUser(
    id: 1
    input: {
      username: "updateduser"
      email: "updated@example.com"
      fullName: "Updated User"
    }
  ) {
    id
    username
    email
  }
}

# Delete operations
mutation {
  deleteProduct(id: 1)
  deleteUser(id: 1)
}
```

### GraphQL with cURL

```bash
# Query example
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ products { id name price } }"
  }'

# Mutation example
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { createUser(input: { username: \"testuser\", email: \"test@example.com\", fullName: \"Test User\" }) { id username email } }"
  }'
```

## Key Features

### Order Management

- **Automatic Total Calculation**: Order totals are calculated from items, ensuring data consistency
- **Inventory Management**: Automatic stock validation and reservation when adding items
- **Stock Validation**: Prevents overselling by checking available inventory
- **Item Management**: Add/remove items with automatic total recalculation and inventory updates
- **Flexible Creation**: Create orders with or without items in a single request
- **Business Logic**: Proper ecommerce workflow with order statuses and inventory tracking
- **Error Handling**: Automatic inventory restoration on transaction failures

### User Management

- **Clean Entity**: Simple user management without external dependencies
- **Multiple Lookups**: Find users by ID, username, or email
- **PATCH Support**: Partial updates for better API design
- **Workshop Ready**: Prepared for Keycloak integration

### Database Management

- **Flyway Migrations**: Professional database versioning
- **Sample Data**: Workshop-ready with realistic test data
- **Schema Evolution**: Prepared for authentication integration

## Error Handling

Global error handling with consistent JSON responses:

```json
{
  "timestamp": "2024-06-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Username and email are required for PUT operation",
  "path": "/api/v1/users/1"
}
```

## Testing

- Unit and integration tests with WebTestClient
- Reactive endpoint validation
- Run tests: `mvn test`

## Project Structure

```
src/
├── main/
│   ├── java/com/ecommerce/
│   │   ├── config/          # Configuration classes
│   │   ├── exception/       # Global error handling
│   │   ├── product/         # Product domain
│   │   │   ├── controller/  # REST & GraphQL controllers
│   │   │   ├── dto/         # Data transfer objects
│   │   │   ├── model/       # Product entity
│   │   │   ├── repository/  # Data access layer
│   │   │   └── service/     # Business logic
│   │   ├── user/            # User domain (same structure)
│   │   ├── order/           # Order domain (same structure)
│   │   └── inventory/       # Inventory domain (same structure)
│   └── resources/
│       ├── application.yml  # Application configuration
│       ├── db/migration/    # Flyway migrations
│       └── graphql/         # GraphQL schema
└── test/                    # Test classes
```

## Workshop Notes

### Microservices Preparation

- **Domain Separation**: Clear boundaries between Product, User, Order, and Inventory
- **Independent Services**: Each domain has its own controller, service, and repository
- **Clean Interfaces**: Well-defined DTOs and API contracts
- **Modular Structure**: Easy to extract into separate services

### Authentication Evolution

- **Current**: Simple user management without external authentication
- **Future**: Ready for Keycloak integration via V3 migration
- **Workshop Flow**: Demonstrate adding authentication to existing system

### Technical Excellence

- **Reactive Stack**: WebFlux + R2DBC for non-blocking operations
- **Professional DB**: Flyway migrations instead of raw SQL
- **Clean Code**: Lombok, proper separation of concerns
- **Testing**: WebTestClient for reactive testing

## Performance Notes

- **Reactive Architecture**: Non-blocking I/O for high concurrency
- **R2DBC Connection Pool**: Configure in `application.yml` based on load
- **Memory Efficiency**: Reactive streams with backpressure support
- **Scalability**: Horizontal scaling ready with stateless design
