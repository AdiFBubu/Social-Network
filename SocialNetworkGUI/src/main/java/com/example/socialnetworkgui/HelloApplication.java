package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.UserController;
import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.FriendshipValidator;
import com.example.socialnetworkgui.domain.validators.UserValidator;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.repository.database.FriendshipDBRepository;
import com.example.socialnetworkgui.repository.database.UserDBRepository;
import com.example.socialnetworkgui.repository.file.UserRepository;
import com.example.socialnetworkgui.service.SocialNetwork;
import com.example.socialnetworkgui.utils.RepoOperations;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    Repository<Long, User> userRepository;
    SocialNetwork socialNetwork;

    @Override
    public void start(Stage stage) throws IOException {

        String username = "postgres";
        String password = "postgres";
        String url = "jdbc:postgresql://localhost:3491/SocialNetwork";

        Validator<User> userValidator = UserValidator::validate;
        userRepository = new UserDBRepository(url, username, password, userValidator);
        FriendshipValidator friendshipValidator = new FriendshipValidator(userRepository);
        Repository<Tuple<Long, Long>, Friendship> friendshipRepo = new FriendshipDBRepository(friendshipValidator::validate, url, username, password);
        socialNetwork = new SocialNetwork(userRepository, friendshipRepo);

        initView(stage);
        stage.setWidth(800);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/user-view.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        UserController userController = fxmlLoader.getController();
        userController.setSocialNetwork(socialNetwork);
    }
}