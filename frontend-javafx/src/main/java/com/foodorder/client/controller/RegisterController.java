package com.foodorder.client.controller;

import com.foodorder.client.MainApp;
import com.foodorder.client.network.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import okhttp3.Response;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class RegisterController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView heroImage;

    @FXML
    private MediaView heroVideo;

    @FXML
    private javafx.scene.layout.StackPane mediaContainer;

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

        if (heroVideo != null) heroVideo.setVisible(false);

        new Thread(() -> {
            try {
                String videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";
                Media media = new Media(videoUrl);
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setAutoPlay(true);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setMute(true);

                mediaPlayer.setOnReady(() -> {
                    Platform.runLater(() -> {
                        if (heroVideo != null) {
                            heroVideo.setMediaPlayer(mediaPlayer);
                            heroVideo.setVisible(true);
                        }
                        if (heroImage != null) heroImage.setVisible(false);
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
                    "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=900&auto=format&fit=crop",
                    true);
                Platform.runLater(() -> {
                    if (heroImage != null) {
                        heroImage.setImage(img);
                        heroImage.setVisible(true);
                    }
                    if (heroVideo != null) heroVideo.setVisible(false);
                });
            } catch (Exception e) {
                // fallback
            }
        }).start();
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields are required.", true);
            return;
        }

        new Thread(() -> {
            try {
                Map<String, String> payload = new HashMap<>();
                payload.put("username", username);
                payload.put("email", email);
                payload.put("password", password);
                payload.put("role", "user");

                Response response = ApiClient.post("/auth/register", payload);
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        showError("Registration successful! Please login.", false);
                        // Optional: automatically go to login after a delay
                    });
                } else {
                    Platform.runLater(() -> showError("Registration failed. Username/Email might exist.", true));
                }
            } catch (Exception e) {
                Platform.runLater(() -> showError("Connection failed.", true));
            }
        }).start();
    }

    @FXML
    private void goToLogin() {
        try {
            MainApp.setRoot("/fxml/login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        System.exit(0);
    }

    private void showError(String msg, boolean isError) {
        errorLabel.setText(msg);
        errorLabel.setTextFill(isError ? javafx.scene.paint.Color.valueOf("#ff4d4d") : javafx.scene.paint.Color.valueOf("#4dff4d"));
        errorLabel.setVisible(true);
    }
}
