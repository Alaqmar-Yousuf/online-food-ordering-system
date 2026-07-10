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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import okhttp3.Response;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML private Label totalOrdersLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<Map<String, Object>> revenueTable;
    @FXML private TableColumn<Map<String, Object>, String> colRevName;
    @FXML private TableColumn<Map<String, Object>, String> colRevOrders;
    @FXML private TableColumn<Map<String, Object>, String> colRevTotal;

    @FXML private TableView<Map<String, Object>> customerTable;
    @FXML private TableColumn<Map<String, Object>, String> colCusName;
    @FXML private TableColumn<Map<String, Object>, String> colCusOrders;
    @FXML private TableColumn<Map<String, Object>, String> colCusSpent;

    @FXML private TableView<Map<String, Object>> riderTable;
    @FXML private TableColumn<Map<String, Object>, String> colRidName;
    @FXML private TableColumn<Map<String, Object>, String> colRidDeliveries;

    private final ObservableList<Map<String, Object>> revenueList = FXCollections.observableArrayList();
    private final ObservableList<Map<String, Object>> customerList = FXCollections.observableArrayList();
    private final ObservableList<Map<String, Object>> riderList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Revenue Table
        colRevName.setCellValueFactory(d -> str(d.getValue().get("restaurantName")));
        colRevOrders.setCellValueFactory(d -> str(d.getValue().get("totalOrders")));
        colRevTotal.setCellValueFactory(d -> currency(d.getValue().get("totalRevenue")));

        // Customer Table
        colCusName.setCellValueFactory(d -> str(d.getValue().get("customerName")));
        colCusOrders.setCellValueFactory(d -> str(d.getValue().get("totalOrders")));
        colCusSpent.setCellValueFactory(d -> currency(d.getValue().get("totalSpent")));

        // Rider Table
        colRidName.setCellValueFactory(d -> str(d.getValue().get("riderName")));
        colRidDeliveries.setCellValueFactory(d -> str(d.getValue().get("totalDeliveries")));

        revenueTable.setItems(revenueList);
        customerTable.setItems(customerList);
        riderTable.setItems(riderList);

        loadAnalytics();
    }

    private SimpleStringProperty str(Object val) {
        return new SimpleStringProperty(val != null ? val.toString().replace(".0", "") : "0");
    }

    private SimpleStringProperty currency(Object val) {
        if (val == null) return new SimpleStringProperty("$0.00");
        try {
            return new SimpleStringProperty(String.format("$%.2f", Double.parseDouble(val.toString())));
        } catch (Exception e) {
            return new SimpleStringProperty(val.toString());
        }
    }

    private void loadAnalytics() {
        statusLabel.setText("Loading analytics...");

        new Thread(() -> {
            try {
                // Revenue
                Response revResp = ApiClient.get("/analytics/admin/revenue");
                if (revResp.isSuccessful() && revResp.body() != null) {
                    List<Map<String, Object>> list = ApiClient.getGson().fromJson(revResp.body().string(), List.class);
                    Platform.runLater(() -> {
                        revenueList.setAll(list);
                        double totalRev = list.stream().mapToDouble(m -> Double.parseDouble(m.getOrDefault("totalRevenue", "0").toString())).sum();
                        long totalOrd = list.stream().mapToLong(m -> (long)Double.parseDouble(m.getOrDefault("totalOrders", "0").toString())).sum();
                        totalRevenueLabel.setText(String.format("$%.2f", totalRev));
                        totalOrdersLabel.setText(String.valueOf(totalOrd));
                    });
                }

                // Customers
                Response cusResp = ApiClient.get("/analytics/admin/customers");
                if (cusResp.isSuccessful() && cusResp.body() != null) {
                    List<Map<String, Object>> list = ApiClient.getGson().fromJson(cusResp.body().string(), List.class);
                    Platform.runLater(() -> customerList.setAll(list));
                }

                // Riders
                Response ridResp = ApiClient.get("/analytics/admin/riders");
                if (ridResp.isSuccessful() && ridResp.body() != null) {
                    List<Map<String, Object>> list = ApiClient.getGson().fromJson(ridResp.body().string(), List.class);
                    Platform.runLater(() -> riderList.setAll(list));
                }

                Platform.runLater(() -> statusLabel.setText("✓ Data up to date"));
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error loading data"));
            }
        }).start();
    }

    @FXML
    private void refreshAnalytics() {
        loadAnalytics();
    }

    @FXML
    private void handleLogout() {
        try {
            ApiClient.setJwtToken(null);
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) revenueTable.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
