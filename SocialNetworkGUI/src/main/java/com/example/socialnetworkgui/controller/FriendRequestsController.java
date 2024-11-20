package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.FriendshipDTO;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.observer.Observer;
import com.example.socialnetworkgui.service.SocialNetwork;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FriendRequestsController implements Observer<FriendshipEntityChangeEvent> {

    SocialNetwork socialNetwork;
    User user;
    Stage stage;
    ObservableList<FriendshipDTO> model = FXCollections.observableArrayList();


    @FXML
    public TableView<FriendshipDTO> tableView;

    @FXML
    public TableColumn<FriendshipDTO, String> tableColumnFirstName;

    @FXML
    public TableColumn<FriendshipDTO, String> tableColumnLastName;

    @FXML
    public TableColumn<FriendshipDTO, LocalDateTime> tableColumnDate;

    @FXML
    public TableColumn<FriendshipDTO, String> tableColumnStatus;

    public void setService(SocialNetwork socialNetwork, User user, Stage stage) {
        this.socialNetwork = socialNetwork;
        this.user = user;
        this.stage = stage;
        socialNetwork.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("state"));
        tableView.setItems(model);
    }

    private void initModel() {
        model.setAll(getFriendshipDTOList());
    }

    private List<FriendshipDTO> getFriendshipDTOList() {
        Iterable<Friendship> sth = socialNetwork.getAllFriendships();
        return StreamSupport.stream(sth.spliterator(), false)
                .filter(x -> Objects.equals(x.getId().getE2(), user.getId()) && !x.getState())
                .map(x -> {
                    Long idFriend = x.getId().getE1();
                    if (Objects.equals(idFriend, user.getId()))
                        idFriend = x.getId().getE2();
                    User u = socialNetwork.getUser(idFriend).get();
                    return new FriendshipDTO(u.getFirstName(), u.getLastName(), x.getDate(), "pending");
                })
                .toList();
    }

    public void handleAcceptButton(ActionEvent actionEvent) {
        FriendshipDTO friendshipDTO = tableView.getSelectionModel().getSelectedItem();
        if (friendshipDTO != null) {
            try {
                var rez = socialNetwork.update(user.getFirstName(), user.getLastName(), friendshipDTO.getFirstName(), friendshipDTO.getLastName(), true);
                if (rez.isPresent())
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend Request Update","You accepted the friend request!");
                else
                    MessageAlert.showErrorMessage(null, "The friend request does not exist!");
            }
            catch (Exception e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(null, "You have to select a friend request!");
    }

    public void handleRejectButton(ActionEvent actionEvent) {
        FriendshipDTO friendshipDTO = tableView.getSelectionModel().getSelectedItem();
        if (friendshipDTO != null) {
            try {
                var rez = socialNetwork.delete(user.getFirstName(), user.getLastName(), friendshipDTO.getFirstName(), friendshipDTO.getLastName());
                if (rez.isPresent())
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend Request Update","You declined the friend request!");
                else
                    MessageAlert.showErrorMessage(null, "The friend request does not exist!");
            }
            catch (Exception e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(null, "You have to select a friend request!");
    }

    public void handleExitButton(ActionEvent actionEvent) {
        stage.close();
    }

    @Override
    public void update(FriendshipEntityChangeEvent event) {
        initModel();
    }
}
