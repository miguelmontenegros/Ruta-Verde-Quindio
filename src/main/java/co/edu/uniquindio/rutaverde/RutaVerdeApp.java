package co.edu.uniquindio.rutaverde;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RutaVerdeApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(RutaVerdeApp.class.getResource("/co/edu/uniquindio/rutaverde/vista/PrincipalVista.fxml"));
        Scene scene = new Scene(loader.load(), 1180, 720);
        stage.setTitle("RutaVerde Quindío - Gestión de reservas");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}