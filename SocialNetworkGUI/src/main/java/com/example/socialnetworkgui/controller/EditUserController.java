package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Account;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.service.AuthService;
import com.example.socialnetworkgui.service.SocialNetwork;
import com.sun.nio.sctp.MessageInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class EditUserController {

    @FXML
    public TextField textFieldEmail;

    @FXML
    public PasswordField textFieldPassword;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldLastName;

    private AuthService authService;
    private SocialNetwork socialNetwork;
    Stage dialogStage;
    Account account;
    User user;

    public void setSocialNetwork(SocialNetwork socialNetwork, AuthService authService, Stage dialogStage, Account account, User user) {
        this.socialNetwork = socialNetwork;
        this.authService = authService;
        this.dialogStage = dialogStage;
        this.account = account;
        this.user = user;
        setAccountDetails();
        if (user != null)
            setFields(user);
    }

    public void handleSave() {
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        if (this.user == null)
            saveMessage(firstName, lastName);
        else
            updateMessage(firstName, lastName);

    }

    private void saveMessage(String firstName, String lastName) {
        try {
            Optional<User> u = this.socialNetwork.save(firstName, lastName);
            if (u.isPresent()) {
                Optional<Account> account = this.authService.add(u.get().getId(), this.account.getEmail(), this.account.getPassword());
                if (account.isPresent())
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Add User", "User has been added successfully");

            }
        }
        catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        dialogStage.close();
    }

    private void updateMessage(String firstName, String lastName) {
        try {
            Optional<User> u = this.socialNetwork.update(this.user.getFirstName(), this.user.getLastName(), firstName, lastName);
            if (u.isPresent())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Update User", "User has been modified successfully");
        }
        catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        dialogStage.close();
    }

    public void handleCancel() {
        dialogStage.close();
    }

    private void setFields(User user) {
        textFieldFirstName.setText(user.getFirstName());
        textFieldLastName.setText(user.getLastName());
    }

    private void setAccountDetails() {
        textFieldEmail.setText(account.getEmail());
        textFieldPassword.setText(account.getPassword());
        textFieldEmail.setEditable(false);
        textFieldPassword.setEditable(false);
    }

}
