package co.edu.uniquindio.rutaverde.modelo.voucher;

import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Formatos;
import co.edu.uniquindio.rutaverde.modelo.Reserva;

public class TiqueteKiosco extends Voucher {

    public TiqueteKiosco(Reserva reserva, Agencia agencia) {
        super(reserva, agencia);
    }

    @Override
    protected String generarDetalle() {
        return reserva.getNumeroFormateado() + " | " + reserva.getProducto().getNombre()
                + " | " + reserva.getFechaViaje().format(Formatos.FECHA)
                + " | " + reserva.getNumeroViajeros() + " viajero(s)"
                + " | CÓDIGO DE ENTRADA: " + generarCodigoEntrada();
    }

    String generarCodigoEntrada() {
        return "K" + reserva.getNumero() + "-" + reserva.getCliente().getDocumento();
    }
}