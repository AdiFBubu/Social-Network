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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthController {

    Stage window;
    AuthService authService;
    SocialNetwork socialNetwork;
    private ExecutorService executorService;


    @FXML
    TextField emailField;

    @FXML
    TextField passwordField;

    public void setService(AuthService authService, Stage window, SocialNetwork socialNetwork) {
        this.authService = authService;
        this.window = window;
        this.socialNetwork = socialNetwork;
        executorService = Executors.newFixedThreadPool(10);
    }

    @FXML
    void signInHandle() {
        String email = emailField.getText();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            MessageAlert.showErrorMessage(null, "Please enter valid email address and password");
        }
        else {
            Optional<Account> account = authService.getAccount(email);
            if (account.isPresent())
                MessageAlert.showErrorMessage(null, "There is already an account with this email address");
            else
                getRegistrationWindow(email, password);
        }

    }

    @FXML
    void loginHandle() {
        String email = emailField.getText();
        String password = passwordField.getText();
        Optional<Account> account = authService.getAccount(email);
        if (account.isPresent()) {
            if (password.equals(account.get().getPassword())) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Authentication", "Welcome to Social Network!");
                //window.close();
                getMainWindow(account.get());

            } else
                MessageAlert.showErrorMessage(null, "Passwords do not match!");
        } else
            MessageAlert.showErrorMessage(null, "Email does not exist!");
    }

    public void getMainWindow(Account account) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/user-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Information");
            //dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            User user = socialNetwork.getUser(account.getId()).get();

            UserController controller = loader.getController();
            controller.setSocialNetwork(socialNetwork, user, account, dialogStage);
            dialogStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getRegistrationWindow(String email, String password) {
        executorService.execute(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/edit-user-view.fxml"));

                AnchorPane root = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("User Registration");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                Account account = new Account(email, password);

                EditUserController controller = loader.getController();
                controller.setSocialNetwork(socialNetwork, authService, dialogStage, account, null);

                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



}
