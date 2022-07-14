module com.anusha.connect4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.anusha.connect4 to javafx.fxml;
    exports com.anusha.connect4;
}