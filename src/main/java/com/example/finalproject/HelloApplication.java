package com.example.finalproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class HelloApplication extends Application {
    //
    // CONSTANTS
    //
    int WORD_COUNT = 10; // Default is set only as a precaution

    //
    // ACCESS GLOBALS
    //
    private Label lb;
    private Scene testScene;
    private final Button startButton = new Button("Start");
    private Scene resultsScene;
    private Stage s;
    private final ProgressBar progress = new ProgressBar(0);
    private final String[] progressColors = new String[]{"FF0000", "FF1100", "FF2200", "FF3300", "FF4400", "FF5500", "FF6600", "FF7700", "FF8800", "FF9900", "FFAA00", "FFBB00", "FFCC00", "FFDD00", "FFEE00", "FFFF00", "EEFF00", "DDFF00", "CCFF00", "BBFF00", "AAFF00", "99FF00", "88FF00", "77FF00", "66FF00", "55FF00", "44FF00", "33FF00", "22FF00", "11FF00", "00FF00"};


    public void start(Stage stage) throws IOException {
        // Make stage accessible globally
        s = stage;

        // Set up initial stage and scene
        stage.setTitle("Typing Speed Test");
        Scene sc = makeScene();
        stage.setScene(sc);
        stage.show();
        stage.setResizable(false);

        // 'Start' button functionality
        startButton.setOnAction((e) -> {
            stage.setScene(difficultySelectionScreen());
        });
    }

    private Scene makeScene() {
        // Create primary and secondary container
        BorderPane bp = new BorderPane();
        VBox vb = new VBox();
        vb.setSpacing(30);
        vb.setAlignment(Pos.CENTER);
        bp.setCenter(vb);

        // Create and style heading
        Label title = new Label("Typing Test");
        title.setFont(new Font("Papyrus", 100));
        title.setAlignment(Pos.TOP_CENTER);

        // 'Start' button styling
        startButton.setPadding(new Insets(25, 25, 25, 25));
        startButton.setFont(Font.font("Papyrus", FontWeight.BOLD, 36.0));

        // Add title and button to scene
        vb.getChildren().addAll(title, startButton);

        return new Scene(bp, 800, 600);
    }

    private Scene difficultySelectionScreen() {
        // Create primary containers
        BorderPane outerContainer = new BorderPane();
        VBox centeredContainer = new VBox();
        HBox buttonContainer = new HBox();
        centeredContainer.setSpacing(30);
        centeredContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(30);
        buttonContainer.setAlignment(Pos.CENTER);
        outerContainer.setCenter(centeredContainer);

        // Create, style, and add title to container
        Label title = new Label("Select Difficulty");
        title.setFont(new Font("Papyrus", 100));
        title.setAlignment(Pos.TOP_CENTER);
        centeredContainer.getChildren().add(title);

        // Create, style, and add difficulty buttons to container
        Button easyButton = new Button("Easy\n(10 Words)");
        Button mediumButton = new Button("Medium\n(25 Words)");
        Button hardButton = new Button("Hard\n(50 Words)");
        Button extremeButton = new Button("Extreme\n(100 Words)");
        easyButton.setTextAlignment(TextAlignment.CENTER);
        mediumButton.setTextAlignment(TextAlignment.CENTER);
        hardButton.setTextAlignment(TextAlignment.CENTER);
        extremeButton.setTextAlignment(TextAlignment.CENTER);
        buttonContainer.getChildren().addAll(easyButton, mediumButton, hardButton, extremeButton);
        centeredContainer.getChildren().add(buttonContainer);

        // Define button functionality
        easyButton.setOnAction((e) -> startTest(10));
        mediumButton.setOnAction((e) -> startTest(25));
        hardButton.setOnAction((e) -> startTest(50));
        extremeButton.setOnAction((e) -> startTest(100));

        return new Scene(outerContainer, 800, 600);
    }

    private void startTest(Scene sc) {
        /* Ghetto timer solution
                - AtomicReference ensures that all operations upon the reference are completed prior to access
                - For some reason it wanted the timer to be final or "effectively final" within the lambda
                - Got around this by making the timer information atomic using AtomicReference
         */
        AtomicLong startTime = new AtomicLong();
        AtomicLong difference = new AtomicLong();
        AtomicBoolean isFirstCharacterTyped = new AtomicBoolean(true);
        double initialLength = lb.getText().length();

        // Primary logic for the display of words
        sc.setOnKeyTyped((e) -> {
            if(isFirstCharacterTyped.get()) {
                startTime.set(System.currentTimeMillis());
                isFirstCharacterTyped.set(false);
            }
            // Checks typed characters against the next character in the test text
            if(e.getCharacter().equals(String.valueOf(lb.getText().charAt(0)))) {
                // So long as the test text is longer than 1 character, remove the first character
                if(lb.getText().length() > 1) {
                    lb.setText(lb.getText().substring(1));
                // Final character logic
                } else {
                    // Kill key typed logic
                    sc.setOnKeyTyped((event) -> {});

                    // Set remaining text to nothing
                    lb.setText("");

                    // Calculate time elapsed
                    difference.set(System.currentTimeMillis() - startTime.get());

                    // Convert time from milliseconds to seconds and proceed to results scene
                    resultsScene = makeResultScene((double)difference.get() / 1000);
                    s.setScene(resultsScene);
                    //System.out.println((double)difference.get().get() / 1000);
                }

                // Update progress bar progress and color
                double percentDone = (double)1 - (lb.getText().length() / initialLength);
                progress.setProgress(percentDone);
                progress.setStyle("-fx-accent: #" + progressColors[(int) Math.ceil(progressColors.length * percentDone) - 1]);
            }
        });
    }

    private Scene makeTestScene() {
        // Create and style primary container, labels
        BorderPane bp = new BorderPane();
        lb = new Label(generateWordList(WORD_COUNT));
        lb.setEllipsisString("");
        lb.setFont(Font.font("Papyrus", FontWeight.BOLD, 96.0));
        bp.setCenter(lb);

        // Spacer to achieve the floating-in-the-middle-of-the-screen effect
        Rectangle spacer = new Rectangle();
        spacer.setWidth(400);
        bp.setLeft(spacer);

        // ProgressBar styling
        HBox hb = new HBox();
        //hb.getChildren().add(progress);
        hb.setAlignment(Pos.CENTER);
        hb.setPadding(new Insets(100));
        bp.setBottom(progress);
        progress.setPrefSize(800, 30);
        progress.setStyle("-fx-accent: #FF0000");

        return new Scene(bp, 800, 600);
    }

    // Create the results scene given the time (in seconds) it took to complete the
    private Scene makeResultScene(double time) {
        // Create and style containers
        BorderPane bp = new BorderPane();
        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        bp.setCenter(vb);

        // Create the title and other labels
        Label title = new Label("Results");
        title.setFont(Font.font("Papyrus", FontWeight.BOLD, 72.0));
        title.setAlignment(Pos.TOP_CENTER);
        Label speed = new Label(Math.round(((double)WORD_COUNT / time) * 60) + " WPM"); // Calculate WPM from time elapsed

        // Add all to the container
        vb.getChildren().addAll(title, speed);

        return new Scene(bp, 800, 600);
    }

    /*
     *  UTILITY METHODS
     */
    // Generate the test text from word list file
    private String generateWordList(int numWords) {
        // Read all words from file
        ArrayList<String> words = MyFile.readFile("words.csv");

        // Shuffle the word list for uniqueness between tests
        Collections.shuffle(words);

        // Create the test text from the shuffled list of words
        StringBuilder wordList = new StringBuilder();
        for(int i = 0; i < numWords; i++) {
            wordList.append(words.get(i)).append(" ");
        }

        return String.valueOf(wordList).trim();
    }

    // Initializes the test scene with a given number of words
    // Just to cut down on repeated code
    private void startTest(int numWords) {
        WORD_COUNT = numWords;
        testScene = makeTestScene();
        s.setScene(testScene);
        startTest(testScene);
    }

    public static void main(String[] args) {
        launch();
    }
}