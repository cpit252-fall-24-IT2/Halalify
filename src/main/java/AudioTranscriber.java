import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionAlternative;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;

import java.io.*;
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

    public static void transcribeAudio(String audioFilePath) throws IOException {
        if (speechToText == null) {
            throw new IllegalStateException("IBM Watson Speech-to-Text not configured. Call configureIBMCredentials first.");
        }

        File audioFile = new File(audioFilePath);
        if (!audioFile.exists()) {
            throw new IllegalArgumentException("Audio file does not exist: " + audioFilePath);
        }

        // Build recognition options
        RecognizeOptions options = new RecognizeOptions.Builder()
                .audio(audioFile)
                .contentType("audio/wav") // Ensure your file is in the correct format
                .model("en-US_BroadbandModel")
                .timestamps(true)
                .build();

        // Perform transcription
        SpeechRecognitionResults results = speechToText.recognize(options).execute().getResult();

        // Create or overwrite the output text file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transcription.txt"))) {
            for (SpeechRecognitionResult result : results.getResults()) {
                for (SpeechRecognitionAlternative alternative : result.getAlternatives()) {
                    String transcription = alternative.getTranscript();
                    writer.write(transcription);
                    writer.newLine(); // Add a new line for readability
                }
            }
        }

        System.out.println("Transcription saved to transcription.txt");
    }

    private static boolean isProfane(String word) {
        List<String> profanities = List.of("badword1", "badword2"); // Add actual offensive words here
        return profanities.contains(word.toLowerCase());
    }
}
