import java.io.IOException;

public class App {

    public static void main(String[] args) {
        try {
            MediaProcessor mediaProcessor = new MediaProcessor();
            mediaProcessor.processMedia();
            System.out.println("Media processing completed successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
