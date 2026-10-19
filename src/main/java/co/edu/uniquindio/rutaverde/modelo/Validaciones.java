package co.edu.uniquindio.rutaverde.modelo;

public class Validaciones {

    public static String obligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException(campo + " es obligatorio");
        }
        return valor.trim();
    }
}
