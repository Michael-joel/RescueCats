module com.example.rescuecats {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens com.example.rescuecats to javafx.fxml;
    exports com.example.rescuecats;
    exports com.example.rescuecats.Controller;
    exports com.example.rescuecats.Model;
    exports com.example.rescuecats.Database;
    opens com.example.rescuecats.Controller to javafx.fxml;
}