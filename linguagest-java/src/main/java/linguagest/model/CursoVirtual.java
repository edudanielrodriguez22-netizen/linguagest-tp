package linguagest.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Especializacion de Curso para la modalidad virtual.
 */
public class CursoVirtual extends Curso {

    private String plataforma;

    public CursoVirtual() {
        super();
        this.modalidad = "virtual";
    }

    public CursoVirtual(String idioma, String nivel, int cupoMaximo, String plataforma) {
        super(idioma, nivel, cupoMaximo, "virtual");
        this.plataforma = plataforma;
    }

    public CursoVirtual(int cursoId, String idioma, String nivel, int cupoMaximo, String plataforma) {
        super(cursoId, idioma, nivel, cupoMaximo, "virtual");
        this.plataforma = plataforma;
    }

    public String getPlataforma() { return plataforma; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }

    @Override
    public void registrarAsistencia(LocalDate fecha, List<Alumno> presentes, List<Alumno> inscriptos) {
        System.out.println("[CursoVirtual] Registrando conexion via \"" + plataforma
                + "\" - " + idioma + " " + nivel + " - fecha " + fecha);
        for (Alumno a : inscriptos) {
            boolean estuvoPresente = presentes.stream().anyMatch(p -> p.getAlumnoId() == a.getAlumnoId());
            System.out.println("   " + a.getNombre() + " " + a.getApellido()
                    + " -> " + (estuvoPresente ? "CONECTADO" : "AUSENTE"));
        }
    }
}
