module com.example.rescuecats {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.net.http;
    requires org.json;
    requires javafx.media;


    opens com.example.rescuecats to javafx.fxml;
    exports com.example.rescuecats;
    exports com.example.rescuecats.Controller;
    exports com.example.rescuecats.Model;
    exports com.example.rescuecats.Database;
    exports com.example.rescuecats.Service;
    opens com.example.rescuecats.Controller to javafx.fxml;
}