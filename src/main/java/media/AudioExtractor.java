package media;

import java.io.BufferedReader;
import java.io.IOException;
import java.io .*;

public class AudioExtractor {
    public static boolean extractAudio(String videoFilePath, String audioFilePath) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "C:\\Users\\saadn\\Downloads\\ffmpeg\\ffmpeg-2024-11-25-git-04ce01df0b-essentials_build\\bin\\ffmpeg.exe", // Path to ffmpeg.exe
                "-y", // Overwrite output files without asking
                "-i", videoFilePath, // Input file
                "-vn", // Disable video
                "-acodec", "pcm_s16le", // Audio codec
                "-ar", "48000", // Sample rate
                "-ac", "1", // Mono channel
                audioFilePath // Output audio file
        );

        System.out.println("FFmpeg path: C:\\Users\\saadn\\Downloads\\ffmpeg\\ffmpeg-2024-11-25-git-04ce01df0b-essentials_build\\bin\\ffmpeg.exe");
        System.out.println("Video file path: " + videoFilePath);
        System.out.println("Audio file path: " + audioFilePath);

        processBuilder.redirectErrorStream(true); // Merge stdout and stderr

        try {
            Process process = processBuilder.start();

            // Capture FFmpeg output for debugging
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor(); // Wait for FFmpeg to finish
            if (exitCode == 0) {
                System.out.println("Audio extraction completed successfully.");
                return true;
            } else {
                System.err.println("FFmpeg exited with error code: " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during audio extraction: " + e.getMessage());
            return false;
        }
    }
}