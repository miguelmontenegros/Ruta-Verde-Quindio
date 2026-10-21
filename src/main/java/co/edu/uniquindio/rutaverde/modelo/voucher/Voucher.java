package co.edu.uniquindio.rutaverde.modelo.voucher;

import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Reserva;

public abstract class Voucher {

    public static final String POLITICA_CANCELACION = "Cancelación sin costo hasta 48 horas antes del viaje.";

    protected final Reserva reserva;
    protected final Agencia agencia;

    protected Voucher(Reserva reserva, Agencia agencia) {
        this.reserva = reserva;
        this.agencia = agencia;
    }

    public final String generar() {
        return generarEncabezado() + "\n" + generarDetalle() + "\n" + generarPie();
    }

    protected String generarEncabezado() {
        return agencia.getNombreComercial() + " · RNT " + agencia.getRnt();
    }

    protected abstract String generarDetalle();

    protected String generarPie() {
        return POLITICA_CANCELACION;
    }
}