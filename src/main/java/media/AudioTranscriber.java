package media;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AudioTranscriber {

    private static SpeechToText speechToText;

    public static void configureIBMCredentials(String credentialsFilePath) throws IOException {
        // Load API key and URL from .env file
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(credentialsFilePath)) {
            properties.load(fis);
        }

        String apiKey = properties.getProperty("SPEECH_TO_TEXT_APIKEY");
        String serviceUrl = properties.getProperty("SPEECH_TO_TEXT_URL");

        if (apiKey == null || serviceUrl == null) {
            throw new IllegalStateException("IBM API key or service URL is missing in the credentials file.");
        }

        // Configure Speech-to-Text client with the IamAuthenticator
        IamAuthenticator authenticator = new IamAuthenticator(apiKey);
        speechToText = new SpeechToText(authenticator);
        speechToText.setServiceUrl(serviceUrl);

        System.out.println("IBM Watson Speech-to-Text credentials configured successfully from credentials file.");
    }

    public static List<Double> transcribeAudio(String audioFilePath) throws IOException {
        if (speechToText == null) {
            throw new IllegalStateException("IBM Watson Speech-to-Text not configured. Call configureIBMCredentials first.");
        }

        File audioFile = new File(audioFilePath);
        if (!audioFile.exists()) {
            throw new IllegalArgumentException("Audio file does not exist: " + audioFilePath);
        }

        // Build recognition options with timestamps enabled
        RecognizeOptions options = new RecognizeOptions.Builder()
                .audio(audioFile)
                .contentType("audio/wav") // Ensure your file is in the correct format
                .model("en-US_BroadbandModel")
                .timestamps(true) // Enable timestamps for words
                .build();

        // Perform transcription
        SpeechRecognitionResults results = speechToText.recognize(options).execute().getResult();

        String outputPath = "C:\\Users\\saadn\\Documents\\transcription.txt"; // Replace with your desired path

        List<Double> badWordTimestamps = new ArrayList<>();

        // Create the output text file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            for (SpeechRecognitionResult result : results.getResults()) {
                for (SpeechRecognitionAlternative alternative : result.getAlternatives()) {
                    String transcription = alternative.getTranscript();
                    writer.write(transcription);
                    writer.newLine();
                    // Check each word for profanity and collect timestamps
                    badWordTimestamps.addAll(getProfanityTimestamps(alternative.getTimestamps()));
                }
            }
        }

        System.out.println("Transcription saved to transcription.txt");
        return badWordTimestamps;
    }

    private static List<Double> getProfanityTimestamps(List<SpeechTimestamp> timestamps) {
        List<Double> badWordTimestamps = new ArrayList<>();
        for (SpeechTimestamp timestamp : timestamps) {
            String word = timestamp.getWord();
            double startTimeInSeconds = timestamp.getStartTime();

            // Check for profanity
            if (isProfane(word)) {
                System.out.printf("Profanity detected: '%s' at %.2f seconds%n", word, startTimeInSeconds);
                badWordTimestamps.add(startTimeInSeconds);
            }
        }
        return badWordTimestamps;
    }

    private static boolean isProfane(String word) {
        List<String> profanities = List.of("subscribe", "badword2"); // Add actual offensive words here
        return profanities.contains(word.toLowerCase());
    }
}