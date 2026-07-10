package com.foodorder.client.controller;

import com.foodorder.client.network.ApiClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import okhttp3.Response;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RiderDashboardController implements Initializable {

    @FXML private Label statusLabel;

    @FXML private TableView<Map<String, Object>> availableTable;
    @FXML private TableColumn<Map<String, Object>, String> colAvId;
    @FXML private TableColumn<Map<String, Object>, String> colAvRest;
    @FXML private TableColumn<Map<String, Object>, String> colAvTotal;
    @FXML private TableColumn<Map<String, Object>, Void> colAvAction;

    @FXML private TableView<Map<String, Object>> myDeliveriesTable;
    @FXML private TableColumn<Map<String, Object>, String> colMyId;
    @FXML private TableColumn<Map<String, Object>, String> colMyUser;
    @FXML private TableColumn<Map<String, Object>, String> colMyStatus;
    @FXML private TableColumn<Map<String, Object>, Void> colMyAction;

    private final ObservableList<Map<String, Object>> availableList = FXCollections.observableArrayList();
    private final ObservableList<Map<String, Object>> myDeliveriesList = FXCollections.observableArrayList();
    private ScheduledExecutorService scheduler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Available Table
        colAvId.setCellValueFactory(d -> str(d.getValue().get("id")));
        colAvRest.setCellValueFactory(d -> {
            Map<String, Object> rest = (Map<String, Object>) d.getValue().get("restaurant");
            return str(rest != null ? rest.get("name") : "Unknown");
        });
        colAvTotal.setCellValueFactory(d -> currency(d.getValue().get("totalAmount")));

        colAvAction.setCellFactory(param -> new TableCell<>() {
            private final Button acceptBtn = new Button("Accept Delivery");
            {
                acceptBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
                acceptBtn.setOnAction(e -> acceptOrder(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : acceptBtn);
            }
        });

        // My Deliveries Table
        colMyId.setCellValueFactory(d -> str(d.getValue().get("id")));
        colMyUser.setCellValueFactory(d -> {
            Map<String, Object> user = (Map<String, Object>) d.getValue().get("user");
            return str(user != null ? user.get("username") : "Unknown");
        });
        colMyStatus.setCellValueFactory(d -> str(d.getValue().get("status")));

        colMyAction.setCellFactory(param -> new TableCell<>() {
            private final Button deliverBtn = new Button("Mark Delivered");
            {
                deliverBtn.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
                deliverBtn.setOnAction(e -> markDelivered(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    String status = (String) getTableView().getItems().get(getIndex()).get("status");
                    setGraphic("DELIVERING".equals(status) ? deliverBtn : null);
                }
            }
        });

        availableTable.setItems(availableList);
        myDeliveriesTable.setItems(myDeliveriesList);

        loadOrders();
        
        // Start polling every 5 seconds
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::loadOrders, 5, 5, TimeUnit.SECONDS);
    }

    private SimpleStringProperty str(Object val) {
        return new SimpleStringProperty(val != null ? val.toString().replace(".0", "") : "—");
    }

    private SimpleStringProperty currency(Object val) {
        if (val == null) return new SimpleStringProperty("$0.00");
        try {
            return new SimpleStringProperty(String.format("$%.2f", Double.parseDouble(val.toString())));
        } catch (Exception e) {
            return new SimpleStringProperty(val.toString());
        }
    }

    @FXML
    private void loadOrders() {
        new Thread(() -> {
            try {
                Response avResp = ApiClient.get("/riders/orders/available");
                Response myResp = ApiClient.get("/riders/orders/my-deliveries");
                
                if (avResp.isSuccessful() && myResp.isSuccessful()) {
                    List<Map<String, Object>> available = ApiClient.getGson().fromJson(avResp.body().string(), List.class);
                    List<Map<String, Object>> myDeliveries = ApiClient.getGson().fromJson(myResp.body().string(), List.class);
                    
                    Platform.runLater(() -> {
                        availableList.setAll(available);
                        myDeliveriesList.setAll(myDeliveries);
                        statusLabel.setText("✓ Synced");
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Connection error"));
            }
        }).start();
    }

    private void acceptOrder(Map<String, Object> order) {
        String orderId = order.get("id").toString().replace(".0", "");
        new Thread(() -> {
            try {
                Response response = ApiClient.put("/riders/orders/" + orderId + "/accept", null);
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        statusLabel.setText("✓ Order accepted");
                        loadOrders();
                    });
                } else {
                    Platform.runLater(() -> statusLabel.setText("Failed to accept order"));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error accepting order"));
            }
        }).start();
    }

    private void markDelivered(Map<String, Object> order) {
        String orderId = order.get("id").toString().replace(".0", "");
        new Thread(() -> {
            try {
                Response response = ApiClient.put("/riders/orders/" + orderId + "/status?status=DELIVERED", null);
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        statusLabel.setText("✓ Order delivered");
                        loadOrders();
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error updating status"));
            }
        }).start();
    }

    @FXML
    private void handleLogout() {
        if (scheduler != null) scheduler.shutdownNow();
        try {
            ApiClient.setJwtToken(null);
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) availableTable.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
