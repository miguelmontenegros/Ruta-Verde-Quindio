package co.edu.uniquindio.rutaverde.modelo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Actividad implements ProductoTuristico {

    private final String codigo;
    private String nombre;
    private String municipio;
    private double duracionHoras;
    private double precioPorPersona;
    private int cupoMaximoDia;
    private NivelDificultad dificultad;
    private EstadoActividad estado;
    private final Map<LocalDate, Integer> cuposOcupados = new HashMap<>();

    public Actividad(String codigo, String nombre, String municipio, double duracionHoras,
                     double precioPorPersona, int cupoMaximoDia, NivelDificultad dificultad,
                     EstadoActividad estado) {
        this.codigo = Validaciones.obligatorio(codigo, "El código de la actividad");
        actualizarDatos(nombre, municipio, duracionHoras, precioPorPersona, cupoMaximoDia, dificultad, estado);
    }

    public void actualizarDatos(String nombre, String municipio, double duracionHoras, double precioPorPersona,
                                int cupoMaximoDia, NivelDificultad dificultad, EstadoActividad estado) {
        if (duracionHoras <= 0) {
            throw new ReglaNegocioException("La duración debe ser mayor que cero");
        }
        if (precioPorPersona <= 0) {
            throw new ReglaNegocioException("El precio por persona debe ser mayor que cero");
        }
        if (cupoMaximoDia <= 0) {
            throw new ReglaNegocioException("El cupo máximo por día debe ser mayor que cero");
        }
        if (dificultad == null || estado == null) {
            throw new ReglaNegocioException("La dificultad y el estado son obligatorios");
        }
        this.nombre = Validaciones.obligatorio(nombre, "El nombre de la actividad");
        this.municipio = Validaciones.obligatorio(municipio, "El municipio");
        this.duracionHoras = duracionHoras;
        this.precioPorPersona = precioPorPersona;
        this.cupoMaximoDia = cupoMaximoDia;
        this.dificultad = dificultad;
        this.estado = estado;
    }

    @Override
    public double calcularPrecioPorPersona() {
        return precioPorPersona;
    }

    @Override
    public double calcularDuracionHoras() {
        return duracionHoras;
    }

    @Override
    public List<Actividad> obtenerActividades() {
        return List.of(this);
    }

    @Override
    public boolean estaDisponible() {
        return estado == EstadoActividad.DISPONIBLE;
    }

    @Override
    public boolean contiene(ProductoTuristico producto) {
        return this.equals(producto);
    }

    @Override
    public String getTipo() {
        return "Actividad";
    }

    public int consultarCuposDisponibles(LocalDate fecha) {
        return cupoMaximoDia - cuposOcupados.getOrDefault(fecha, 0);
    }

    public boolean hayCupo(LocalDate fecha, int viajeros) {
        return consultarCuposDisponibles(fecha) >= viajeros;
    }

    public void ocuparCupos(LocalDate fecha, int viajeros) {
        if (!hayCupo(fecha, viajeros)) {
            throw new ReglaNegocioException("No hay cupo en '" + nombre + "' para el " + fecha.format(Formatos.FECHA));
        }
        int ocupados = cuposOcupados.getOrDefault(fecha, 0);
        cuposOcupados.put(fecha, ocupados + viajeros);
    }

    public void liberarCupos(LocalDate fecha, int viajeros) {
        int ocupados = cuposOcupados.getOrDefault(fecha, 0);
        cuposOcupados.put(fecha, Math.max(0, ocupados - viajeros));
    }

    @Override
    public String getCodigo() { return codigo; }
    @Override
    public String getNombre() { return nombre; }
    public String getMunicipio() { return municipio; }
    public double getDuracionHoras() { return duracionHoras; }
    public double getPrecioPorPersona() { return precioPorPersona; }
    public int getCupoMaximoDia() { return cupoMaximoDia; }
    public NivelDificultad getDificultad() { return dificultad; }
    public EstadoActividad getEstado() { return estado; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Actividad)) {
            return false;
        }
        return codigo.equals(((Actividad) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "[A] " + codigo + " - " + nombre;
    }
}