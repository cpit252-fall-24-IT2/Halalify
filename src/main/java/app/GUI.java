package app;

import javafx.application.Application;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import media.MediaProcessor;
import java.io.File;

public class GUI extends Application {

    private File selectedFile;
    private File outputDirectory;
    private String ffmpegPath;
    private final MediaProcessor mediaProcessor = new MediaProcessor();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Video Profanity Processor");

        Label statusLabel = new Label("Please select a video file to process.");

        // Create buttons
        Button selectFileButton = createFileButton(
                "Select Video",
                "Select Video File",
                file -> {
                    selectedFile = file;  // Store the selected file
                    updateStatus(statusLabel, "Selected file: " + file.getName()); // Update UI
                },
                "*.mp4", "*.mkv", "*.avi", "*.mov"
        );


        Button setFfmpegPathButton = createFileButton(
                "Set FFmpeg Path",
                "Select FFmpeg Executable",
                file -> {
                    ffmpegPath = file.getAbsolutePath();
                    updateStatus(statusLabel, "FFmpeg Path Set: " + ffmpegPath);
                },
                "*.exe"
                );

        Button setOutputDirectoryButton = createDirectoryButton("Set Output Directory", directory -> {
            outputDirectory = directory;
            updateStatus(statusLabel, "Output directory set to: " + directory.getAbsolutePath());
        });

        Button muteButton = createProcessingButton("Mute Bad Words", "MUTE", statusLabel);
        Button beepButton = createProcessingButton("Bleep Bad Words", "BLEEP", statusLabel);

        // Layout
        VBox layout = new VBox(10, selectFileButton, setFfmpegPathButton, setOutputDirectoryButton, muteButton, beepButton, statusLabel);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createFileButton(String buttonText, String dialogTitle, FileHandler handler, String... extensions) {
        Button button = new Button(buttonText);
        button.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(dialogTitle);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Supported Files", extensions));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) handler.handle(file);
        });
        return button;
    }


    private Button createDirectoryButton(String buttonText, DirectoryHandler handler) {
        Button button = new Button(buttonText);
        button.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Directory");
            File directory = directoryChooser.showDialog(null);
            if (directory != null) handler.handle(directory);
        });
        return button;
    }

    private Button createProcessingButton(String buttonText, String action, Label statusLabel) {
        Button button = new Button(buttonText);
        button.setOnAction(e -> processMedia(action, statusLabel));
        return button;
    }

    private void processMedia(String action, Label statusLabel) {
        if (ffmpegPath == null || selectedFile == null || outputDirectory == null) {
            updateStatus(statusLabel, "Ensure FFmpeg, video file, and output directory are set.");
            return;
        }

        updateStatus(statusLabel, "Processing: " + action + " bad words in " + selectedFile.getName());
        try {
            mediaProcessor.setFfmpegPath(ffmpegPath);
            mediaProcessor.setVideoOutputPath(outputDirectory);
            mediaProcessor.processMedia(selectedFile, action);
            updateStatus(statusLabel, "Processing completed. Output saved to: " + new File(outputDirectory, "processed_" + selectedFile.getName()).getAbsolutePath());
        } catch (Exception e) {
            updateStatus(statusLabel, "Error: " + e.getMessage());
        }
    }

    private void updateStatus(Label statusLabel, String message) {
        statusLabel.setText(message);
        System.out.println(message);
    }

    @FunctionalInterface
    interface FileHandler {
        void handle(File file);
    }

    @FunctionalInterface
    interface DirectoryHandler {
        void handle(File directory);
    }
}