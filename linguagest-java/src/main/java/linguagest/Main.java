package linguagest;

import linguagest.control.GestorAsistencia;
import linguagest.dao.*;
import linguagest.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Prototipo operacional de LinguaGest (consola).
 * Requiere que la base de datos "linguagest" ya exista (ver sql/01_crear_tablas.sql
 * y, opcionalmente, sql/02_datos_prueba.sql) y que config.properties tenga
 * los datos de conexion correctos.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private static final CursoDAO cursoDAO = new CursoDAO();
    private static final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private static final CalificacionDAO calificacionDAO = new CalificacionDAO();
    private static final GestorAsistencia gestorAsistencia = new GestorAsistencia();

    public static void main(String[] args) {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> altaAlumno();
                    case 2 -> altaCurso();
                    case 3 -> inscribirAlumno();
                    case 4 -> registrarAsistencia();
                    case 5 -> consultarCursosDisponibles();
                    case 6 -> consultarHistorialAsistencia();
                    case 7 -> cargarCalificacion();
                    case 8 -> consultarPromedioCurso();
                    case 0 -> System.out.println("Saliendo...");
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n===== LinguaGest — Prototipo =====");
        System.out.println("1. Alta de alumno");
        System.out.println("2. Alta de curso");
        System.out.println("3. Inscribir alumno a curso");
        System.out.println("4. Registrar asistencia");
        System.out.println("5. Consultar cursos con cupo disponible");
        System.out.println("6. Consultar historial de asistencia de un alumno");
        System.out.println("7. Cargar calificacion");
        System.out.println("8. Consultar promedio de un curso");
        System.out.println("0. Salir");
    }

    private static void altaAlumno() throws SQLException {
        System.out.print("Legajo: "); String legajo = sc.nextLine();
        System.out.print("Nombre: "); String nombre = sc.nextLine();
        System.out.print("Apellido: "); String apellido = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        int id = alumnoDAO.insertar(new Alumno(legajo, nombre, apellido, email));
        System.out.println("Alumno creado con id: " + id);
    }

    private static void altaCurso() throws SQLException {
        System.out.print("Idioma: "); String idioma = sc.nextLine();
        System.out.print("Nivel: "); String nivel = sc.nextLine();
        int cupo = leerEntero("Cupo maximo: ");
        System.out.print("Modalidad (presencial/virtual): "); String modalidad = sc.nextLine();

        Curso curso = modalidad.equalsIgnoreCase("virtual")
                ? new CursoVirtual(idioma, nivel, cupo, "Plataforma institucional")
                : new CursoPresencial(idioma, nivel, cupo, "Aula 1");

        int id = cursoDAO.insertar(curso);
        System.out.println("Curso creado con id: " + id);
    }

    private static void inscribirAlumno() throws SQLException {
        int alumnoId = leerEntero("Id de alumno: ");
        int cursoId = leerEntero("Id de curso: ");
        int idInscripcion = inscripcionDAO.inscribirAlumno(alumnoId, cursoId);
        if (idInscripcion > 0) {
            System.out.println("Inscripcion registrada con id: " + idInscripcion);
        }
    }

    private static void registrarAsistencia() throws SQLException {
        int cursoId = leerEntero("Id de curso: ");
        List<Curso> cursos = cursoDAO.listarTodos();
        Curso curso = cursos.stream().filter(c -> c.getCursoId() == cursoId).findFirst().orElse(null);
        if (curso == null) {
            System.out.println("Curso no encontrado.");
            return;
        }

        List<Alumno> inscriptos = inscripcionDAO.listarInscriptosPorCurso(cursoId);
        if (inscriptos.isEmpty()) {
            System.out.println("No hay alumnos inscriptos en ese curso.");
            return;
        }

        List<Alumno> presentes = new ArrayList<>();
        System.out.println("Marcar presentes (s/n) para cada alumno:");
        for (Alumno a : inscriptos) {
            System.out.print("  " + a.getNombre() + " " + a.getApellido() + " presente? (s/n): ");
            String resp = sc.nextLine();
            if (resp.equalsIgnoreCase("s")) {
                presentes.add(a);
            }
        }

        gestorAsistencia.registrarAsistencia(curso, LocalDate.now(), presentes);
    }

    private static void consultarCursosDisponibles() throws SQLException {
        List<String> disponibles = cursoDAO.cursosConCupoDisponible();
        System.out.println("Cursos con cupo disponible:");
        disponibles.forEach(System.out::println);
    }

    private static void consultarHistorialAsistencia() throws SQLException {
        int alumnoId = leerEntero("Id de alumno: ");
        AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
        List<String> historial = asistenciaDAO.historialPorAlumno(alumnoId);
        System.out.println("Historial de asistencia:");
        historial.forEach(System.out::println);
    }

    private static void cargarCalificacion() throws SQLException {
        int alumnoId = leerEntero("Id de alumno: ");
        int cursoId = leerEntero("Id de curso: ");
        double nota = leerDouble("Nota (0 a 10): ");
        int inscripcionId = inscripcionDAO.obtenerInscripcionId(alumnoId, cursoId);
        if (inscripcionId < 0) {
            System.out.println("El alumno no esta inscripto activamente en ese curso.");
            return;
        }
        calificacionDAO.cargarNota(inscripcionId, LocalDate.now(), nota);
        System.out.println("Calificacion cargada.");
    }

    private static void consultarPromedioCurso() throws SQLException {
        int cursoId = leerEntero("Id de curso: ");
        Double promedio = calificacionDAO.promedioPorCurso(cursoId);
        System.out.println(promedio == null ? "Sin calificaciones cargadas." : "Promedio: " + promedio);
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        int valor = Integer.parseInt(sc.nextLine().trim());
        return valor;
    }

    private static double leerDouble(String mensaje) {
        System.out.print(mensaje);
        return Double.parseDouble(sc.nextLine().trim());
    }
}
