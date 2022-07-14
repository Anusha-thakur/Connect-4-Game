package com.anusha.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {

    private HelloController controller;

    // Mandatory to override Start method
    // Init & Stop are optional to override
    @Override
    public void start(Stage stage) throws IOException { // Stage is outermost container of the app

        // fxmlLoader connects HelloApplication with FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("game.fxml"));
        GridPane rootGridPane = fxmlLoader.load();  // Loads rootNode as GridPane

        controller = fxmlLoader.getController();
        controller.createPlayGround();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(stage.widthProperty());  // Matches the menuBar width with primaryStage width

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);

        // Setting the scene
        stage.setScene(scene);
        stage.setTitle("Connect Four");
        stage.setResizable(false);  // If you want to prevent the user from resizing the Stage
        stage.show();
    }

    private MenuBar createMenu() {

        //File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.resetGame());

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(actionEvent -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem(); // allows for a horizontal Separator to be embedded within it

        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(actionEvent -> exitGame());

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

        //Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Connect4");
        aboutGame.setOnAction(actionEvent -> aboutConnect4());

        SeparatorMenuItem separator = new SeparatorMenuItem();

        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(actionEvent -> aboutMe());

        helpMenu.getItems().addAll(aboutGame, separator, aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu); //Adding Menus to Menu Bar

        return menuBar;
    }

    private void aboutMe() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Anusha Thakur");
        alert.setContentText("Hi! I am Anusha, a developer focused on crafting " +
                "great web/app experiences. Designing and Coding have been my passion since " +
                "the days I started working with computers but I found myself into app " +
                "design/development since 2020. I enjoy creating beautifully designed" +
                "intuitive and functional applications & websites. Being an Engineer " +
                "I believe in using science to find creative practical solutions." +
                "My other hobbies includes Sketching, Cooking & Reading.");

        alert.show(); // To display the About section
    }

    private void aboutConnect4() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play?");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color " +
                "and then take turns dropping colored discs from the top into a seven-column, six-row vertically " +
                "suspended grid. The pieces fall straight down, occupying the next available space within the column. " +
                "The objective of the game is to be the first to form a horizontal, vertical, " +
                "or diagonal line of four of one's own discs. Connect Four is a solved game. " +
                "The first player can always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();   // Closes the Virtual Machine
        System.exit(0);  // Closes the Application
    }

    public static void main(String[] args) {
        launch();
    }
}