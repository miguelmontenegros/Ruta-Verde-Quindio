package co.edu.uniquindio.rutaverde.modelo.voucher;

import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Reserva;

public class EmisorVoucherAsesor extends EmisorVoucher {

    @Override
    protected Voucher crearVoucher(Reserva reserva, Agencia agencia) {
        return new VoucherDetallado(reserva, agencia);
    }

    @Override
    public String getNombreCanal() {
        return "Pantalla del asesor";
    }
}