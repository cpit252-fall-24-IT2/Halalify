package media;

import java.io.IOException;

public class VideoAudioMerger {

    public static boolean merge(String videoPath, String audioPath, String outputVideoPath) {
        try {
            // Build the FFmpeg command
            String command = String.format(
                    "ffmpeg -i \"%s\" -i \"%s\" -c:v copy -map 0:v:0 -map 1:a:0 \"%s\" -y",
                    videoPath, audioPath, outputVideoPath
            );

            // Execute the command
            Process process = Runtime.getRuntime().exec(command);

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Check if the process completed successfully
            if (exitCode == 0) {
                System.out.println("Audio and video merged successfully.");
                return true;
            } else {
                System.err.println("Error merging audio and video. Exit code: " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while merging audio and video: " + e.getMessage());
            return false;
        }
    }
}