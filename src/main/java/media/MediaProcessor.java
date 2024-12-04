package media;

//This is the Facade design pattern, we separated this implementation from the main class
//to make it cleaner and more user-friendly.
import filter.Filter;
import filter.FilterFactory;
import java.io.File;
import java.util.List;
import static media.VideoAudioMerger.merge;

public class MediaProcessor {

    private String ffmpegPath;
    private String outputVideoPath;


    public void setFfmpegPath(String ffmpegPath) throws Exception {
        File file = new File(ffmpegPath);
        if (!file.exists() || !file.isFile() || !file.getName().equalsIgnoreCase("ffmpeg.exe")) {
            throw new Exception("Invalid FFmpeg path. Please select the correct FFmpeg executable.");
        }
        this.ffmpegPath = ffmpegPath;
    }

    public void setVideoOutputPath(String outputVideoPath) throws Exception {
        this.outputVideoPath = outputVideoPath;
    }


    public void processMedia(File videoFile, String action) throws Exception {
        if (ffmpegPath == null || ffmpegPath.isEmpty()) {
            throw new Exception("FFmpeg path is not set.");
        }

        //Bad smell
        String audioPath = "C:\\Users\\saad-\\Documents\\audio.wav";
        String modifiedAudioPath = "C:\\Users\\saad-\\Documents\\modified_audio.wav";

        System.out.println("Extracting audio from video...");
        if (!AudioExtractor.extractAudio(videoFile.getAbsolutePath(), audioPath, ffmpegPath)) {
            throw new Exception("Failed to extract audio from video.");
        }

        System.out.println("Configuring IBM Watson credentials...");
        String credentialsPath = "C:\\Users\\saad-\\Downloads\\ibm-credentials.env";
        AudioTranscriber.configureIBMCredentials(credentialsPath);

        System.out.println("Transcribing audio...");
        List<Double> badWordTimestamps = AudioTranscriber.transcribeAudio(audioPath);

        if (badWordTimestamps.isEmpty()) {
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