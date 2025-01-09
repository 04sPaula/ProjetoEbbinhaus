module com.paula {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
	requires javafx.graphics;
	requires javafx.base;
    opens com.paula.ebbinhaus to javafx.fxml;
    exports com.paula.ebbinhaus;
    
}