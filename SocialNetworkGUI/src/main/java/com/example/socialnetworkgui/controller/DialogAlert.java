package com.example.socialnetworkgui.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;

public class DialogAlert {

    public static void showNonModalDialog(Stage ownerStage, String header, String text) {
        // Creăm un dialog non-modal
        Stage dialog = new Stage();
        StackPane dialogLayout = new StackPane();

        Label label = new Label(text);

        // Creăm un buton pentru a închide dialogul
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> dialog.close());

        dialogLayout.getChildren().addAll(label, closeButton);
        dialog.initModality(Modality.WINDOW_MODAL);



        // Setăm proprietăți
        dialog.setTitle(header);

        // Setăm feronța părinte ca owner, dar dialogul nu va bloca interfața
        dialog.initOwner(ownerStage);

        StackPane.setAlignment(label, javafx.geometry.Pos.TOP_CENTER); // Poziționăm textul sus
        StackPane.setAlignment(closeButton, javafx.geometry.Pos.BOTTOM_CENTER); // Poziționăm butonul jos

        Scene dialogScene = new Scene(dialogLayout, 300, 150);
        dialog.setScene(dialogScene);


        // Afișăm dialogul non-modal
        dialog.show();
    }
}
