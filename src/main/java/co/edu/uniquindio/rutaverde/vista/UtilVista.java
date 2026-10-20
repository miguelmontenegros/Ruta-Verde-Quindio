package co.edu.uniquindio.rutaverde.vista;

import co.edu.uniquindio.rutaverde.modelo.ReglaNegocioException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.util.Optional;

public class UtilVista {

    public static double leerDecimal(TextField campo, String nombre) {
        try {
            return Double.parseDouble(campo.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException(nombre + " debe ser un número");
        }
    }

    public static int leerEntero(TextField campo, String nombre) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException(nombre + " debe ser un número entero");
        }
    }

    public static void informar(String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, "Información", mensaje);
    }

    public static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, "Error", mensaje);
    }

    public static boolean confirmar(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alerta.setHeaderText(null);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.YES;
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
