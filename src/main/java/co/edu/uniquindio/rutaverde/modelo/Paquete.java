package co.edu.uniquindio.rutaverde.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Paquete implements ProductoTuristico {

    public static final double DESCUENTO_MAXIMO = 20.0;

    private final String codigo;
    private String nombre;
    private double porcentajeDescuento;
    private final List<ProductoTuristico> componentes = new ArrayList<>();
    private final List<Paquete> contenedores = new ArrayList<>();

    public Paquete(String codigo, String nombre, double porcentajeDescuento) {
        this.codigo = Validaciones.obligatorio(codigo, "El código del paquete");
        actualizarDatos(nombre, porcentajeDescuento);
    }

    public void actualizarDatos(String nombre, double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento > DESCUENTO_MAXIMO) {
            throw new ReglaNegocioException("El descuento del paquete debe estar entre 0 % y 20 %");
        }
        this.nombre = Validaciones.obligatorio(nombre, "El nombre del paquete");
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public void agregar(ProductoTuristico producto) {
        if (producto == null) {
            throw new ReglaNegocioException("Debe seleccionar el producto a agregar");
        }
        if (producto.equals(this)) {
            throw new ReglaNegocioException("El paquete '" + nombre + "' no puede contenerse a sí mismo");
        }
        if (producto.contiene(this)) {
            throw new ReglaNegocioException("'" + producto.getNombre() + "' ya contiene a '" + nombre
                    + "': un paquete no puede contenerse a sí mismo, ni directa ni indirectamente");
        }
        for (Paquete plan : obtenerPlanesQueLoIncluyen()) {
            for (Actividad actividad : producto.obtenerActividades()) {
                if (plan.obtenerActividades().contains(actividad)) {
                    throw new ReglaNegocioException("El plan '" + plan.getNombre()
                            + "' ya incluye la actividad '" + actividad.getNombre() + "'");
                }
            }
        }
        componentes.add(producto);
        if (producto instanceof Paquete) {
            ((Paquete) producto).contenedores.add(this);
        }
    }

    public void quitar(ProductoTuristico producto) {
        boolean estaba = componentes.remove(producto);
        if (estaba && producto instanceof Paquete) {
            ((Paquete) producto).contenedores.remove(this);
        }
    }

    private Set<Paquete> obtenerPlanesQueLoIncluyen() {
        Set<Paquete> planes = new LinkedHashSet<>();
        planes.add(this);
        for (Paquete contenedor : contenedores) {
            planes.addAll(contenedor.obtenerPlanesQueLoIncluyen());
        }
        return planes;
    }

    @Override
    public double calcularPrecioPorPersona() {
        double suma = 0;
        for (ProductoTuristico componente : componentes) {
            suma += componente.calcularPrecioPorPersona();
        }
        return suma * (1 - porcentajeDescuento / 100.0);
    }

    @Override
    public double calcularDuracionHoras() {
        double suma = 0;
        for (ProductoTuristico componente : componentes) {
            suma += componente.calcularDuracionHoras();
        }
        return suma;
    }

    @Override
    public List<Actividad> obtenerActividades() {
        List<Actividad> actividades = new ArrayList<>();
        for (ProductoTuristico componente : componentes) {
            actividades.addAll(componente.obtenerActividades());
        }
        return actividades;
    }

    @Override
    public boolean estaDisponible() {
        for (ProductoTuristico componente : componentes) {
            if (!componente.estaDisponible()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contiene(ProductoTuristico producto) {
        if (this.equals(producto)) {
            return true;
        }
        for (ProductoTuristico componente : componentes) {
            if (componente.contiene(producto)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTipo() {
        return "Paquete";
    }

    public boolean tieneActividadesUnicas() {
        List<Actividad> actividades = obtenerActividades();
        return new HashSet<>(actividades).size() == actividades.size();
    }

    @Override
    public String getCodigo() { return codigo; }
    @Override
    public String getNombre() { return nombre; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public List<ProductoTuristico> getComponentes() { return Collections.unmodifiableList(componentes); }
    public boolean estaContenidoEnOtroPaquete() { return !contenedores.isEmpty(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Paquete)) {
            return false;
        }
        return codigo.equals(((Paquete) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "[P] " + codigo + " - " + nombre;
    }
}