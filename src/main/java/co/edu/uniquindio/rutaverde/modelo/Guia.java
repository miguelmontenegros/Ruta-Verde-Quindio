package co.edu.uniquindio.rutaverde.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Guia {

    public static final int HORAS_POR_DIA = 8;

    private final String identificacion;
    private String nombre;
    private final List<String> idiomas = new ArrayList<>();
    private String telefono;
    private double tarifaDia;

    public Guia(String identificacion, String nombre, List<String> idiomas, String telefono, double tarifaDia) {
        this.identificacion = Validaciones.obligatorio(identificacion, "La identificación del guía");
        actualizarDatos(nombre, idiomas, telefono, tarifaDia);
    }

    public void actualizarDatos(String nombre, List<String> idiomas, String telefono, double tarifaDia) {
        if (idiomas == null || idiomas.isEmpty()) {
            throw new ReglaNegocioException("El guía debe hablar al menos un idioma");
        }
        if (tarifaDia <= 0) {
            throw new ReglaNegocioException("La tarifa por día debe ser mayor que cero");
        }
        this.nombre = Validaciones.obligatorio(nombre, "El nombre del guía");
        this.telefono = Validaciones.obligatorio(telefono, "El teléfono del guía");
        this.tarifaDia = tarifaDia;
        this.idiomas.clear();
        for (String idioma : idiomas) {
            if (idioma != null && !idioma.isBlank()) {
                this.idiomas.add(idioma.trim());
            }
        }
    }

    public boolean hablaIdioma(String idioma) {
        for (String propio : idiomas) {
            if (propio.equalsIgnoreCase(idioma.trim())) {
                return true;
            }
        }
        return false;
    }

    public int calcularDiasCobro(double duracionHoras) {
        return Math.max(1, (int) Math.ceil(duracionHoras / HORAS_POR_DIA));
    }

    public double calcularCosto(double duracionHoras) {
        return calcularDiasCobro(duracionHoras) * tarifaDia;
    }

    public String getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public List<String> getIdiomas() { return Collections.unmodifiableList(idiomas); }
    public String getTelefono() { return telefono; }
    public double getTarifaDia() { return tarifaDia; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Guia)) {
            return false;
        }
        return identificacion.equals(((Guia) o).identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificacion);
    }

    @Override
    public String toString() {
        return nombre + " (" + String.join(", ", idiomas) + ")";
    }
}
