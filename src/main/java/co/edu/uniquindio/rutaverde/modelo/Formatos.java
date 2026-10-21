package co.edu.uniquindio.rutaverde.modelo;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formatos {

    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formatos() {
    }

    public static String moneda(double valor) {
        NumberFormat formato = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO"));
        return "$" + formato.format(Math.round(valor));
    }

    public static String horas(double horas) {
        if (horas == Math.floor(horas)) {
            return String.valueOf((long) horas);
        }
        return String.valueOf(horas).replace('.', ',');
    }
}