package media;

import filter.Filter;
import filter.FilterFactory;

import java.io.File;

public class MediaProcessor {

    public void processMedia(File videoFile, String action) throws Exception {
        String audioPath = "C:\\Users\\saadn\\Documents\\audio.wav";
        String modifiedAudioPath = "C:\\Users\\saadn\\Documents\\modified_audio.wav";
        String outputVideoPath = "C:\\Users\\saadn\\Documents\\output_video.mp4";

        if (!AudioExtractor.extractAudio(videoFile.getAbsolutePath(), audioPath)) {
            throw new Exception("Failed to extract audio from video.");
        }

        String credentialsPath = "C:\\Users\\saadn\\Downloads\\ibm-credentials.env";
        AudioTranscriber.configureIBMCredentials(credentialsPath);
        AudioTranscriber.transcribeAudio(audioPath);

        Filter filter = FilterFactory.getFilter(action);
        filter.apply(audioPath, modifiedAudioPath); // Apply the selected filter

        System.out.println("Processing completed. Output video: " + outputVideoPath);
    }
}