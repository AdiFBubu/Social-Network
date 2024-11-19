package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Account;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.service.AuthService;
import com.example.socialnetworkgui.service.SocialNetwork;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class AuthController {

    Stage window;
    AuthService authService;
    SocialNetwork socialNetwork;

    @FXML
    TextField emailField;

    @FXML
    TextField passwordField;

    public void setService(AuthService authService, Stage window, SocialNetwork socialNetwork) {
        this.authService = authService;
        this.window = window;
        this.socialNetwork = socialNetwork;
    }

    @FXML
    void signInHandle() {

    }

    @FXML
    void loginHandle() {
        String email = emailField.getText();
        String password = passwordField.getText();
        Optional<Account> account = authService.getAccount(email);
        if (account.isPresent()) {
            if (password.equals(account.get().getPassword())) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Authentication", "Welcome to Social Network!" );
                window.close();
                getMainWindow(account.get().getId());
            }
            else
                MessageAlert.showErrorMessage(null, "Passwords do not match!");
        }
        else
            MessageAlert.showErrorMessage(null, "Email does not exist!");
    }

    public void getMainWindow(Long userID) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/user-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Information");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UserController controller = loader.getController();
            controller.setSocialNetwork(socialNetwork);

            dialogStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }



}
