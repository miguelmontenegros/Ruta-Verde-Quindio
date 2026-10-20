package co.edu.uniquindio.rutaverde.modelo.pago;

import co.edu.uniquindio.rutaverde.modelo.pago.externo.PagoAndinoAPI;

public class PagoAndinoAdapter implements PasarelaPago {

    private final PagoAndinoAPI pagoAndino;

    public PagoAndinoAdapter(PagoAndinoAPI pagoAndino) {
        this.pagoAndino = pagoAndino;
    }

    @Override
    public ResultadoPago cobrar(String numeroTarjeta, double valorPesos, String referencia) {
        long valorCentavos = Math.round(valorPesos * 100);
        int codigo = pagoAndino.procesarCobro(numeroTarjeta.replace(" ", ""), valorCentavos, referencia);
        if (codigo == 0) {
            return new ResultadoPago(true, pagoAndino.ultimaAutorizacion(), "Pago aprobado");
        }
        return new ResultadoPago(false, null, traducirCodigo(codigo));
    }

    static String traducirCodigo(int codigo) {
        if (codigo == 51) {
            return "Fondos insuficientes";
        } else if (codigo == 14) {
            return "Tarjeta inválida";
        } else if (codigo == 91) {
            return "Pasarela de pagos no disponible, intente más tarde";
        }
        return "Pago rechazado por la pasarela (código " + codigo + ")";
    }
}
