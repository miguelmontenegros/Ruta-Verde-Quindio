package co.edu.uniquindio.rutaverde;

import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.pago.PagoAndinoAdapter;
import co.edu.uniquindio.rutaverde.modelo.pago.PasarelaPago;
import co.edu.uniquindio.rutaverde.modelo.pago.externo.PagoAndinoAPI;
import co.edu.uniquindio.rutaverde.modelo.servicio.FachadaReservas;
import co.edu.uniquindio.rutaverde.modelo.servicio.NotificadorCorreoSimulado;
import co.edu.uniquindio.rutaverde.modelo.servicio.ServicioReportes;

public class ContextoAplicacion {

    private static ContextoAplicacion instancia;

    private final Agencia agencia;
    private final NotificadorCorreoSimulado notificador;
    private final FachadaReservas fachadaReservas;
    private final ServicioReportes servicioReportes;

    private ContextoAplicacion() {
        agencia = new Agencia("RutaVerde Quindío", "900.456.789-1", "54321",
                "Carrera 14 # 21-30, Armenia, Quindío", "(606) 741 2233",
                "reservas@rutaverdequindio.co", "www.rutaverdequindio.co");
        notificador = new NotificadorCorreoSimulado();
        PasarelaPago pasarela = new PagoAndinoAdapter(new PagoAndinoAPI());
        fachadaReservas = new FachadaReservas(agencia, pasarela, notificador);
        servicioReportes = new ServicioReportes(agencia);
    }

    public static ContextoAplicacion getInstancia() {
        if (instancia == null) {
            instancia = new ContextoAplicacion();
            DatosIniciales.cargar(instancia);
        }
        return instancia;
    }

    public Agencia getAgencia() { return agencia; }
    public NotificadorCorreoSimulado getNotificador() { return notificador; }
    public FachadaReservas getFachadaReservas() { return fachadaReservas; }
    public ServicioReportes getServicioReportes() { return servicioReportes; }
}