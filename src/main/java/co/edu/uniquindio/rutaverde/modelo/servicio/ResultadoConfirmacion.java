package co.edu.uniquindio.rutaverde.modelo.servicio;

import co.edu.uniquindio.rutaverde.modelo.Reserva;

public class ResultadoConfirmacion {

    private final boolean exitosa;
    private final String mensaje;
    private final Reserva reserva;
    private final String voucher;

    public ResultadoConfirmacion(boolean exitosa, String mensaje, Reserva reserva, String voucher) {
        this.exitosa = exitosa;
        this.mensaje = mensaje;
        this.reserva = reserva;
        this.voucher = voucher;
    }

    public boolean isExitosa() { return exitosa; }
    public String getMensaje() { return mensaje; }
    public Reserva getReserva() { return reserva; }
    public String getVoucher() { return voucher; }
}
