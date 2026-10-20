package co.edu.uniquindio.rutaverde.modelo.servicio;

import co.edu.uniquindio.rutaverde.modelo.Actividad;
import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Formatos;
import co.edu.uniquindio.rutaverde.modelo.ReglaNegocioException;
import co.edu.uniquindio.rutaverde.modelo.Reserva;
import co.edu.uniquindio.rutaverde.modelo.pago.PasarelaPago;
import co.edu.uniquindio.rutaverde.modelo.pago.ResultadoPago;
import co.edu.uniquindio.rutaverde.modelo.pago.ValidadorLuhn;
import co.edu.uniquindio.rutaverde.modelo.voucher.EmisorVoucher;

import java.util.ArrayList;
import java.util.List;

public class FachadaReservas {

    private final Agencia agencia;
    private final PasarelaPago pasarela;
    private final ServicioNotificacion notificador;

    public FachadaReservas(Agencia agencia, PasarelaPago pasarela, ServicioNotificacion notificador) {
        this.agencia = agencia;
        this.pasarela = pasarela;
        this.notificador = notificador;
    }

    public ResultadoConfirmacion confirmarReserva(Reserva reserva, String numeroTarjeta, EmisorVoucher emisor) {
        if (reserva.estaConfirmada()) {
            return new ResultadoConfirmacion(false, "La reserva ya fue confirmada", null, null);
        }
        List<Actividad> cuposDescontados = new ArrayList<>();
        try {
           for (Actividad actividad : reserva.getProducto().obtenerActividades()) {
                if (!actividad.hayCupo(reserva.getFechaViaje(), reserva.getNumeroViajeros())) {
                    throw new ReglaNegocioException("No hay cupo suficiente en '" + actividad.getNombre() + "' para el "
                            + reserva.getFechaViaje().format(Formatos.FECHA) + " (disponibles: "
                            + actividad.consultarCuposDisponibles(reserva.getFechaViaje()) + ")");
                }
            }

           if (!ValidadorLuhn.esValido(numeroTarjeta)) {
                throw new ReglaNegocioException("El número de tarjeta no es válido");
            }

            String referencia = "RV-" + reserva.getCliente().getDocumento() + "-" + System.currentTimeMillis();
            ResultadoPago pago = pasarela.cobrar(numeroTarjeta, reserva.calcularValorTotal(), referencia);
            if (!pago.isAprobado()) {
                throw new ReglaNegocioException("Pago rechazado: " + pago.getMotivo());
            }

           for (Actividad actividad : reserva.getProducto().obtenerActividades()) {
                actividad.ocuparCupos(reserva.getFechaViaje(), reserva.getNumeroViajeros());
                cuposDescontados.add(actividad);
            }

            reserva.confirmar(pago.getCodigoAutorizacion());
            agencia.registrarReserva(reserva);

            String voucher = emisor.emitir(reserva, agencia);

            notificador.enviarCorreo(reserva.getCliente().getCorreo(),
                    "Su reserva " + reserva.getNumeroFormateado() + " está confirmada", voucher);

            return new ResultadoConfirmacion(true, "Reserva " + reserva.getNumeroFormateado() + " confirmada",
                    reserva, voucher);

        } catch (RuntimeException error) {
            for (Actividad actividad : cuposDescontados) {
                actividad.liberarCupos(reserva.getFechaViaje(), reserva.getNumeroViajeros());
            }
            agencia.anularRegistro(reserva);
            notificador.enviarCorreo(reserva.getCliente().getCorreo(),
                    "No fue posible confirmar su reserva", "Motivo: " + error.getMessage());
            return new ResultadoConfirmacion(false, error.getMessage(), null, null);
        }
    }
}
