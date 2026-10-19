package co.edu.uniquindio.rutaverde.modelo;

import java.util.List;

public interface ProductoTuristico {

    String getCodigo();

    String getNombre();

    double calcularPrecioPorPersona();

    double calcularDuracionHoras();

    List<Actividad> obtenerActividades();

    boolean estaDisponible();

    boolean contiene(ProductoTuristico producto);

    String getTipo();
}