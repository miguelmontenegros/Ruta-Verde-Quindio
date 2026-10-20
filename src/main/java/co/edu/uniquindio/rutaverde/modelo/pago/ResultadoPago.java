package co.edu.uniquindio.rutaverde.modelo.pago;

public class ResultadoPago {

    private final boolean aprobado;
    private final String codigoAutorizacion;
    private final String motivo;

    public ResultadoPago(boolean aprobado, String codigoAutorizacion, String motivo) {
        this.aprobado = aprobado;
        this.codigoAutorizacion = codigoAutorizacion;
        this.motivo = motivo;
    }

    public boolean isAprobado() { return aprobado; }
    public String getCodigoAutorizacion() { return codigoAutorizacion; }
    public String getMotivo() { return motivo; }
}
