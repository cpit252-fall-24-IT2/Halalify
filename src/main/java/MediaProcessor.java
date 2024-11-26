import java.io.File;
import java.io.IOException;

//This is the Facade design pattern, we separated this implementation from the main class to make it cleaner and more user-friendly.

public class MediaProcessor {

    public void processMedia() throws IOException {
        VideoProcessor videoProcessor = new VideoProcessor();
        File videoFile = videoProcessor.uploadVideo();

        if (videoFile == null) {
            System.err.println("No video selected. Exiting.");
            System.exit(1);
        }

        String audioPath = "audio.wav";
        if (!AudioExtractor.extractAudio(videoFile.getAbsolutePath(), audioPath)) {
            System.err.println("Failed to extract audio from video. Exiting.");
            System.exit(1);
        }

        try {
            String credentialsPath = "C:\\Users\\saadn\\Documents\\halalify-442817-b014fc4a0392.json";
            AudioTranscriber.configureGoogleCredentials(credentialsPath);

            AudioTranscriber.transcribeAudio(audioPath);
            System.out.println("Filter applied successfully.");
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

        System.out.println("Success!!!!!!!!!!");
    }
}