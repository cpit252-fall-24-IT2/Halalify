import java.io.File;
import java.io.IOException;

public class AudioExtractor {
    public static boolean extractAudio(String videoPath, String audioPath) {
        String ffmpegPath = "C:\\Users\\saadn\\Downloads\\ffmpeg\\ffmpeg-2024-11-25-git-04ce01df0b-essentials_build\\bin\\ffmpeg.exe"; // Replace with the full path to ffmpeg if not in PATH

        ProcessBuilder processBuilder = new ProcessBuilder(
                ffmpegPath,
                "-i", videoPath,
                "-vn",
                "-acodec", "pcm_s16le",
                "-ar", "44100",
                "-ac", "2",
                audioPath
        );
        processBuilder.redirectErrorStream(true); // Redirect FFmpeg error messages to standard output

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor(); // Wait for FFmpeg to finish
            return exitCode == 0; // Return true if FFmpeg exited successfully
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during audio extraction: " + e.getMessage());
            return false;
        }
    }
}
