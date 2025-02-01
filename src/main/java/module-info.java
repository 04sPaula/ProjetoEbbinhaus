module com.paula {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
	requires javafx.graphics;
	requires javafx.base;
    opens com.paula.ebbinhaus to javafx.fxml;
    opens com.paula.ebbinhaus.classes to javafx.base;
    exports com.paula.ebbinhaus;
    exports com.paula.ebbinhaus.classes;
    exports com.paula.ebbinhaus.telas;
}