import java.io.File;

public class App {
    public static void main(String[] args) {

        VideoProcessor videoProcessor = new VideoProcessor();

        File videoFile = videoProcessor.uploadVideo();
        if (videoFile == null) {
            System.err.println("No video selected. Exiting.");;
            System.exit(1);
        }
    }
}
