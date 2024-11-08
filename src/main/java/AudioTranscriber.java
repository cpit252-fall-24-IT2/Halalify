import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


public class AudioTranscriber {

    public static void transcribeAudio(String audioFilePath) throws IOException {
        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .setEnableWordTimeOffsets(true)
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.readFrom(new FileInputStream(audioFilePath)))
                    .build();

            List<SpeechRecognitionResult> results = speechClient.recognize(config, audio).getResultsList();

            for (SpeechRecognitionResult result : results) {
                for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
                    System.out.printf("Transcription: %s%n", alternative.getTranscript());
                    for (WordInfo wordInfo : alternative.getWordsList()) {
                        String word = wordInfo.getWord();
                        System.out.printf("Word: %s, Start: %s, End: %s%n", word,
                                wordInfo.getStartTime().getSeconds(),
                                wordInfo.getEndTime().getSeconds());

                        if (isProfane(word)) {
                            System.out.printf("Profanity detected at: %s seconds%n", wordInfo.getStartTime().getSeconds());
                        }
                    }
                }
            }
        }
    }

    private static boolean isProfane(String word) {
        List<String> profanities = List.of("badword1", "badword2"); // Add actual offensive words here
        return profanities.contains(word.toLowerCase());
    }
}
