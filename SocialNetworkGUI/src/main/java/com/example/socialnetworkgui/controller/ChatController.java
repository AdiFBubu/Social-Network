package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Message;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.events.ChangeEventType;
import com.example.socialnetworkgui.events.EntityChangeEvent;
import com.example.socialnetworkgui.events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.observer.Observer;
import com.example.socialnetworkgui.service.SocialNetwork;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class ChatController implements Observer<EntityChangeEvent> {

    User from;
    User to;
    SocialNetwork socialNetwork;
    ObservableList<Message> model = FXCollections.observableArrayList();

    @FXML
    public Label replyLabel;

    @FXML
    public ListView<Message> listView;

    @FXML
    public TextArea textArea;

    private Long reply = null;

    public void setService(SocialNetwork socialNetwork, User from, User to) {
        this.socialNetwork = socialNetwork;
        socialNetwork.addObserver(this);
        this.from = from;
        this.to = to;
        initModel();
    }

    @FXML
    void initialize() {
        listView.setItems(model);
        replyLabel.setText("");

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Creăm HBox-ul care conține doar textul mesajului
                    Label messageLabel = new Label(message.getMessage());
                    messageLabel.setStyle("-fx-padding: 10; -fx-background-color: lightblue; -fx-border-radius: 5; -fx-background-radius: 5;");
                    VBox layout = new VBox();
                    HBox messageBox = new HBox(layout);

                    if (message.getReply() != null) {
                        Label replyLabel = new Label();
                        Message prevMessage = socialNetwork.getMessage(message.getReply()).get();
                        replyLabel.setText(prevMessage.getMessage());
                        replyLabel.setOpacity(0.5);
                        layout.getChildren().add(replyLabel);
                    }

                    layout.getChildren().add(messageLabel);

                    if (message.getFrom().equals(from.getId())) {
                        messageBox.setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        messageBox.setAlignment(Pos.CENTER_LEFT);
                    }

                    setText(null); // Nu afișăm textul implicit
                    setGraphic(messageBox); // Afișăm HBox-ul ca grafic
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Message selectedMessage = listView.getSelectionModel().getSelectedItem();
                if (selectedMessage != null) {
                    replyLabel.setText("Reply to: " + selectedMessage.getMessage());
                    reply = selectedMessage.getId();
                }
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection == null) {
                // Dacă nu există selecție, setează un text gol pe replyLabel
                replyLabel.setText("");
                reply = null;
            } else if (oldSelection != null && oldSelection.equals(newSelection)) {
                // Dacă aceleași elemente sunt selectate (click pe același element pentru a-l deselecta)
                listView.getSelectionModel().clearSelection();
                replyLabel.setText("");
                reply = null;
            } else {
                // Dacă un element diferit este selectat, actualizează replyLabel cu textul respectiv
                replyLabel.setText("Reply to: " + newSelection.getMessage());
                reply = newSelection.getId();
            }
        });
    }

    private void initModel() {
        Iterable<Message> sth = socialNetwork.getMessages();
//        List<HBox> messages = StreamSupport.stream(sth.spliterator(), false)
//                .filter(x -> Objects.equals(x.getFrom(), from.getId()) && Objects.equals(x.getTo().get(0), to.getId()) ||
//                        Objects.equals(x.getFrom(), to.getId()) && Objects.equals(x.getTo().get(0), from.getId()))
//                .sorted(Comparator.comparingLong(Message::getId))
//                .map(x -> {
//                    HBox messageBox = new HBox();
//                    //Label messageLabel = new Label(x.getMessage());
//                    //messageLabel.setStyle("-fx-padding: 10; -fx-background-color: lightblue; -fx-border-radius: 5; -fx-background-radius: 5;");
//
//                    if (x.getFrom().equals(from.getId())) {
//                        messageBox.setAlignment(Pos.CENTER_RIGHT);
//                    } else {
//                        messageBox.setAlignment(Pos.CENTER_LEFT);
//                    }
//                    messageBox.getChildren().add(x);
//                    return messageBox;
//                })
//                .toList();
        List<Message> messages = StreamSupport.stream(sth.spliterator(), false)
                .filter(x -> Objects.equals(x.getFrom(), from.getId()) && Objects.equals(x.getTo().get(0), to.getId()) ||
                        Objects.equals(x.getFrom(), to.getId()) && Objects.equals(x.getTo().get(0), from.getId()))
                .sorted(Comparator.comparingLong(Message::getId))
                .toList();
        model.setAll(messages);
    }

    public void handleSendMessage(ActionEvent actionEvent) {
        String messageText = textArea.getText();
        socialNetwork.saveMessage(from, to, LocalDateTime.now(), messageText, reply);
        replyLabel.setText("");
        textArea.setText("");
        reply = null;
    }

    @Override
    public void update(EntityChangeEvent event) {
        if (event.getType() == ChangeEventType.MESSAGE)
            initModel();
    }

    public void stopReplyingButton(ActionEvent actionEvent) {
        reply = null;
        replyLabel.setText("");
    }
}
