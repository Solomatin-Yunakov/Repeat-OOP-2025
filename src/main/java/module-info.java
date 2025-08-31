module Reapeat.OO {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;

    opens sd2 to javafx.fxml;
    opens sd2.GUI to javafx.fxml;
    exports sd2.DAOs;
    exports sd2.BusinessObjects;
    exports sd2.DTOs;
    exports sd2.Exceptions;
    exports sd2.GUI;
}