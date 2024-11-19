module com.example.socialnetworkgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.sctp;


    opens com.example.socialnetworkgui to javafx.fxml;
    exports com.example.socialnetworkgui;
}