package co.edu.uniquindio.rutaverde.modelo.pago.externo;

public class PagoAndinoAPI {
    private int consecutivo = 1000;

    public int procesarCobro(String numeroTarjeta, long valorCentavos, String referencia) {
        System.out.println("[PagoAndino] ref " + referencia + " por " + valorCentavos + " centavos");
        if (valorCentavos > 300_000_000L) {
            return 51;
        }
        consecutivo++;
        return 0;

    }

    public String ultimaAutorizacion() {
        return "PA-" + consecutivo;

    }
}
