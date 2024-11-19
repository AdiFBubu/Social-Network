package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.service.SocialNetwork;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController {
    SocialNetwork socialNetwork;
    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;

    public void setSocialNetwork(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<User> sth = socialNetwork.getAllUsers();
        List<User> users = StreamSupport.stream(sth.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    public void handleAddUser(ActionEvent event) {
        userInfoWindow(null);
    }

    public void handleUpdateUser(ActionEvent event) {
        User user = tableView.getSelectionModel().getSelectedItem();
        if (user != null) {
            userInfoWindow(user);
        }
        else
            MessageAlert.showErrorMessage(null, "You have to select a user!");
    }

    public void handleDeleteUser(ActionEvent event) {
        User user = tableView.getSelectionModel().getSelectedItem();
        if (user != null) {
            Optional<User> deleted = socialNetwork.delete(user.getFirstName(), user.getLastName());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete User", "User has been removed!");
        }
        else
            MessageAlert.showErrorMessage(null, "You have to select a user!");
    }

    public void userInfoWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/edit-user-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Information");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUserController controller = loader.getController();
            controller.setSocialNetwork(socialNetwork, dialogStage, user);

            dialogStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}
