package co.edu.uniquindio.rutaverde.modelo.servicio;

public interface ServicioNotificacion {

    void enviarCorreo(String destinatario, String asunto, String mensaje);
}
