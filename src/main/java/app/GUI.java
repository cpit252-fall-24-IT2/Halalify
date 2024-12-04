package app;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import media.MediaProcessor;

import java.io.File;

public class GUI extends Application {

    private File selectedFile;
    private String ffmpegPath; // Store the selected FFmpeg path
    private final MediaProcessor mediaProcessor = new MediaProcessor();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Video Profanity Processor");

        Label statusLabel = new Label("Please select a video file to process.");
        Button selectFileButton = new Button("Select Video");
        selectFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Video File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mkv", "*.avi", "*.mov")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile = file;
                statusLabel.setText("Selected file: " + file.getName());
            } else {
                statusLabel.setText("No file selected.");
            }
            if (selectedFile != null) {
                final long MAX_SIZE = 1073741824L; // 1GB
                if (selectedFile.length() > MAX_SIZE) {
                    System.err.println("File size is above the 1GB limit. Please choose a smaller file.");
                }
                System.out.println("Video selected: " + selectedFile.getAbsolutePath());
            } else {
                System.out.println("No video selected.");
            }
        });

        Button setFfmpegPathButton = new Button("Set FFmpeg Path");
        setFfmpegPathButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select FFmpeg Executable");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Executable Files", "*.exe")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                ffmpegPath = file.getAbsolutePath();
                statusLabel.setText("FFmpeg Path Set: " + ffmpegPath);
                System.out.println("FFmpeg Path Set: " + ffmpegPath);
            } else {
                statusLabel.setText("No FFmpeg path selected.");
                System.out.println("No FFmpeg path selected.");
            }
        });

        Button muteButton = new Button("Mute Bad Words");
        muteButton.setOnAction(e -> processMedia("MUTE", statusLabel));

        Button beepButton = new Button("Beep Bad Words");
        beepButton.setOnAction(e -> processMedia("BEEP", statusLabel));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(selectFileButton, setFfmpegPathButton, muteButton, beepButton, statusLabel);

        Scene scene = new Scene(layout, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void processMedia(String action, Label statusLabel) {
        if (ffmpegPath == null || ffmpegPath.isEmpty()) {
            statusLabel.setText("Please set the FFmpeg path first.");
            return;
        }
        if (selectedFile != null) {
            statusLabel.setText("Processing: " + action + " bad words in " + selectedFile.getName());
            try {
                mediaProcessor.setFfmpegPath(ffmpegPath); // Pass the FFmpeg path to the media processor
                mediaProcessor.processMedia(selectedFile, action);
                statusLabel.setText("Processing completed successfully.");
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
            }
        } else {
            statusLabel.setText("Please select a video file first.");
        }
    }
}