package co.edu.uniquindio.rutaverde.modelo.servicio;

import java.util.ArrayList;
import java.util.List;

public class NotificadorCorreoSimulado implements ServicioNotificacion {

    private final List<Correo> enviados = new ArrayList<>();

    @Override
    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        enviados.add(new Correo(destinatario, asunto, mensaje));
        System.out.println("[Correo] Para " + destinatario + ": " + asunto);
    }

    public List<Correo> getEnviados() {
        return enviados;
    }

    public Correo getUltimoEnviado() {
        if (enviados.isEmpty()) {
            return null;
        }
        return enviados.get(enviados.size() - 1);
    }
}
