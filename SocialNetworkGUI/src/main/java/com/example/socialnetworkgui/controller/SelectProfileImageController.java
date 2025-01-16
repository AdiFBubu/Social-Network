package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.service.SocialNetwork;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.Objects;

public class SelectProfileImageController {

    SocialNetwork SocialNetwork;
    User user;
    Stage stage;
    ImageView profileImageView;

    @FXML
    public ImageView image1;

    @FXML
    public ImageView image2;

    @FXML
    public ImageView image3;

    @FXML
    public ImageView image4;

    @FXML
    private void initialize() {
        image1.setImage(new Image(Objects.requireNonNull(getClass().getResource("../../../../images/BeeImage.jpg")).toExternalForm()));
        image1.setUserData("../../../../images/BeeImage.jpg");
        image2.setImage(new Image(Objects.requireNonNull(getClass().getResource("../../../../images/3d-cartoon-baby-genius-photo.jpg")).toExternalForm()));
        image2.setUserData("../../../../images/3d-cartoon-baby-genius-photo.jpg");
        image3.setImage(new Image(Objects.requireNonNull(getClass().getResource("../../../../images/DonaldDuckImage.jpg")).toExternalForm()));
        image3.setUserData("../../../../images/DonaldDuckImage.jpg");
        image4.setImage(new Image(Objects.requireNonNull(getClass().getResource("../../../../images/PenguinImage.jpg")).toExternalForm()));
        image4.setUserData("../../../../images/PenguinImage.jpg");

    }

    public void setService(SocialNetwork socialNetwork, User user, Stage stage, ImageView profileImageView) {
        this.SocialNetwork = socialNetwork;
        this.user = user;
        this.stage = stage;
        this.profileImageView = profileImageView;
    }

    @FXML
    public void handleImageClick(MouseEvent mouseEvent) {

        // Verifică care dintre imagini a fost selectată
        ImageView clickedImageView = (ImageView) mouseEvent.getSource();
        Image selectedImage = clickedImageView.getImage();

        String imagePath = (String) clickedImageView.getUserData();

        SocialNetwork.update(user.getFirstName(), user.getLastName(), imagePath);

        // Actualizează imaginea de profil
        profileImageView.setImage(selectedImage);

        stage.close();
    }
}
