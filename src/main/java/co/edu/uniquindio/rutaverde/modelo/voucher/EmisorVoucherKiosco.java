package co.edu.uniquindio.rutaverde.modelo.voucher;

import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Reserva;

public class EmisorVoucherKiosco extends EmisorVoucher {

    @Override
    protected Voucher crearVoucher(Reserva reserva, Agencia agencia) {
        return new TiqueteKiosco(reserva, agencia);
    }

    @Override
    public String getNombreCanal() {
        return "Kiosco terminal de Armenia";
    }
}
