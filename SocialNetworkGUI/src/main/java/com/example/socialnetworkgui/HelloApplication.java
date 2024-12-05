package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controller.AuthController;
import com.example.socialnetworkgui.controller.UserController;
import com.example.socialnetworkgui.domain.*;
import com.example.socialnetworkgui.domain.validators.*;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.repository.database.AuthDBRepository;
import com.example.socialnetworkgui.repository.database.FriendshipDBRepository;
import com.example.socialnetworkgui.repository.database.MessageDBRepository;
import com.example.socialnetworkgui.repository.database.UserDBRepository;
import com.example.socialnetworkgui.repository.file.UserRepository;
import com.example.socialnetworkgui.repository.paging.FriendshipPagingDBRepository;
import com.example.socialnetworkgui.repository.paging.FriendshipPagingRepository;
import com.example.socialnetworkgui.repository.paging.UserPagingDBRepository;
import com.example.socialnetworkgui.repository.paging.UserPagingRepository;
import com.example.socialnetworkgui.service.AuthService;
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
    UserPagingRepository userPagingRepository;
    Repository<Long, Message> messageRepository;
    Repository<Long, Account> accountRepository;
    SocialNetwork socialNetwork;
    AuthService authService;

    @Override
    public void start(Stage stage) throws IOException {

        String username = "postgres";
        String password = "postgres";
        String url = "jdbc:postgresql://localhost:3491/SocialNetwork";

        Validator<User> userValidator = UserValidator::validate;
        userRepository = new UserDBRepository(url, username, password, userValidator);
        userPagingRepository = new UserPagingDBRepository(url, username, password, userValidator);
        Validator<Message> messageValidator = MessageValidator::validate;
        messageRepository = new MessageDBRepository(url, username, password, messageValidator);
        FriendshipValidator friendshipValidator = new FriendshipValidator(userRepository);
        Repository<Tuple<Long, Long>, Friendship> friendshipRepo = new FriendshipDBRepository(friendshipValidator::validate, url, username, password);
        FriendshipPagingRepository friendshipPagingRepository = new FriendshipPagingDBRepository(url, username, password, friendshipValidator::validate);
        socialNetwork = new SocialNetwork(userPagingRepository, friendshipPagingRepository, messageRepository);

        Validator<Account> accountValidator = AccountValidator::validate;
        accountRepository = new AuthDBRepository(url, username, password, accountValidator);
        authService = new AuthService(accountRepository);

        initView(stage);
        stage.setWidth(800);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/auth-view.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        AuthController authController = fxmlLoader.getController();
        authController.setService(authService, primaryStage, socialNetwork);
    }
}