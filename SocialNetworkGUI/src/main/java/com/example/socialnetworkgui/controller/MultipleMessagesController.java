package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.service.SocialNetwork;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class MultipleMessagesController {

    SocialNetwork socialNetwork;
    User user;
    ObservableList<User> modelAll = FXCollections.observableArrayList();
    ObservableList<User> modelSelected = FXCollections.observableArrayList();
    List<Long> to;

    @FXML
    public TextArea txtArea;

    @FXML
    public ListView<User> allListView;

    @FXML
    public ListView<User> selectedListView;

    @FXML
    void initialize() {
        allListView.setItems(modelAll);
        selectedListView.setItems(modelSelected);
        to = new ArrayList<>();
    }

    public void setService(SocialNetwork socialNetwork, User user) {
        this.socialNetwork = socialNetwork;
        this.user = user;
        initModel();

        allListView.setCellFactory(list -> new ListCell<User>(){
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getFirstName() + " " + item.getLastName());
                }
            }
        });

        selectedListView.setCellFactory(list -> new ListCell<User>(){
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getFirstName() + " " + item.getLastName());
                }
            }
        });

//        allListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
//            @Override
//            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
//                modelSelected.add(newValue);
//                to.add(newValue.getId());
//                modelAll.remove(newValue);
//            }
//        });

        allListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User selectedFriend = allListView.getSelectionModel().getSelectedItem();
                modelSelected.add(selectedFriend);
                to.add(selectedFriend.getId());
                modelAll.remove(selectedFriend);
            }
        });
    }

    private void initModel() {
        Iterable<Friendship> sth = socialNetwork.getAllFriendships();
        List<User> lista = StreamSupport.stream(sth.spliterator(), false)
                .filter(x -> {
                    return x.getState().equals(true) && (x.getId().getE1().equals(user.getId()) || x.getId().getE2().equals(user.getId()));
                })
                .map(x -> {
                    Long userID = x.getId().getE1();
                    if (userID.equals(user.getId()))
                        userID = x.getId().getE2();
                    return socialNetwork.getUser(userID).get();
                })
                .toList();
        modelAll.setAll(lista);
    }


    public void sendButton(ActionEvent actionEvent) {
        String textMessage = txtArea.getText();
        socialNetwork.saveMultipleMessages(user, to, LocalDateTime.now(), textMessage, null);
        txtArea.setText("");
        modelSelected.clear();
        initModel();
    }
}
