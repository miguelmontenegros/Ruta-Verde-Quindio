package co.edu.uniquindio.rutaverde.modelo.voucher;

import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.ReglaNegocioException;
import co.edu.uniquindio.rutaverde.modelo.Reserva;

public abstract class EmisorVoucher {

    public final String emitir(Reserva reserva, Agencia agencia) {
        if (!reserva.estaConfirmada()) {
            throw new ReglaNegocioException("Solo se emite voucher para reservas confirmadas");
        }
        Voucher voucher = crearVoucher(reserva, agencia);
        return voucher.generar();
    }

    protected abstract Voucher crearVoucher(Reserva reserva, Agencia agencia);

    public abstract String getNombreCanal();
}