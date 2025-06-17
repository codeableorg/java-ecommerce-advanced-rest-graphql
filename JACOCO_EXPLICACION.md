# ¿Cómo interpretar y mejorar el reporte de cobertura JaCoCo?

## ¿Qué es JaCoCo?
JaCoCo es una herramienta que mide cuántas líneas y ramas de tu código son ejecutadas por tus tests. Así puedes saber qué partes de tu aplicación están bien probadas y cuáles no.

## ¿Cómo generar el reporte?
1. Ejecuta los tests y genera el reporte:
   ```sh
   mvn clean test jacoco:report
   ```
2. Abre el archivo `target/site/jacoco/index.html` en tu navegador.

## ¿Cómo leer el reporte?
- Verás una tabla con los paquetes y clases de tu proyecto.
- Las columnas principales son:
  - **Instruction**: porcentaje de instrucciones ejecutadas.
  - **Branch**: porcentaje de ramas (if/else, switch) cubiertas.
  - **Line**: porcentaje de líneas cubiertas.
  - **Method**: porcentaje de métodos cubiertos.
- Haz clic en una clase para ver el detalle por método y línea.
- **Verde**: cubierto por tests. **Rojo**: no cubierto. **Amarillo**: parcialmente cubierto.

## ¿Cómo mejorar la cobertura?
- Escribe tests para los métodos y ramas en rojo.
- Prioriza la lógica de negocio (servicios, controladores, repositorios).
- Usa mocks para dependencias externas.
- No busques 100% de cobertura, enfócate en lo relevante.

## Ciclo recomendado
1. Ejecuta los tests y genera el reporte.
2. Revisa las clases/métodos en rojo.
3. Escribe tests para esos métodos/casos.
4. Repite hasta alcanzar el nivel de cobertura deseado.
