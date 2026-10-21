package co.edu.uniquindio.rutaverde.modelo.voucher;

import co.edu.uniquindio.rutaverde.modelo.Actividad;
import co.edu.uniquindio.rutaverde.modelo.Agencia;
import co.edu.uniquindio.rutaverde.modelo.Formatos;
import co.edu.uniquindio.rutaverde.modelo.Reserva;

public class VoucherDetallado extends Voucher {

    public VoucherDetallado(Reserva reserva, Agencia agencia) {
        super(reserva, agencia);
    }

    @Override
    protected String generarDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------\n");
        sb.append("Reserva: ").append(reserva.getNumeroFormateado()).append('\n');
        sb.append("Cliente: ").append(reserva.getCliente().getNombreCompleto())
                .append(" (").append(reserva.getCliente().getDocumento()).append(")\n");
        sb.append("Producto: ").append(reserva.getProducto().getNombre()).append('\n');
        sb.append("Fecha del viaje: ").append(reserva.getFechaViaje().format(Formatos.FECHA))
                .append(" · Viajeros: ").append(reserva.getNumeroViajeros()).append('\n');
        sb.append("Actividades:\n");
        for (Actividad actividad : reserva.getProducto().obtenerActividades()) {
            sb.append("  - ").append(actividad.getNombre()).append(" (").append(actividad.getMunicipio())
                    .append(", ").append(Formatos.horas(actividad.getDuracionHoras())).append(" h)\n");
        }
        sb.append("Guía: ").append(reserva.getGuia() == null ? "Sin guía"
                : reserva.getGuia().getNombre() + " - " + Formatos.moneda(reserva.calcularValorGuia())).append('\n');
        sb.append("Transporte desde el hotel: ").append(reserva.isIncluyeTransporte()
                ? Formatos.moneda(reserva.calcularValorTransporte()) : "No incluido").append('\n');
        sb.append("Seguro de viaje: ").append(reserva.isIncluyeSeguro()
                ? Formatos.moneda(reserva.calcularValorSeguro()) : "No incluido").append('\n');
        if (!reserva.getObservaciones().isEmpty()) {
            sb.append("Observaciones: ").append(reserva.getObservaciones()).append('\n');
        }
        sb.append("TOTAL PAGADO: ").append(Formatos.moneda(reserva.calcularValorTotal()))
                .append(" · Autorización ").append(reserva.getCodigoAutorizacionPago()).append('\n');
        sb.append("------------------------------------------------");
        return sb.toString();
    }
}