package co.edu.uniquindio.rutaverde.modelo.pago;

public interface PasarelaPago {

    ResultadoPago cobrar(String numeroTarjeta, double valorPesos, String referencia);
}
