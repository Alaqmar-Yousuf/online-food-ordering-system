package com.foodorder.client.controller;

import com.foodorder.client.MainApp;
import com.foodorder.client.network.ApiClient;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import okhttp3.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutController {

    @FXML private ListView<MainController.CartItem> receiptListView;
    @FXML private Label restaurantNameLabel;
    @FXML private Label grandTotalLabel;
    @FXML private Label statusLabel;

    @FXML private TextField nameField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;

    @FXML private VBox paymentFormBox;
    @FXML private VBox receiptSuccessBox;
    
    @FXML private Label receiptRestaurantLabel;
    @FXML private Label receiptDateLabel;
    @FXML private ListView<MainController.CartItem> receiptItemsListView;
    @FXML private Label receiptTotalLabel;

    private List<Map<String, Object>> payloadItems = new java.util.ArrayList<>();
    private Long restaurantId;
    private ObservableList<MainController.CartItem> displayItems;
    private double total;

    public void initData(ObservableList<MainController.CartItem> displayItems, double total, Long restId, String restName) {
        this.displayItems = displayItems;
        this.restaurantId = restId;
        this.total = total;
        
        for (MainController.CartItem item : displayItems) {
            Map<String, Object> payloadItem = new HashMap<>();
            payloadItem.put("menuItemId", item.id);
            payloadItem.put("quantity", 1);
            this.payloadItems.add(payloadItem);
        }

        receiptListView.setItems(displayItems);
        receiptListView.setCellFactory(lv -> new ListCell<MainController.CartItem>() {
            @Override
            protected void updateItem(MainController.CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: #ffffff;");
                }
            }
        });

        restaurantNameLabel.setText(restName);
        grandTotalLabel.setText(String.format("$%.2f", total));
    }

    @FXML
    private void handlePay() {
        String name = nameField.getText().trim();
        String card = cardNumberField.getText().trim();
        String expiry = expiryField.getText().trim();
        String cvv = cvvField.getText().trim();

        if (name.isEmpty() || card.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            statusLabel.setText("Please fill in all payment details.");
            statusLabel.setTextFill(Color.RED);
            return;
        }
        if (card.replaceAll("\\s", "").length() < 12) {
            statusLabel.setText("Invalid card number.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        statusLabel.setText("Processing payment...");
        statusLabel.setTextFill(Color.web("#333333"));

        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("restaurantId", restaurantId);
        orderRequest.put("items", payloadItems);

        new Thread(() -> {
            try {
                Response response = ApiClient.post("/orders", orderRequest);
                Platform.runLater(() -> {
                    if (response.isSuccessful()) {
                        showReceipt();
                    } else {
                        statusLabel.setText("Payment failed. Please try again.");
                        statusLabel.setTextFill(Color.RED);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Network error. Check connection.");
                    statusLabel.setTextFill(Color.RED);
                });
            }
        }).start();
    }

    private void showReceipt() {
        paymentFormBox.setVisible(false);
        paymentFormBox.setManaged(false);

        // Populate receipt
        receiptRestaurantLabel.setText(restaurantNameLabel.getText());
        receiptDateLabel.setText("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
        receiptItemsListView.setItems(displayItems);
        receiptItemsListView.setCellFactory(lv -> new ListCell<MainController.CartItem>() {
            @Override
            protected void updateItem(MainController.CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: #ffffff;");
                }
            }
        });
        receiptTotalLabel.setText(String.format("$%.2f", total));

        receiptSuccessBox.setVisible(true);
        receiptSuccessBox.setManaged(true);
    }

    @FXML
    private void handleCancel() {
        try {
            MainApp.setRoot("/fxml/main.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturnHome() {
        try {
            MainApp.setRoot("/fxml/main.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
