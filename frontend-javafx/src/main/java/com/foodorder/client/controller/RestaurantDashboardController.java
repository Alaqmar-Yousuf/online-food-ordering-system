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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import okhttp3.Response;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RestaurantDashboardController implements Initializable {

    @FXML private Label activeOrdersLabel;
    @FXML private Label todayRevenueLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<Map<String, Object>> ordersTable;
    @FXML private TableColumn<Map<String, Object>, String> colOrderId;
    @FXML private TableColumn<Map<String, Object>, String> colUser;
    @FXML private TableColumn<Map<String, Object>, String> colTotal;
    @FXML private TableColumn<Map<String, Object>, String> colStatus;
    @FXML private TableColumn<Map<String, Object>, Void> colAction;

    private final ObservableList<Map<String, Object>> ordersList = FXCollections.observableArrayList();
    private ScheduledExecutorService scheduler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colOrderId.setCellValueFactory(d -> str(d.getValue().get("id")));
        colUser.setCellValueFactory(d -> str(d.getValue().get("username")));
        colTotal.setCellValueFactory(d -> currency(d.getValue().get("totalAmount")));
        colStatus.setCellValueFactory(d -> str(d.getValue().get("status")));

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button prepareBtn = new Button("Start Preparing");
            private final Button readyBtn = new Button("Ready for Pickup");
            
            {
                prepareBtn.getStyleClass().addAll("primary-button");
                readyBtn.getStyleClass().addAll("primary-button");
                prepareBtn.setStyle("-fx-background-color: #3b82f6;"); // Blue
                readyBtn.setStyle("-fx-background-color: #f97316;"); // Orange
                
                prepareBtn.setOnAction(e -> updateStatus(getTableView().getItems().get(getIndex()), "PREPARING"));
                readyBtn.setOnAction(e -> updateStatus(getTableView().getItems().get(getIndex()), "READY_FOR_PICKUP"));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Map<String, Object> order = getTableView().getItems().get(getIndex());
                    String status = (String) order.get("status");
                    if ("PENDING".equals(status)) {
                        setGraphic(prepareBtn);
                    } else if ("PREPARING".equals(status)) {
                        setGraphic(readyBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        ordersTable.setItems(ordersList);

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
                Response response = ApiClient.get("/orders/restaurant");
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> orders = ApiClient.getGson().fromJson(response.body().string(), List.class);
                    Platform.runLater(() -> {
                        // Sort newest first
                        orders.sort((a, b) -> {
                            Double idA = Double.parseDouble(a.get("id").toString());
                            Double idB = Double.parseDouble(b.get("id").toString());
                            return idB.compareTo(idA);
                        });
                        
                        ordersList.setAll(orders);
                        
                        long active = orders.stream().filter(o -> {
                            String s = (String) o.get("status");
                            return "PENDING".equals(s) || "PREPARING".equals(s);
                        }).count();
                        activeOrdersLabel.setText(String.valueOf(active));
                        
                        double revenue = orders.stream()
                                .filter(o -> !"CANCELLED".equals(o.get("status")))
                                .mapToDouble(o -> Double.parseDouble(o.getOrDefault("totalAmount", "0").toString()))
                                .sum();
                        todayRevenueLabel.setText(String.format("$%.2f", revenue));
                        
                        statusLabel.setText("✓ Synced");
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Connection error"));
            }
        }).start();
    }

    private void updateStatus(Map<String, Object> order, String newStatus) {
        String orderId = order.get("id").toString().replace(".0", "");
        new Thread(() -> {
            try {
                Response response = ApiClient.put("/orders/restaurant/" + orderId + "/status?status=" + newStatus, null);
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        order.put("status", newStatus);
                        ordersTable.refresh();
                        statusLabel.setText("✓ Order updated");
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
            Stage stage = (Stage) ordersTable.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
