module com.foodorder.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires okhttp3;
    requires com.google.gson;

    opens com.foodorder.client to javafx.fxml;
    opens com.foodorder.client.controller to javafx.fxml;
    exports com.foodorder.client;
    exports com.foodorder.client.controller;
}
