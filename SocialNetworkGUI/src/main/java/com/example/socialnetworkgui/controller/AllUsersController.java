package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.dto.UserRow;
import com.example.socialnetworkgui.service.SocialNetwork;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class AllUsersController {

    ObservableList<UserRow> model = FXCollections.observableArrayList();
    SocialNetwork socialNetwork;
    UserController userController;
    User user;

    @FXML
    public TableView<UserRow> userTable;

    @FXML
    public TableColumn<UserRow, User> userColumn;

    @FXML
    public TableColumn<UserRow, User> userColumn1;

    @FXML
    public TableColumn<UserRow, User> userColumn2;

    @FXML
    public TableColumn<UserRow, User> userColumn3;

    public void setService(SocialNetwork socialNetwork, UserController userController, User user) {
        this.socialNetwork = socialNetwork;
        this.userController = userController;
        this.user = user;
        init();

        // Set cell factories with custom content
        setCustomCellFactory(userColumn, UserRow::getUser1);
        setCustomCellFactory(userColumn1, UserRow::getUser2);
        setCustomCellFactory(userColumn2, UserRow::getUser3);
        setCustomCellFactory(userColumn3, UserRow::getUser4);
    }

    private void init() {
        List<User> userList = StreamSupport.stream(socialNetwork.getAllUsers().spliterator(), false).toList();
        List<UserRow> userRows = new ArrayList<>();

        for (int i = 0; i < userList.size(); i += 4) {
            User user1 = userList.get(i);
            User user2 = (i + 1 < userList.size()) ? userList.get(i + 1) : null;
            User user3 = (i + 2 < userList.size()) ? userList.get(i + 2) : null;
            User user4 = (i + 3 < userList.size()) ? userList.get(i + 3) : null;
            userRows.add(new UserRow(user1, user2, user3, user4));
        }

        model.setAll(userRows);
        userTable.setItems(model);

        // Enable cell selection
        userTable.getSelectionModel().setCellSelectionEnabled(true);
       // userTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Handle cell selection
        userTable.getSelectionModel().getSelectedCells().addListener((ListChangeListener<TablePosition>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    TablePosition position = change.getAddedSubList().get(0);
                    int row = position.getRow();
                    int column = position.getColumn();

                    // Get the selected cell's value
                    UserRow userRow = userTable.getItems().get(row);
                    User selectedUser = getUserFromCell(userRow, column);
                    if (selectedUser != null)
                        handleProfile(selectedUser);
                }
            }
        });

    }

    public void handleProfile(User newUser) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../views/profile-view.fxml"));

        try {
            AnchorPane root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Profile");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            ProfileController controller = loader.getController();
            controller.setService(socialNetwork, userController, user, newUser);
            dialogStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User getUserFromCell(UserRow userRow, int column) {
        return switch (column) {
            case 0 -> userRow.getUser1();
            case 1 -> userRow.getUser2();
            case 2 -> userRow.getUser3();
            case 3 -> userRow.getUser4();
            default -> null;
        };
    }

    private void setCustomCellFactory(TableColumn<UserRow, User> column, Callback<UserRow, User> userExtractor) {
        // pt unul singur:
        // userColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(userExtractor.call(cellData.getValue())));
        column.setCellFactory(new Callback<TableColumn<UserRow, User>, TableCell<UserRow, User>>() {
            @Override
            public TableCell<UserRow, User> call(TableColumn<UserRow, User> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (empty || user == null) {
                            setGraphic(null);
                        } else {
                            VBox vbox = new VBox(5); // 5 pixels between image and name

                            // Load image
                            String url = user.getImageUrl();
                            URL imageUrl = getClass().getResource(url);
                            ImageView imageView = new ImageView(new Image(imageUrl.toString()));
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);

                            // Create label
                            Label nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
                            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                            vbox.getChildren().addAll(imageView, nameLabel);
                            setGraphic(vbox);
                        }
                    }
                };
            }
        });
    }
}