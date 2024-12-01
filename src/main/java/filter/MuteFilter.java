package filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MuteFilter implements Filter {

    @Override
    public void apply(String inputAudioPath, String outputAudioPath, List<Double> badWordTimestamps) throws IOException {
        String ffmpegPath = "C:\\Users\\saadn\\Downloads\\ffmpeg\\ffmpeg-2024-11-25-git-04ce01df0b-essentials_build\\bin\\ffmpeg.exe";

        // Create a filter_complex string to mute sections
        StringBuilder filterComplex = new StringBuilder();
        for (double timestamp : badWordTimestamps) {
            filterComplex.append(String.format(
                    "volume=enable='between(t,%f,%f)':volume=0,", timestamp, timestamp + 1
            ));
        }
        String filter = filterComplex.toString();

        // Construct the FFmpeg command
        String[] command = {
                ffmpegPath,
                "-i", inputAudioPath,
                "-af", filter.substring(0, filter.length() - 1), // Remove trailing comma
                outputAudioPath
        };

        // Execute FFmpeg command
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            throw new IOException("Error applying mute filter: " + e.getMessage(), e);
        }
    }
}