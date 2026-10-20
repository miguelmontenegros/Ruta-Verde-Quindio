package co.edu.uniquindio.rutaverde.modelo.pago;

public final class ValidadorLuhn {

    private ValidadorLuhn() {
    }

    public static boolean esValido(String numeroTarjeta) {
        if (numeroTarjeta == null) {
            return false;
        }

        String digitos = numeroTarjeta.replace(" ", "");

        if (digitos.length() < 13 || digitos.length() > 19) {
            return false;
        }
        int suma = 0;
        boolean duplicar = false;

        for (int i = digitos.length() - 1; i >= 0; i--) {
            char caracter = digitos.charAt(i);
            if (!Character.isDigit(caracter)) {
                return false;
            }
            int digito = caracter - '0';
            if (duplicar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            duplicar = !duplicar;
        }

        return suma % 10 == 0;
    }
}
