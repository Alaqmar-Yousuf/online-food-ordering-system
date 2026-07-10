package com.foodorder.client.controller;

import com.foodorder.client.network.ApiClient;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.Response;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private VBox restaurantsView;
    @FXML private VBox restaurantsGrid;
    @FXML private javafx.scene.layout.HBox categoryBox;

    @FXML private VBox menuView;
    @FXML private FlowPane menuGrid;
    @FXML private Label restaurantNameLabel;

    @FXML private VBox ordersView;
    @FXML private VBox ordersListContainer;

    @FXML private ListView<CartItem> cartListView;
    @FXML private Label totalLabel;
    @FXML private Label statusLabel;
    @FXML private Label userNameLabel;

    public static class CartItem {
        public String name;
        public Double price;
        public Long id;
        public CartItem(String name, Double price, Long id) {
            this.name = name; this.price = price; this.id = id;
        }
        @Override
        public String toString() {
            return name + "  —  $" + String.format("%.2f", price);
        }
    }

    private final ObservableList<CartItem> cartItemsDisplay = FXCollections.observableArrayList();
    private double currentTotal = 0.0;
    private Long currentRestaurantId = null;
    private String currentRestaurantName = "";

    // Map food item names/categories to specific Unsplash Source image queries (no quota, free)
    private static final Map<String, String> FOOD_IMAGES = new HashMap<>();
    static {
        FOOD_IMAGES.put("Burgers",   "https://source.unsplash.com/200x130/?burger");
        FOOD_IMAGES.put("Chicken",   "https://source.unsplash.com/200x130/?fried+chicken");
        FOOD_IMAGES.put("Pizza",     "https://source.unsplash.com/200x130/?pizza");
        FOOD_IMAGES.put("Sushi",     "https://source.unsplash.com/200x130/?sushi");
        FOOD_IMAGES.put("Tacos",     "https://source.unsplash.com/200x130/?taco");
        FOOD_IMAGES.put("Burritos",  "https://source.unsplash.com/200x130/?burrito");
        FOOD_IMAGES.put("Sides",     "https://source.unsplash.com/200x130/?french+fries");
        FOOD_IMAGES.put("Drinks",    "https://source.unsplash.com/200x130/?cold+drink");
        FOOD_IMAGES.put("Desserts",  "https://source.unsplash.com/200x130/?chocolate+dessert");
        FOOD_IMAGES.put("Specials",  "https://source.unsplash.com/200x130/?quesadilla");
    }

    private static final Map<String, String> RESTAURANT_LOGOS = new HashMap<>();
    static {
        RESTAURANT_LOGOS.put("Burger King",   "https://logo.clearbit.com/burgerking.com");
        RESTAURANT_LOGOS.put("Pizza Hut",     "https://logo.clearbit.com/pizzahut.com");
        RESTAURANT_LOGOS.put("Sushi Master",  "https://source.unsplash.com/200x130/?sushi+restaurant");
        RESTAURANT_LOGOS.put("Taco Bell",     "https://logo.clearbit.com/tacobell.com");
        RESTAURANT_LOGOS.put("KFC",           "https://logo.clearbit.com/kfc.com");
    }

    private List<Map<String, Object>> allRestaurants = new ArrayList<>();
    private boolean isDarkMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cartListView.setItems(cartItemsDisplay);
        cartListView.setCellFactory(lv -> new javafx.scene.control.ListCell<CartItem>() {
            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    Label nameLbl = new Label(item.toString());
                    nameLbl.setStyle("-fx-text-fill: #ffffff;");
                    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
                    javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                    
                    javafx.scene.control.Button removeBtn = new javafx.scene.control.Button("❌");
                    removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #D70F64; -fx-cursor: hand; -fx-font-size: 10px;");
                    removeBtn.setOnAction(e -> {
                        cartItemsDisplay.remove(item);
                        currentTotal -= item.price;
                        if (currentTotal < 0) currentTotal = 0.0;
                        totalLabel.setText(String.format("$%.2f", currentTotal));
                    });
                    
                    hbox.getChildren().addAll(nameLbl, spacer, removeBtn);
                    setGraphic(hbox);
                }
            }
        });
        setupCategoryChips();
        loadRestaurants();
    }

    private void setupCategoryChips() {
        if (categoryBox == null) return;
        String[] categories = {"All", "Pizza", "Burgers", "Sushi", "Healthy", "Desserts", "Chicken"};
        for (String cat : categories) {
            Label chip = new Label(cat);
            chip.getStyleClass().add("category-chip");
            if (cat.equals("All")) chip.getStyleClass().add("active");
            chip.setOnMouseClicked(e -> {
                categoryBox.getChildren().forEach(n -> n.getStyleClass().remove("active"));
                chip.getStyleClass().add("active");
                filterRestaurants(cat);
            });
            categoryBox.getChildren().add(chip);
        }
    }

    private void filterRestaurants(String category) {
        if (category.equals("All")) {
            populateRestaurantsGrid(allRestaurants);
            return;
        }
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> r : allRestaurants) {
            String desc = (String) r.get("description");
            String name = (String) r.get("name");
            if ((desc != null && desc.toLowerCase().contains(category.toLowerCase())) ||
                (name != null && name.toLowerCase().contains(category.toLowerCase()))) {
                filtered.add(r);
            }
        }
        if (filtered.isEmpty() && !allRestaurants.isEmpty()) {
            filtered.add(allRestaurants.get(0)); // Dummy fallback
        }
        populateRestaurantsGrid(filtered);
    }

    private void loadRestaurants() {
        new Thread(() -> {
            try {
                Response response = ApiClient.get("/restaurants");
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> restaurants = ApiClient.getGson().fromJson(response.body().string(), List.class);
                    allRestaurants = restaurants;
                    Platform.runLater(() -> populateRestaurantsGrid(restaurants));
                } else {
                    Platform.runLater(() -> statusLabel.setText("Failed to load restaurants (HTTP " + response.code() + ")"));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Network error: " + e.getMessage()));
            }
        }).start();
    }

    private void populateRestaurantsGrid(List<Map<String, Object>> restaurants) {
        restaurantsGrid.getChildren().clear();
        for (Map<String, Object> r : restaurants) {
            String name = (String) r.get("name");
            String logoUrl = r.get("imageUrl") != null ? (String) r.get("imageUrl") : RESTAURANT_LOGOS.getOrDefault(name, "https://source.unsplash.com/200x130/?restaurant");
            
            Double rating = r.get("rating") != null ? (Double) r.get("rating") : 4.5;
            Integer time = r.get("deliveryTime") != null ? ((Double) r.get("deliveryTime")).intValue() : 30;
            String address = r.get("address") != null ? (String) r.get("address") : "123 Main St";

            javafx.scene.layout.HBox card = createRestaurantCard(name, address, rating, time, logoUrl);
            card.setOnMouseClicked(e -> {
                Long id = ((Double) r.get("id")).longValue();
                currentRestaurantId = id;
                currentRestaurantName = name;
                restaurantNameLabel.setText(name + " Menu");
                loadMenu(id);
                restaurantsView.setVisible(false);
                menuView.setVisible(true);
            });
            restaurantsGrid.getChildren().add(card);
        }
    }

    private void loadMenu(Long restaurantId) {
        menuGrid.getChildren().clear();
        new Thread(() -> {
            try {
                Response response = ApiClient.get("/restaurants/" + restaurantId + "/menu");
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> menuItems = ApiClient.getGson().fromJson(response.body().string(), List.class);
                    Platform.runLater(() -> populateMenuGrid(menuItems));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Failed to load menu."));
            }
        }).start();
    }

    private void populateMenuGrid(List<Map<String, Object>> menuItems) {
        for (Map<String, Object> item : menuItems) {
            Double priceDouble = (Double) item.get("price");
            String price = String.format("$%.2f", priceDouble);
            String category = (String) item.get("category");
            if (category == null) category = "Burgers";
            String imageUrl = item.get("imageUrl") != null ? (String) item.get("imageUrl") : FOOD_IMAGES.getOrDefault(category, "https://source.unsplash.com/200x130/?food");

            javafx.scene.layout.HBox card = createMenuCard((String) item.get("name"), (String) item.get("description"), price, imageUrl);
            final String finalCategory = category;
            card.setOnMouseClicked(e -> {
                Long itemId = ((Double) item.get("id")).longValue();
                addToCart((String) item.get("name"), priceDouble, itemId);
            });
            menuGrid.getChildren().add(card);
        }
    }

    private javafx.scene.layout.HBox createRestaurantCard(String title, String address, Double rating, Integer time, String imageUrl) {
        javafx.scene.layout.HBox card = new javafx.scene.layout.HBox(20);
        card.getStyleClass().add("food-card");
        card.setPadding(new Insets(15, 25, 15, 15));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinHeight(120);
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(false);
        try {
            Image img = new Image(imageUrl, true);
            imageView.setImage(img);
        } catch (Exception e) { }
        
        // Clip image to rounded rect
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(100, 100);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);

        VBox textArea = new VBox(6);
        textArea.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("card-title");
        titleLbl.setWrapText(true);

        Label addrLbl = new Label("📍 " + address);
        addrLbl.getStyleClass().add("subtitle-text");
        
        textArea.getChildren().addAll(titleLbl, addrLbl);
        javafx.scene.layout.HBox.setHgrow(textArea, javafx.scene.layout.Priority.ALWAYS);

        javafx.scene.layout.VBox statsArea = new javafx.scene.layout.VBox(6);
        statsArea.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        Label statsLbl = new Label("⭐ " + rating + "   •   ⏱️ " + time + " mins");
        statsLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaa; -fx-font-weight: bold;");
        
        javafx.scene.control.Button menuBtn = new javafx.scene.control.Button("View Menu");
        menuBtn.setStyle("-fx-background-color: #D70F64; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        
        statsArea.getChildren().addAll(statsLbl, menuBtn);

        card.getChildren().addAll(imageView, textArea, statsArea);
        card.setOnMouseEntered(e -> animateZoom(card, 1.04));
        card.setOnMouseExited(e -> animateZoom(card, 1.0));

        return card;
    }

    private javafx.scene.layout.HBox createMenuCard(String title, String description, String price, String imageUrl) {
        javafx.scene.layout.HBox card = new javafx.scene.layout.HBox(15);
        card.getStyleClass().add("food-card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(340);
        card.setMinHeight(120);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(false);
        try {
            Image img = new Image(imageUrl, true);
            imageView.setImage(img);
        } catch (Exception e) { }
        
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(100, 100);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);

        VBox textArea = new VBox(6);
        textArea.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("card-title");
        titleLbl.setWrapText(true);

        Label descLbl = new Label(description != null ? description : "");
        descLbl.getStyleClass().add("subtitle-text");
        descLbl.setWrapText(true);
        descLbl.setMaxHeight(40);

        Label priceLbl = new Label(price);
        priceLbl.getStyleClass().add("card-price");

        textArea.getChildren().addAll(titleLbl, descLbl, priceLbl);

        card.getChildren().addAll(imageView, textArea);
        card.setOnMouseEntered(e -> animateZoom(card, 1.04));
        card.setOnMouseExited(e -> animateZoom(card, 1.0));

        return card;
    }

    private void animateZoom(Node node, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(180), node);
        st.setToX(scale);
        st.setToY(scale);
        st.play();
    }

    private void addToCart(String itemName, Double price, Long itemId) {
        cartItemsDisplay.add(new CartItem(itemName, price, itemId));

        currentTotal += price;
        totalLabel.setText(String.format("$%.2f", currentTotal));
        statusLabel.setText("");
    }

    @FXML
    private void showRestaurants() {
        menuView.setVisible(false);
        ordersView.setVisible(false);
        restaurantsView.setVisible(true);
    }

    @FXML
    private void showOrders() {
        restaurantsView.setVisible(false);
        menuView.setVisible(false);
        ordersView.setVisible(true);
        loadUserOrders();
    }

    @FXML
    private void loadUserOrders() {
        ordersListContainer.getChildren().clear();
        Label loadingLabel = new Label("Loading orders...");
        ordersListContainer.getChildren().add(loadingLabel);

        new Thread(() -> {
            try {
                Response response = ApiClient.get("/orders/user");
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> orders = ApiClient.getGson().fromJson(response.body().string(), List.class);
                    Platform.runLater(() -> populateOrdersList(orders));
                } else {
                    Platform.runLater(() -> {
                        ordersListContainer.getChildren().clear();
                        ordersListContainer.getChildren().add(new Label("Failed to load orders."));
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    ordersListContainer.getChildren().clear();
                    ordersListContainer.getChildren().add(new Label("Error: " + e.getMessage()));
                });
            }
        }).start();
    }

    private void populateOrdersList(List<Map<String, Object>> orders) {
        ordersListContainer.getChildren().clear();
        if (orders == null || orders.isEmpty()) {
            ordersListContainer.getChildren().add(new Label("You have no orders yet."));
            return;
        }

        // Sort by ID descending (newest first)
        orders.sort((a, b) -> {
            Double idA = (Double) a.get("id");
            Double idB = (Double) b.get("id");
            return Double.compare(idB, idA);
        });

        for (Map<String, Object> order : orders) {
            ordersListContainer.getChildren().add(createOrderCard(order));
        }
    }

    private javafx.scene.layout.VBox createOrderCard(Map<String, Object> order) {
        javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(15);
        card.getStyleClass().add("food-card");
        card.setPadding(new Insets(20));
        card.setPrefWidth(800);
        card.setMaxWidth(900);

        Long orderId = ((Double) order.get("id")).longValue();
        String status = (String) order.get("status");
        Double total = (Double) order.get("totalAmount");
        String dateStr = (String) order.get("orderDate"); // e.g., "2023-10-01T12:30:00"

        // Extract restaurant info safely
        Map<String, Object> restaurant = (Map<String, Object>) order.get("restaurant");
        String restaurantName = restaurant != null && restaurant.get("name") != null ? (String) restaurant.get("name") : "Unknown Restaurant";
        Integer deliveryTime = restaurant != null && restaurant.get("deliveryTime") != null ? ((Double) restaurant.get("deliveryTime")).intValue() : 30;

        // Header: Restaurant Name & Order ID
        javafx.scene.layout.HBox header = new javafx.scene.layout.HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label resLabel = new Label(restaurantName);
        resLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        javafx.scene.layout.Region spacer1 = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer1, javafx.scene.layout.Priority.ALWAYS);
        Label idLabel = new Label("Order #" + orderId);
        idLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 14px;");
        header.getChildren().addAll(resLabel, spacer1, idLabel);

        // Calculate Estimated Time
        String etaStr = "Calculating...";
        try {
            java.time.LocalDateTime orderDate = java.time.LocalDateTime.parse(dateStr);
            java.time.LocalDateTime etaDate = orderDate.plusMinutes(deliveryTime);
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
            
            if ("DELIVERED".equals(status) || "CANCELLED".equals(status)) {
                etaStr = "Ended at " + etaDate.format(formatter);
            } else {
                etaStr = "Arriving by " + etaDate.format(formatter) + " (~" + deliveryTime + " mins)";
            }
        } catch (Exception e) {
            etaStr = "Estimated ~" + deliveryTime + " mins";
        }

        // Details Row
        javafx.scene.layout.HBox details = new javafx.scene.layout.HBox(20);
        details.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label totalLabel = new Label(String.format("Total: $%.2f", total));
        totalLabel.setStyle("-fx-text-fill: #D70F64; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label etaLabel = new Label("⏱️ " + etaStr);
        etaLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 13px;");
        details.getChildren().addAll(totalLabel, etaLabel);

        // Status Timeline
        javafx.scene.layout.HBox timelineBox = new javafx.scene.layout.HBox(10);
        timelineBox.setAlignment(javafx.geometry.Pos.CENTER);
        timelineBox.setPadding(new Insets(10, 0, 5, 0));

        String[] stages = {"PENDING", "PREPARING", "READY_FOR_PICKUP", "DELIVERING", "DELIVERED"};
        int currentStageIdx = 0;
        if ("CANCELLED".equals(status)) {
            currentStageIdx = -1;
        } else {
            for (int i = 0; i < stages.length; i++) {
                if (stages[i].equals(status)) {
                    currentStageIdx = i;
                    break;
                }
            }
        }

        for (int i = 0; i < stages.length; i++) {
            javafx.scene.layout.VBox stageNode = new VBox(5);
            stageNode.setAlignment(javafx.geometry.Pos.CENTER);
            
            // Circle
            javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(8);
            if (currentStageIdx == -1) {
                circle.setFill(Color.web("#ff4d4d")); // Cancelled red
            } else if (i <= currentStageIdx) {
                circle.setFill(Color.web("#D70F64")); // Active pink
            } else {
                circle.setFill(Color.web("#333344")); // Inactive grey
            }

            // Label
            Label stageLbl = new Label(stages[i]);
            stageLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (i <= currentStageIdx ? "#eee" : "#777") + ";");
            stageNode.getChildren().addAll(circle, stageLbl);
            
            timelineBox.getChildren().add(stageNode);

            // Add line between stages
            if (i < stages.length - 1) {
                javafx.scene.shape.Line line = new javafx.scene.shape.Line(0, 0, 80, 0);
                line.setStrokeWidth(2);
                if (currentStageIdx != -1 && i < currentStageIdx) {
                    line.setStroke(Color.web("#D70F64"));
                } else {
                    line.setStroke(Color.web("#333344"));
                }
                javafx.scene.layout.HBox.setMargin(line, new Insets(0, 0, 15, 0));
                timelineBox.getChildren().add(line);
            }
        }

        if ("CANCELLED".equals(status)) {
            Label cancelledMsg = new Label("Order Cancelled");
            cancelledMsg.setStyle("-fx-text-fill: #ff4d4d; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
            card.getChildren().addAll(header, details, cancelledMsg);
        } else {
            card.getChildren().addAll(header, details, timelineBox);
            
            // Show Rider Info if DELIVERING or DELIVERED
            Map<String, Object> rider = (Map<String, Object>) order.get("rider");
            if (rider != null && ("DELIVERING".equals(status) || "DELIVERED".equals(status))) {
                String riderName = (String) rider.get("username");
                Label riderLbl = new Label("🛵 Picked up by: " + riderName);
                riderLbl.setStyle("-fx-text-fill: #8b5cf6; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");
                card.getChildren().add(riderLbl);
            }
        }

        return card;
    }

    @FXML
    private void checkout() {
        if (cartItemsDisplay.isEmpty()) {
            statusLabel.setText("Your cart is empty. Add items first!");
            statusLabel.setTextFill(Color.RED);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/checkout.fxml"));
            Parent root = loader.load();

            CheckoutController checkoutCtrl = loader.getController();
            checkoutCtrl.initData(cartItemsDisplay, currentTotal, currentRestaurantId, currentRestaurantName);

            Stage stage = (Stage) restaurantsView.getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Could not open checkout.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleLogout() {
        try {
            ApiClient.setJwtToken(null);
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) restaurantsView.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
