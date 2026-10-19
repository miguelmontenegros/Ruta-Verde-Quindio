package co.edu.uniquindio.rutaverde.modelo;

public enum EstadoActividad {
    DISPONIBLE("Disponible"),
    SUSPENDIDA("Suspendida");

    private final String etiqueta;

    EstadoActividad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
