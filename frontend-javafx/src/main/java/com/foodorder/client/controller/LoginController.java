package com.foodorder.client.controller;

import com.foodorder.client.MainApp;
import com.foodorder.client.network.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import okhttp3.Response;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private ImageView heroImage;
    @FXML private MediaView heroVideo;
    @FXML private javafx.scene.layout.StackPane mediaContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Make media fully responsive (simulate object-fit: cover)
        if (mediaContainer != null) {
            javafx.beans.value.ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> {
                double containerW = mediaContainer.getWidth();
                double containerH = mediaContainer.getHeight();
                if (containerW == 0 || containerH == 0) return;
                
                double mediaRatio = 16.0 / 9.0; // Default 16:9 ratio
                double containerRatio = containerW / containerH;
                
                if (containerRatio > mediaRatio) {
                    heroImage.setFitWidth(containerW);
                    heroImage.setFitHeight(containerW / mediaRatio);
                    if (heroVideo != null) {
                        heroVideo.setFitWidth(containerW);
                        heroVideo.setFitHeight(containerW / mediaRatio);
                    }
                } else {
                    heroImage.setFitHeight(containerH);
                    heroImage.setFitWidth(containerH * mediaRatio);
                    if (heroVideo != null) {
                        heroVideo.setFitHeight(containerH);
                        heroVideo.setFitWidth(containerH * mediaRatio);
                    }
                }
            };
            
            mediaContainer.widthProperty().addListener(sizeListener);
            mediaContainer.heightProperty().addListener(sizeListener);
            
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
            clip.widthProperty().bind(mediaContainer.widthProperty());
            clip.heightProperty().bind(mediaContainer.heightProperty());
            mediaContainer.setClip(clip);
        }

        // Hide video by default, show image
        heroVideo.setVisible(false);
        
        new Thread(() -> {
            try {
                // Try to load a background video
                String videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";
                Media media = new Media(videoUrl);
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setAutoPlay(true);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setMute(true);

                mediaPlayer.setOnReady(() -> {
                    Platform.runLater(() -> {
                        heroVideo.setMediaPlayer(mediaPlayer);
                        heroVideo.setVisible(true);
                        heroImage.setVisible(false);
                    });
                });

                mediaPlayer.setOnError(() -> loadFallbackImage());
            } catch (Exception e) {
                loadFallbackImage();
            }
        }).start();
    }

    private void loadFallbackImage() {
        new Thread(() -> {
            try {
                Image img = new Image(
                    "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=900&auto=format&fit=crop",
                    true);
                Platform.runLater(() -> {
                    if (heroImage != null) heroImage.setImage(img);
                    if (heroVideo != null) heroVideo.setVisible(false);
                    if (heroImage != null) heroImage.setVisible(true);
                });
            } catch (Exception e) {
                // fallback
            }
        }).start();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        // Run network call on separate thread
        new Thread(() -> {
            try {
                Map<String, String> payload = new HashMap<>();
                payload.put("username", username);
                payload.put("password", password);

                Response response = ApiClient.post("/auth/login", payload);
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    // Parse response using Gson
                    Map<String, Object> jsonMap = ApiClient.getGson().fromJson(responseBody, Map.class);
                    String token = (String) jsonMap.get("accessToken");
                    ApiClient.setJwtToken(token);
                    
                    java.util.List<String> roles = (java.util.List<String>) jsonMap.get("roles");
                    boolean isAdmin = roles != null && roles.contains("ROLE_ADMIN");
                    boolean isRestaurant = roles != null && roles.contains("ROLE_RESTAURANT");
                    boolean isRider = roles != null && roles.contains("ROLE_RIDER");
                    
                    Platform.runLater(() -> {
                        try {
                            if (isAdmin) {
                                MainApp.setRoot("/fxml/admin.fxml");
                            } else if (isRestaurant) {
                                MainApp.setRoot("/fxml/restaurant-dashboard.fxml");
                            } else if (isRider) {
                                MainApp.setRoot("/fxml/rider-dashboard.fxml");
                            } else {
                                MainApp.setRoot("/fxml/main.fxml");
                            }
                            System.out.println("Login successful!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Platform.runLater(() -> showError("Invalid username or password."));
                }
            } catch (Exception e) {
                Platform.runLater(() -> showError("Connection failed. Is the server running?"));
            }
        }).start();
    }

    @FXML
    private void goToRegister() {
        try {
            MainApp.setRoot("/fxml/register.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        System.exit(0);
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
