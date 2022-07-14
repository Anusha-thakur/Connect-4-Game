package com.anusha.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HelloController implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 80;

    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "4CAA88";

    private static String PLAYER_ONE = "Player One"; // Changes on User input
    private static String PLAYER_TWO = "Player Two"; // Changes on User input

    private boolean isPlayerOneTurn = true; // at beginning player one will insert // when it becomes false player two will play his turn
    // so this value will toggle between true and false

    private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS]; // For Structural Changes [For Developers]
    // This array will contain all the inserted disc objects.

    private boolean isAllowedToInsert = true; // Flag to prevent user from dropping multiple Discs at once

    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertedDiscsPane;

    @FXML
    public Label playerNameLabel;

    @FXML
    public TextField playerOneTextField;

    @FXML
    public TextField playerTwoTextField;

    @FXML
    public Button setNamesButton;

    public void createPlayGround() {

        setNamesButton.setOnAction(event ->
        {
            PLAYER_ONE = playerOneTextField.getText();
            PLAYER_TWO = playerTwoTextField.getText();
        });

        Shape rectangleWithHoles = createGameStructuralGrid();
        rootGridPane.add(rectangleWithHoles, 0, 1);  // Adding rectangleWithHoles to rootGridPane at (0,1)

        List<Rectangle> rectangleList = createClickableColumns();

        for (Rectangle rectangle : rectangleList) {
            rootGridPane.add(rectangle, 0, 1);  // Adding rectangles to rectangleLists
            // This will add our Hower Over Rectangles to our Grid Pane.
        }
    }

    private Shape createGameStructuralGrid() {

        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
        // columns + 1 and rows + 1 will generate extra space in both row and column
        // if we do not add 1 there will no space between the disks

        for (int row = 0; row < ROWS; row++) {  // 2D Array of Circles
            for (int col = 0; col < COLUMNS; col++) {

                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2); // the horizontal position of the center of the circle in pixels
                circle.setCenterY(CIRCLE_DIAMETER / 2); //the vertical position of the center of the circle in pixels
                circle.setSmooth(true); // this will make the circle edges smoother

                // Below 2 lines will set the coordinate for each circle
                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                // CIRCLE_DIAMETER / 4 = 20 pixels

                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle); // When we subtract circle from rectangle we will get a hole.
            }
        }

        rectangleWithHoles.setFill(Color.WHITE);

        return rectangleWithHoles;
    }


    private List<Rectangle> createClickableColumns() {

        List<Rectangle> rectangleList = new ArrayList<>();

        for (int col = 0; col < COLUMNS; col++) {

            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

            rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26"))); // Hover Effect
            // When we Hover Over on Each reactangle it will become light grey

            rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));
            // The rectangle will again come back to its original color when we remove our cursor

            final int column = col;
            rectangle.setOnMouseClicked(mouseEvent -> {
                if(isAllowedToInsert) {
                    isAllowedToInsert = false; // Prevents user from dropping multiple Discs at once
                    insertDisc(new Disc(isPlayerOneTurn), column);
                }
            });

            rectangleList.add(rectangle);
        }

        return rectangleList;
    }

    private void insertDisc(Disc disc, int column) {

        // This will determine the position of our new disk
        int row = ROWS - 1;  // Array index start from 0 . So we decremented 1 from ROWS

        while (row >= 0) {
            if (getDiscIfPresent(row, column) == null)
                break;  // To place Disc on the Top of the column

            row--;
        }

        if (row < 0) { // If it is full, we cannot insert anymore disc
            return;
        }

        insertedDiscsArray[row][column] = disc; //For Structural Changes: For Developers
        insertedDiscsPane.getChildren().add(disc);  // For Visual Changes : For Users

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4.0);

        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

        translateTransition.setOnFinished(actionEvent -> {

            isAllowedToInsert = true; // Allows next Disc to be dropped
            if (gameEnded(currentRow, column)) {
                gameOver();
                return;  // Ends the Game
            }

            isPlayerOneTurn = !isPlayerOneTurn;  // Toggling b/w the Players
            playerNameLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);  // Changing Player
        });

        translateTransition.play();  // To play the Animation
    }

    private boolean gameEnded(int row, int col) {

        // vertical Points. A small example: Player has inserted his last disc at row = 2, column = 3.

        // index of each element present in column [row][column] : 0,3  1,3  2,3  3,3  4,3  5,3

        // Point2D class hold the value of x and y in terms of coordinate
        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3).  // 0,1,2,3,4,5
                mapToObj(r -> new Point2D(r, col)).  // Point2D --> 0,3  1,3  2,3  3,3  4,3  5,3
                collect(Collectors.toList());    // This whole will check for vertical win.

        List<Point2D> horizontalPoints = IntStream.rangeClosed(col - 3, col + 3).
                mapToObj(c -> new Point2D(row, c)).
                collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row - 3, col + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint1.add(i, -i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3, col - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint2.add(i, i))
                .collect(Collectors.toList());

        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points); //Checking for Matches

        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> points) {

        int chain = 0;

        for (Point2D point : points) {
            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) { //If the last inserted disc belongs to the current player
                chain++;
                if (chain == 4) {
                    return true;
                }
            } else{
                chain = 0;
            }
        }
        return false;
    }

    // Below Method will help to resolve ArrayIndexOutOfBound Exception
    private Disc getDiscIfPresent(int row, int col) {  //To Prevent ArrayIndexOutOfBound Exception

        if (row >= ROWS || row < 0 || col >= COLUMNS || col < 0) {  //If Row or Column Index is Invalid
            return null;
        }
        return insertedDiscsArray[row][col];
    }

    private void gameOver() {

        String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
        System.out.println("Winner is: " + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The Winner is " + winner + "!!");
        alert.setContentText("Want to play Again?");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Platform.runLater(() -> {

            Optional<ButtonType> btnClicked = alert.showAndWait();
            if(btnClicked.isPresent() && btnClicked.get() == yesBtn){

                //Yes will reset the game
                resetGame();
            }
            else{
                //No will exit the game
                Platform.exit();  //Closes the Virtual Machine
                System.exit(0); //Closes the Application
            }
        });
    }

    public void resetGame() {

        //Visually
        insertedDiscsPane.getChildren().clear(); //Remove all inserted discs from pane

        //Structurally
        for(int row = 0; row < insertedDiscsArray.length; row++){
            for(int col = 0; col < insertedDiscsArray[row].length; col++){

                insertedDiscsArray[row][col] = null; //Make All elements of insertedDiscsArray = null
            }
        }

        isPlayerOneTurn = true;
        playerNameLabel.setText(PLAYER_ONE);

        createPlayGround(); //Prepare a fresh ground
    }

    // Disc class will help us to determine the color of the disc according to the player.
    private static class Disc extends Circle {

        private static boolean isPlayerOneMove;

        public Disc(boolean isPlayerOneMove) {
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER / 2);
            setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER / 2);
            setCenterY(CIRCLE_DIAMETER / 2);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}