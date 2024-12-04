package media;

import filter.Filter;
import filter.FilterFactory;

import java.io.File;
import java.util.List;

import static media.VideoAudioMerger.merge;

public class MediaProcessor {

    private String ffmpegPath;

    public void setFfmpegPath(String ffmpegPath) throws Exception {
        File file = new File(ffmpegPath);
        if (!file.exists() || !file.isFile() || !file.getName().equalsIgnoreCase("ffmpeg.exe")) {
            throw new Exception("Invalid FFmpeg path. Please select the correct FFmpeg executable.");
        }
        this.ffmpegPath = ffmpegPath;
    }


    public void processMedia(File videoFile, String action) throws Exception {
        if (ffmpegPath == null || ffmpegPath.isEmpty()) {
            throw new Exception("FFmpeg path is not set.");
        }

        String audioPath = "C:\\Users\\saadn\\Documents\\audio.wav";
        String modifiedAudioPath = "C:\\Users\\saadn\\Documents\\modified_audio.wav";
        String outputVideoPath = "C:\\Users\\saadn\\Documents\\output_video.mp4";

        System.out.println("Extracting audio from video...");
        if (!AudioExtractor.extractAudio(videoFile.getAbsolutePath(), audioPath)) {
            throw new Exception("Failed to extract audio from video.");
        }

        System.out.println("Configuring IBM Watson credentials...");
        String credentialsPath = "C:\\Users\\saadn\\Downloads\\ibm-credentials.env";
        AudioTranscriber.configureIBMCredentials(credentialsPath);

        System.out.println("Transcribing audio...");
        List<Double> badWordTimestamps = AudioTranscriber.transcribeAudio(audioPath);
        if (badWordTimestamps == null || badWordTimestamps.isEmpty()) {
            System.out.println("No bad words detected. Skipping audio modification.");
            merge(videoFile.getAbsolutePath(), audioPath, outputVideoPath);
            System.out.println("Processing completed. Output video: " + outputVideoPath);
            return;
        }

        System.out.println("Applying filter: " + action);
        Filter filter = FilterFactory.getFilter(action);
        filter.apply(audioPath, modifiedAudioPath, badWordTimestamps);

        System.out.println("Merging modified audio with video...");
        if (!merge(videoFile.getAbsolutePath(), modifiedAudioPath, outputVideoPath)) {
            throw new Exception("Failed to merge modified audio with video.");
        }

        System.out.println("Processing completed. Output video: " + outputVideoPath);
    }
}