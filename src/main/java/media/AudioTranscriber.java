package media;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.*;
import java.io.*;
import java.util.*;

public class AudioTranscriber {

    private static SpeechToText speechToText;
    private static final Set<String> PROFANITIES = Set.of("subscribe", "wrong"); // Add more words as needed

    public static void configureIBMCredentials(String credentialsFilePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(credentialsFilePath)) {
            properties.load(fis);
        }

        String apiKey = properties.getProperty("SPEECH_TO_TEXT_APIKEY");
        String serviceUrl = properties.getProperty("SPEECH_TO_TEXT_URL");

        if (apiKey == null || serviceUrl == null) {
            throw new IllegalStateException("IBM API key or service URL is missing in the credentials file.");
        }

        IamAuthenticator authenticator = new IamAuthenticator(apiKey);
        speechToText = new SpeechToText(authenticator);
        speechToText.setServiceUrl(serviceUrl);

        System.out.println("IBM Watson Speech-to-Text credentials configured successfully.");
    }

    public static List<Double> transcribeAudio(String audioFilePath) throws IOException {
        validateSetup();
        File audioFile = validateAudioFile(audioFilePath);

        // Perform transcription
        SpeechRecognitionResults results = transcribe(audioFile);

        // Detect bad words
        return detectProfanity(results);
    }

    private static void validateSetup() {
        if (speechToText == null) {
            throw new IllegalStateException("IBM Watson Speech-to-Text not configured. Call configureIBMCredentials first.");
        }
    }

    private static File validateAudioFile(String audioFilePath) {
        File audioFile = new File(audioFilePath);
        if (!audioFile.exists()) {
            throw new IllegalArgumentException("Audio file does not exist: " + audioFilePath);
        }
        return audioFile;
    }

    private static SpeechRecognitionResults transcribe(File audioFile) throws FileNotFoundException {
        RecognizeOptions options = new RecognizeOptions.Builder()
                .audio(audioFile)
                .contentType("audio/wav") // Ensure your file is in the correct format
                .model("en-US_BroadbandModel")
                .timestamps(true) // Enable timestamps for words
                .build();

        return speechToText.recognize(options).execute().getResult();
    }

    private static List<Double> detectProfanity(SpeechRecognitionResults results) {
        List<Double> badWordTimestamps = new ArrayList<>();

        for (SpeechRecognitionResult result : results.getResults()) {
            for (SpeechRecognitionAlternative alternative : result.getAlternatives()) {
                badWordTimestamps.addAll(getProfanityTimestamps(alternative.getTimestamps()));
            }
        }
        return badWordTimestamps;
    }

    private static List<Double> getProfanityTimestamps(List<SpeechTimestamp> timestamps) {
        List<Double> badWordTimestamps = new ArrayList<>();
        for (SpeechTimestamp timestamp : timestamps) {
            String word = timestamp.getWord();
            if (isProfane(word)) {
                System.out.printf("Profanity detected: '%s' at %.2f seconds%n", word, timestamp.getStartTime());
                badWordTimestamps.add(timestamp.getStartTime());
            }
        }
        return badWordTimestamps;
    }

    private static boolean isProfane(String word) {
        return PROFANITIES.contains(word.toLowerCase());
    }
}