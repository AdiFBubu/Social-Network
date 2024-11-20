package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Account;
import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.service.SocialNetwork;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class AddFriendController {

    @FXML
    public TableColumn<User, String> tableColumnFirstName;
    @FXML
    public TableColumn<User, String> tableColumnLastName;
    @FXML
    public TextField nameTextField;
    ObservableList<User> model = FXCollections.observableArrayList();
    List<User> possibleFriends;
    Stage stage;
    SocialNetwork socialNetwork;
    User user;

    @FXML
    TableView<User> tableView;

    public void setService(SocialNetwork socialNetwork, User user, Stage stage) {
        this.socialNetwork = socialNetwork;
        this.stage = stage;
        this.user = user;
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> handleFilter());
    }

    void handleFilter() {
        Predicate<User> p1 = x -> x.getLastName().toLowerCase().startsWith(nameTextField.getText().toLowerCase());
        model.setAll(possibleFriends.stream()
                .filter(p1)
                        .toList()
                );
    }

    List<User> getPossibeFriends() {
        Iterable<User> sth = socialNetwork.getAllUsers();
        return StreamSupport.stream(sth.spliterator(), false)
                .filter(x -> {
                    Optional<Friendship> rez = socialNetwork.getFriendship(new Tuple<>(x.getId(), user.getId()));
                    return !user.equals(x) && rez.isEmpty();
                })
                .toList();
    }

    private void initModel() {
        possibleFriends = getPossibeFriends();
        model.setAll(possibleFriends);
    }

    public void addFriend(ActionEvent actionEvent) {
        User possibleFriend = tableView.getSelectionModel().getSelectedItem();
        try {
            Optional<Friendship> u = this.socialNetwork.save(user.getFirstName(), user.getLastName(), possibleFriend.getFirstName(), possibleFriend.getLastName());
            if (u.isPresent())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Add Friend", "A new friend has been added successfully");
            stage.close();
        }
        catch (Exception e) {
            MessageAlert.showErrorMessage(null, "You have to select a user!");
        }
    }

    public void cancel(ActionEvent actionEvent) {
        stage.close();
    }
}
