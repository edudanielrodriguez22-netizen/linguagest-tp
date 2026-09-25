# LinguaGest — Prototipo

Prototipo funcional en Java con persistencia en MySQL del sistema de gestión académica
para el Instituto de Idiomas, desarrollado como parte del Trabajo Práctico de
Análisis y Diseño de Software (Etapas 1 y 2, PUD).

## Requisitos

- JDK 17 o superior
- MySQL 8.0 o superior
- Maven (o un IDE que lo gestione: IntelliJ, Eclipse, NetBeans)

## 1. Crear la base de datos

Ejecutar en MySQL, en este orden:

```bash
mysql -u root -p < sql/01_crear_tablas.sql
mysql -u root -p < sql/02_datos_prueba.sql
```

(`sql/03_consultas.sql` contiene las consultas documentadas en el informe, para probarlas manualmente si se desea.)

## 2. Configurar la conexión

Editar `src/main/resources/config.properties` con el usuario y la contraseña de tu MySQL local:

```properties
db.url=jdbc:mysql://localhost:3306/linguagest?useSSL=false&serverTimezone=UTC
db.user=root
db.password=TU_PASSWORD_AQUI
```

## 3. Compilar y ejecutar

Con Maven:

```bash
mvn clean package
java -cp target/linguagest.jar linguagest.Main
```

O bien, importar el proyecto como Maven Project en tu IDE y ejecutar la clase `linguagest.Main`.

## Estructura del proyecto

```
linguagest-java/
├── pom.xml
├── sql/
│   ├── 01_crear_tablas.sql
│   ├── 02_datos_prueba.sql
│   └── 03_consultas.sql
└── src/main/
    ├── java/linguagest/
    │   ├── Main.java                  → menú de consola (prototipo operacional)
    │   ├── model/                     → clases de entidad (Alumno, Curso, CursoPresencial, CursoVirtual, Instituto)
    │   ├── db/ConexionBD.java         → conexión JDBC
    │   ├── dao/                       → acceso a datos (una clase por tabla)
    │   └── control/GestorAsistencia.java → clase de control (caso de uso "Registrar asistencia")
    └── resources/config.properties
```

## Correspondencia con el informe (Trabajo Practico Nº 2)

| Documento | Código |
|---|---|
| Diagrama de secuencia (CU "Registrar asistencia") | `control/GestorAsistencia.java` |
| Diagrama de clases de diseño | `model/Curso.java`, `CursoPresencial.java`, `CursoVirtual.java`, `Alumno.java`, `Instituto.java` |
| Creación de tablas MySQL | `sql/01_crear_tablas.sql` |
| Inserción / consulta / borrado | `sql/02_datos_prueba.sql`, métodos de los DAO |
| Presentación de consultas SQL | `sql/03_consultas.sql`, métodos `listarInscriptosPorCurso`, `historialPorAlumno`, `promedioPorCurso`, `cursosConCupoDisponible` |
