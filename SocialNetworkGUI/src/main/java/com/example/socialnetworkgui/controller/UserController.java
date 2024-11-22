package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Account;
import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.events.UserEntityChangeEvent;
import com.example.socialnetworkgui.observer.Observer;
import com.example.socialnetworkgui.service.AuthService;
import com.example.socialnetworkgui.service.SocialNetwork;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<FriendshipEntityChangeEvent> {

    Account account;
    User user;
    SocialNetwork socialNetwork;
    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    @FXML
    Label nameLabel;

    public void setSocialNetwork(SocialNetwork socialNetwork, User user, Account account) {
        this.socialNetwork = socialNetwork;
        this.user = user;
        this.account = account;
        socialNetwork.addObserver(this);
        nameLabel.setText("Salut, " + user.getFirstName() + " " + user.getLastName() + "!");
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<Friendship> sth = socialNetwork.getAllFriendships();
        List<User> friends = StreamSupport.stream(sth.spliterator(), false)
                        .filter(x -> ( Objects.equals(x.getId().getE1(), user.getId()) || Objects.equals(x.getId().getE2(), user.getId()) ) && x.getState() )
                        .map(x -> {
                            Long idFriend = x.getId().getE1();
                            if (Objects.equals(idFriend, user.getId()))
                                idFriend = x.getId().getE2();
                            return socialNetwork.getUser(idFriend).get();
                        })
                        .toList();
        model.setAll(friends);
    }

//    public void handleAddUser(ActionEvent event) {
//        userInfoWindow(null);
//    }

    public void handleUpdateUser(ActionEvent event) {
        userInfoWindow(user);
    }

//    public void handleDeleteUser(ActionEvent event) {
//        User user = tableView.getSelectionModel().getSelectedItem();
//        if (user != null) {
//            Optional<User> deleted = socialNetwork.delete(user.getFirstName(), user.getLastName());
//            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete User", "User has been removed!");
//        }
//        else
//            MessageAlert.showErrorMessage(null, "You have to select a user!");
//    }

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
            controller.setSocialNetwork(socialNetwork, null, dialogStage, account, user);

            dialogStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(FriendshipEntityChangeEvent event) {
        initModel();
    }

    public void handleAddFriend(ActionEvent actionEvent) {
        possibleFriendsWindow();
    }

    public void handleDeleteFriend(ActionEvent actionEvent) {
        User user = tableView.getSelectionModel().getSelectedItem();
        if (user != null) {
            Optional<Friendship> deleted = socialNetwork.delete(this.user.getFirstName(), this.user.getLastName(), user.getFirstName(), user.getLastName());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete Friendship", "Friendship has been removed!");
        }
        else
            MessageAlert.showErrorMessage(null, "You have to select a user!");
    }

    public void possibleFriendsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/add-friend-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add a friend");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddFriendController controller = loader.getController();
            controller.setService(socialNetwork, user, dialogStage);

            dialogStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handleFriendRequests(ActionEvent actionEvent) {
        friendRequestsWindow();
    }

    public void friendRequestsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/friend-requests-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("View your friend requests");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendRequestsController controller = loader.getController();
            controller.setService(socialNetwork, user, dialogStage);

            dialogStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
