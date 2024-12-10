package media;

import filter.Filter;
import filter.FilterFactory;
import java.io.File;
import java.util.List;

import static media.VideoAudioMerger.merge;

public class MediaProcessor {

    private String ffmpegPath;
    private File outputVideoPath;

    public void setFfmpegPath(String ffmpegPath) throws Exception {
        File file = new File(ffmpegPath);
        if (!file.exists() || !file.isFile() || !file.getName().equalsIgnoreCase("ffmpeg.exe")) {
            throw new IllegalArgumentException("Invalid FFmpeg path. Please select the correct FFmpeg executable.");
        }
        this.ffmpegPath = ffmpegPath;
    }

    public void setVideoOutputPath(File outputVideoPath) {
        this.outputVideoPath = outputVideoPath;
    }

    public void processMedia(File videoFile, String action) throws Exception {
        validateFFmpegPath();

        String baseOutputPath = outputVideoPath.getAbsolutePath();
        String audioPath = baseOutputPath + "\\audio.wav";
        String modifiedAudioPath = baseOutputPath + "\\modified_audio.wav";
        String finalVideoPath = baseOutputPath + "\\Modified_video.mp4";

        System.out.println("Extracting audio from video...");
        if (!AudioExtractor.extractAudio(videoFile.getAbsolutePath(), audioPath, ffmpegPath)) {
            throw new Exception("Failed to extract audio from video.");
        }

        configureIBMCredentials();

        System.out.println("Transcribing audio...");
        List<Double> badWordTimestamps = AudioTranscriber.transcribeAudio(audioPath);

        if (badWordTimestamps.isEmpty()) {
            System.out.println("No bad words detected. Skipping audio modification.");
            mergeFiles(videoFile.getAbsolutePath(), audioPath, finalVideoPath);
            return;
        }

        System.out.println("Applying filter: " + action);
        Filter filter = FilterFactory.getFilter(action);
        filter.apply(audioPath, modifiedAudioPath, badWordTimestamps);

        System.out.println("Merging modified audio with video...");
        mergeFiles(videoFile.getAbsolutePath(), modifiedAudioPath, finalVideoPath);
    }

    private void validateFFmpegPath() throws Exception {
        if (ffmpegPath == null || ffmpegPath.isEmpty()) {
            throw new IllegalStateException("FFmpeg path is not set.");
        }
    }

    private void configureIBMCredentials() throws Exception {
        String credentialsPath = "C:\\Users\\saadn\\Downloads\\ibm-credentials.env";
        System.out.println("Configuring IBM Watson credentials...");
        AudioTranscriber.configureIBMCredentials(credentialsPath);
    }

    private void mergeFiles(String videoPath, String audioPath, String outputPath) throws Exception {
        if (!merge(videoPath, audioPath, outputPath)) {
            throw new Exception("Failed to merge audio and video.");
        }
        System.out.println("Processing completed. Output video: " + outputPath);
    }
}