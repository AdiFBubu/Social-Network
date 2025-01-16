package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.service.SocialNetwork;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ProfileController {

    SocialNetwork socialNetwork;
    UserController userController;
    User user;
    User baseUser;

    @FXML
    public Button changeProfileImage;

    @FXML
    public Label urmaritoriLabel;

    @FXML
    public Label urmaririLabel;

    @FXML
    public ImageView profileImageView;

    @FXML
    public Label relatieLabel;

    @FXML
    public Button AddDeleteButton;

    @FXML
    public Button messageButton;


    Boolean friend = null;
    Boolean request = null;

    public void updateFollowFollowers(Iterable<Friendship> sth) {
        int urmaritori = 0, urmariri = 0;

        for (var friendship : sth) {
            if (friendship.getId().getE2().equals(user.getId()))
                urmaritori++;
            if (friendship.getId().getE1().equals(user.getId()) || ( friendship.getId().getE2().equals(user.getId()) && friendship.getState().equals(true) ))
                urmariri ++;
        }

        urmaririLabel.setText("Urmariri: " + " " + urmariri);
        urmaritoriLabel.setText("Urmaritori: " + " " + urmaritori);
    }

    public void init() {

//        if (user.getImageUrl().isEmpty())
//            profileImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("../../../../images/DefaultImage.png")).toExternalForm()));
//        else
            profileImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource(user.getImageUrl())).toExternalForm()));

        Iterable<Friendship> sth = socialNetwork.getAllFriendships();

        updateFollowFollowers(sth);

        if (user.equals(baseUser)) {
            relatieLabel.setText("Welcome " + user.getFirstName() + "!");
            AddDeleteButton.setVisible(false);
            messageButton.setVisible(false);
        }
        else {
            changeProfileImage.setVisible(false);
            for (var friendship : sth)
                if ( friendship.getId().getE1().equals(user.getId()) && friendship.getId().getE2().equals(baseUser.getId()) ) {
                    friend = friendship.getState().equals(true);
                    if (!friend)
                        request = true;
                    break;
                }
                else if (friendship.getId().getE1().equals(baseUser.getId()) && friendship.getId().getE2().equals(user.getId()) ) {
                    friend = friendship.getState().equals(true);
                    if (!friend)
                        request = false;
                    break;
            }
           updateFriendship();
        }


    }

    public void updateFriendship() {
        if (friend == null) {
            relatieLabel.setText("You are not friend yet with " + user.getFirstName() + " " + user.getLastName());
            AddDeleteButton.setText("Add friend");
        }
        else if (!friend){
            if (!request) {
                relatieLabel.setText(user.getFirstName() + " has not accepted your friend request yet!");
                AddDeleteButton.setText("Waiting for acceptance");
                AddDeleteButton.setDisable(true);
            }
            else {
                relatieLabel.setText(user.getFirstName() + " sent you a friend request!");
                AddDeleteButton.setText("Accept friend request");
            }
        }
        else {
            relatieLabel.setText("You are friend with " + user.getFirstName() + " " + user.getLastName());
            AddDeleteButton.setText("Remove friend");
        }
    }

    public void setService(SocialNetwork socialNetwork, UserController userController, User baseUser, User user) {
        this.socialNetwork = socialNetwork;
        this.userController = userController;
        this.baseUser = baseUser;
        this.user = user;
        init();
    }

    public void handleChangeProfileImage(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../views/select-profile-image-view.fxml"));

        try {
            AnchorPane root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Change your Profile Image");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            SelectProfileImageController controller = loader.getController();
            controller.setService(socialNetwork, user, dialogStage, profileImageView);
            dialogStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessage(ActionEvent actionEvent) {
        userController.openChatWindow(user);
    }

    public void handleAddDelete(ActionEvent actionEvent) {
        if (friend == null) {
            try {
                Optional<Friendship> u = this.socialNetwork.save(baseUser.getFirstName(), baseUser.getLastName(), user.getFirstName(), user.getLastName());
                if (u.isPresent()) {
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Add Friend", "A new friend has been added successfully");
                    friend = false;
                    request = false;
                }
            }
            catch (Exception e) {
                MessageAlert.showErrorMessage(null, "Fail");
            }
        }
        else if (friend){
            socialNetwork.delete(user.getFirstName(), user.getLastName(), baseUser.getFirstName(), baseUser.getLastName());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete Friendship", "Friendship has been removed!");
            friend = null;
            request = null;
        }
        else if (request) {
            var rez = socialNetwork.update(user.getFirstName(), user.getLastName(), baseUser.getFirstName(), baseUser.getLastName(), true);
            if (rez.isPresent())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend Request Update","You accepted the friend request!");
            friend = true;
            request = null;
        }
        updateFriendship();
        updateFollowFollowers(socialNetwork.getAllFriendships());
    }
}
