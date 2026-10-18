module co.edu.uniquindio.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.demo to javafx.fxml;
    exports co.edu.uniquindio.demo;
}