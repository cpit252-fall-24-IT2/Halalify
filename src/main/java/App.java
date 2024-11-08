import java.io.File;

public class App {
    public static void main(String[] args) {
        VideoProcessor videoProcessor = new VideoProcessor();
        File videoFile = videoProcessor.uploadVideo();

        if (videoFile == null) {
            System.err.println("No video selected. Exiting.");
            System.exit(1);
        }

        try {
            // Extract audio to "audio.wav" using FFmpeg (not shown here for simplicity)
            AudioTranscriber.transcribeAudio("audio.wav");
            System.out.println("Filter applied successfully.");
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
