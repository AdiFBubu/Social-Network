package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Account;
import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.dto.UserFilterDTO;
import com.example.socialnetworkgui.events.ChangeEventType;
import com.example.socialnetworkgui.events.EntityChangeEvent;
import com.example.socialnetworkgui.events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.events.UserEntityChangeEvent;
import com.example.socialnetworkgui.observer.Observer;
import com.example.socialnetworkgui.service.AuthService;
import com.example.socialnetworkgui.service.SocialNetwork;
import com.example.socialnetworkgui.utils.paging.Page;
import com.example.socialnetworkgui.utils.paging.Pageable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<EntityChangeEvent> {

    Account account;
    User user;
    SocialNetwork socialNetwork;
    ObservableList<User> model = FXCollections.observableArrayList();
    Stage stage;
    private ExecutorService executorService;

    private int pageSize = 2;
    int currentPage = 0;
    int totalNumberOfElements = 0;
    private UserFilterDTO userFilter = new UserFilterDTO();

    @FXML
    public Button previousButton;
    @FXML
    public Button nextButton;
    @FXML
    public TextField textFieldFirstName;
    @FXML
    public TextField textFieldLastName;
    @FXML
    public Label labelPage;
    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    @FXML
    Label nameLabel;

    public void setSocialNetwork(SocialNetwork socialNetwork, User user, Account account, Stage stage) {
        this.socialNetwork = socialNetwork;
        this.user = user;
        this.account = account;
        this.stage = stage;
        executorService = Executors.newFixedThreadPool(10);

        stage.setOnCloseRequest(event -> {
            // La închiderea ferestrei, dezabonați această fereastră de la observații
            socialNetwork.removeObserver(this);
        });

        socialNetwork.addObserver(this);
        nameLabel.setText("Salut, " + user.getFirstName() + " " + user.getLastName() + "!");

        userFilter.setIdUser(Optional.of(user.getId()));
        userFilter.setState(Optional.of(true));

        initModelPage();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User selectedFriend = tableView.getSelectionModel().getSelectedItem();
                if (selectedFriend != null) {
                    openChatWindow(selectedFriend);
                }
            }
        });

        textFieldFirstName.textProperty().addListener((observable, oldValue, newValue) -> handleFilter());
        textFieldLastName.textProperty().addListener(o-> handleFilter());


    }

    private void initModelPage() {
        Page<Friendship> page = socialNetwork.findAllOnPage(new Pageable(currentPage, pageSize), userFilter);
        Iterable<Friendship> sth = page.getElementsOnPage();
        List<User> friends = StreamSupport.stream(sth.spliterator(), false)
                        .map(x -> {
                            Long idFriend = x.getId().getE1();
                            if (Objects.equals(idFriend, user.getId()))
                                idFriend = x.getId().getE2();
                            return socialNetwork.getUser(idFriend).get();
                        })
                        .toList();
        model.setAll(friends);

        totalNumberOfElements = page.getTotalNumberOfElements();

        int totalNumberOfPages = totalNumberOfElements / pageSize;
        if (totalNumberOfElements % pageSize != 0) totalNumberOfPages ++;
        labelPage.setText("Page " + (currentPage + 1) + " of " + totalNumberOfPages);

        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable(currentPage == totalNumberOfPages - 1);

    }

    private void handleFilter() {

        if (textFieldLastName.getText() == null)
            userFilter.setFirstName(Optional.empty());
        else
            userFilter.setFirstName(Optional.of(textFieldFirstName.getText()));

        if (textFieldLastName.getText() == null)
            userFilter.setLastName(Optional.empty());
        else
            userFilter.setLastName(Optional.of(textFieldLastName.getText()));
        currentPage = 0;
        initModelPage();
    }

//    private void initModel() {
//        Iterable<Friendship> sth = socialNetwork.getAllFriendships();
//        List<User> friends = StreamSupport.stream(sth.spliterator(), false)
//                        .filter(x -> ( Objects.equals(x.getId().getE1(), user.getId()) || Objects.equals(x.getId().getE2(), user.getId()) ) && x.getState() )
//                        .map(x -> {
//                            Long idFriend = x.getId().getE1();
//                            if (Objects.equals(idFriend, user.getId()))
//                                idFriend = x.getId().getE2();
//                            return socialNetwork.getUser(idFriend).get();
//                        })
//                        .toList();
//        model.setAll(friends);
//    }

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
    public void update(EntityChangeEvent event) {
        if (event.getType() == ChangeEventType.FRIEND) {
            currentPage = 0;
            initModelPage();
        }
        if (event.getType() == ChangeEventType.FRIEND_REQUEST) {
            Friendship possibleFriendhip = (Friendship) event.getData();
            if (possibleFriendhip.getId().getE2().equals(user.getId())) {
                Long friendID = possibleFriendhip.getId().getE1();
                User possibleFriend = socialNetwork.getUser(friendID).get();
                String text = "User " + possibleFriend.getFirstName() + " " + possibleFriend.getLastName() + " wants to become friends with you!";
                DialogAlert.showNonModalDialog(stage, "Friend request", text);
            }
        }
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
            //dialogStage.initModality(Modality.APPLICATION_MODAL);
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
            //dialogStage.initModality(Modality.APPLICATION_MODAL);
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

    public void openChatWindow(User selectedFriend) {
        executorService.execute(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/chat-view.fxml"));

                AnchorPane root = loader.load();

                Platform.runLater(() -> {
                    try {
                        Stage dialogStage = new Stage();
                        dialogStage.setTitle("Chat with " + selectedFriend.getFirstName() + " " + selectedFriend.getLastName());
                        Scene scene = new Scene(root);
                        dialogStage.setScene(scene);

                        ChatController controller = loader.getController();
                        controller.setService(socialNetwork, user, selectedFriend);

                        dialogStage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    private void sendMultipleMessages() {
        executorService.execute(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/multiple-messages-view.fxml"));

                AnchorPane root = loader.load();

                Platform.runLater(() -> {
                    try {
                        Stage dialogStage = new Stage();
                        dialogStage.setTitle("Send a message to multiple friends");
                        Scene scene = new Scene(root);
                        dialogStage.setScene(scene);

                        MultipleMessagesController controller = loader.getController();
                        controller.setService(socialNetwork, user);

                        dialogStage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void handleMultipleMessages(ActionEvent actionEvent) {
        sendMultipleMessages();
    }

    public void onNextPage(ActionEvent actionEvent) {
        currentPage ++;
        initModelPage();
    }

    public void onPreviousPage(ActionEvent actionEvent) {
        currentPage --;
        initModelPage();
    }

    public void handleProfile(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../views/profile-view.fxml"));

        try {
            AnchorPane root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Profile");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            ProfileController controller = loader.getController();
            controller.setService(socialNetwork, this, user, user);
            dialogStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAllUsers(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../views/all-users-view.fxml"));

        try {
            AnchorPane root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("All users");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            AllUsersController controller = loader.getController();
            controller.setService(socialNetwork, this, user);
            dialogStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
