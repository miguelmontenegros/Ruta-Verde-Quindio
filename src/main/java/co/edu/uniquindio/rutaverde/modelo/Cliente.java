package co.edu.uniquindio.rutaverde.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Cliente {

    private final String documento;
    private String nombreCompleto;
    private String telefono;
    private String correo;
    private String paisOrigen;
    private String idiomaPreferido;
    private final List<Reserva> reservas = new ArrayList<>();

    public Cliente(String documento, String nombreCompleto, String telefono, String correo, String paisOrigen, String idiomaPreferido) {
        this.documento = Validaciones.obligatorio(documento, "El documento o pasaporte");
        actualizarDatos(nombreCompleto, telefono, correo, paisOrigen, idiomaPreferido);
    }

    public void actualizarDatos(String nombreCompleto, String telefono, String correo, String paisOrigen, String idiomaPreferido) {
        this.nombreCompleto = Validaciones.obligatorio(nombreCompleto, "El nombre");
        this.telefono = Validaciones.obligatorio(telefono, "El teléfono");
        this.correo = Validaciones.obligatorio(correo, "El correo");
        if (!this.correo.contains("@")) {
            throw new ReglaNegocioException("El correo electrónico no es válido: " + correo);
        }
        this.paisOrigen = Validaciones.obligatorio(paisOrigen, "El país de origen");
        this.idiomaPreferido = Validaciones.obligatorio(idiomaPreferido, "El idioma preferido");
    }


    void agregarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    void quitarReserva(Reserva reserva) {
        reservas.remove(reserva);
    }

    public String getDocumento() { return documento; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getPaisOrigen() { return paisOrigen; }
    public String getIdiomaPreferido() { return idiomaPreferido; }
    public List<Reserva> getReservas() { return Collections.unmodifiableList(reservas); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cliente)) {
            return false;
        }
        return documento.equals(((Cliente) o).documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }

    @Override
    public String toString() {
        return documento + " - " + nombreCompleto;
    }
}
