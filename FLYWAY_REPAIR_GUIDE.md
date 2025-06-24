# Guía de Reparación de Migraciones Flyway

## Problema Común
Cuando el proyecto falla con el siguiente error:
```
Caused by: org.flywaydb.core.api.exception.FlywayValidateException: Validate failed: Migrations have failed validation
Detected failed migration to version 2 (Insert sample data).
Please remove any half-completed changes then run repair to fix the schema history.
```

## ¿Por qué ocurre este error?

Este error puede ocurrir por varias razones:
- La migración se ejecutó parcialmente y falló
- Hay conflictos de integridad referencial en los datos
- El esquema de la tabla `flyway_schema_history` está corrupto
- Cambios manuales en la base de datos que interfieren con las migraciones

## Pasos para Reparar el Proyecto

### Paso 1: Detener la Aplicación
Si la aplicación está ejecutándose, detenerla completamente antes de proceder.

### Paso 2: Reparar el Historial de Flyway
Ejecutar el comando repair para limpiar el historial de migraciones fallidas:

```bash
mvn flyway:repair
```

**¿Qué hace este comando?**
- Limpia las entradas de migraciones fallidas en `flyway_schema_history`
- Elimina checksums inválidos
- Prepara el esquema para una nueva ejecución de migraciones

### Paso 3: Verificar el Estado de las Migraciones
Revisar el estado actual de las migraciones:

```bash
mvn flyway:info
```

**Salida esperada:**
```
| Category  | Version | Description           | Type     | Installed On        | State              |
+-----------+---------+-----------------------+----------+---------------------+--------------------+
| Versioned | 1       | Create base tables    | SQL      |                     | Ignored (Baseline) |
|           | 1       | << Flyway Baseline >> | BASELINE | 2025-06-24 20:15:07 | Baseline           |
| Versioned | 2       | Insert sample data    | SQL      |                     | Pending            |
```

### Paso 4: Aplicar las Migraciones Pendientes
Ejecutar las migraciones que están pendientes:

```bash
mvn flyway:migrate
```

**Salida esperada:**
```
Successfully applied 1 migration to schema `ecommerce_db`, now at version v2
```

### Paso 5: Verificar el Estado Final
Confirmar que todas las migraciones se aplicaron correctamente:

```bash
mvn flyway:info
```

**Salida esperada:**
```
| Category  | Version | Description           | Type     | Installed On        | State              |
+-----------+---------+-----------------------+----------+---------------------+--------------------+
| Versioned | 1       | Create base tables    | SQL      |                     | Ignored (Baseline) |
|           | 1       | << Flyway Baseline >> | BASELINE | 2025-06-24 20:15:07 | Baseline           |
| Versioned | 2       | Insert sample data    | SQL      | 2025-06-24 20:31:18 | Success            |
```

### Paso 6: Iniciar la Aplicación
Una vez que las migraciones estén en estado "Success", iniciar la aplicación:

```bash
mvn spring-boot:run
```

O usando la tarea de VS Code:
- Abrir la paleta de comandos (`Ctrl+Shift+P`)
- Buscar "Tasks: Run Task"
- Seleccionar "run"

## Comandos de Emergencia

### Si el repair no funciona:
1. **Limpiar completamente Flyway** (⚠️ CUIDADO: Solo en desarrollo):
```bash
mvn flyway:clean
mvn flyway:migrate
```

2. **Verificar la conexión a la base de datos:**
```bash
mvn flyway:validate
```

3. **Ver información detallada:**
```bash
mvn flyway:info -X
```

## Verificación Final

### Comprobar que la aplicación funciona:
1. La aplicación debe iniciar sin errores
2. Verificar endpoint de salud: `http://localhost:8080/actuator/health`
3. Verificar que los datos de prueba están cargados:
   - Productos: `http://localhost:8080/api/v1/products`
   - GraphQL: `http://localhost:8080/graphql`

### Ejecutar Tests
```bash
mvn test
```

## Configuración Relevante

### application.yml
```yaml
spring:
  flyway:
    url: jdbc:mysql://localhost:3306/ecommerce_db
    user: root
    password: password
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### Archivos de Migración
- `V1__Create_base_tables.sql` - Crea la estructura de tablas
- `V2__Insert_sample_data.sql` - Inserta datos de prueba

## Prevención

Para evitar este problema en el futuro:
1. **No editar manualmente** los archivos de migración una vez aplicados
2. **No modificar directamente** la base de datos cuando hay migraciones activas
3. **Siempre** usar `mvn flyway:info` antes de hacer cambios
4. **Crear nuevas migraciones** en lugar de modificar las existentes

## Notas Importantes

- ⚠️ **NUNCA** usar `flyway:clean` en producción
- ⚠️ Los comandos de repair pueden requerir limpieza manual adicional
- ✅ Siempre verificar el estado con `flyway:info` después de cualquier operación
- ✅ Mantener respaldos de la base de datos antes de operaciones de migración

## Contacto y Soporte

Si los pasos anteriores no resuelven el problema:
1. Revisar los logs completos de la aplicación
2. Verificar la conectividad a MySQL
3. Comprobar que la base de datos `ecommerce_db` existe
4. Verificar permisos del usuario de base de datos
