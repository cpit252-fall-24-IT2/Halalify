package filter;

import javax.sound.sampled.*;
import java.io.*;
import java.util.List;

public class BleepFilter implements Filter {

    @Override
    public void apply(String inputAudioFile, String outputAudioFile, List<Double> badWordTimestamps) {
        String bleepFilePath = "src/main/Bleep.wav";

        try (AudioInputStream inputAudioStream = loadAudioStream(inputAudioFile);
             AudioInputStream beepStream = loadAudioStream(bleepFilePath)) {

            // Normalize beep audio
            AudioInputStream normalizedBeepStream = convertAudioFormat(beepStream, inputAudioStream.getFormat());

            // Process the audio
            byte[] processedAudioBytes = processAudio(inputAudioStream, normalizedBeepStream, badWordTimestamps);

            // Write output
            writeOutputAudio(outputAudioFile, inputAudioStream.getFormat(), processedAudioBytes);

            System.out.println("BleepFilter applied successfully. Output file: " + outputAudioFile);

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Error: Unsupported audio file format - " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error: IO issue encountered - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private AudioInputStream loadAudioStream(String filePath) throws IOException, UnsupportedAudioFileException {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        try {
            return AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException e) {
            throw new UnsupportedAudioFileException("The file format is unsupported: " + filePath);
        }
    }

    private byte[] processAudio(AudioInputStream inputAudioStream, AudioInputStream normalizedBeepStream, List<Double> badWordTimestamps) throws IOException {
        try {
            byte[] inputAudioBytes = inputAudioStream.readAllBytes();
            byte[] beepAudioBytes = normalizedBeepStream.readAllBytes();

            return overlayBeep(inputAudioBytes, beepAudioBytes, badWordTimestamps, inputAudioStream.getFormat(), 0.6);
        } catch (IOException e) {
            throw new IOException("Error processing audio streams: " + e.getMessage(), e);
        }
    }

    private byte[] overlayBeep(byte[] inputAudio, byte[] beepAudio, List<Double> timestamps, AudioFormat format, double beepDurationSeconds) {
        try {
            int frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();

            // Calculate the number of bytes for the desired beep duration
            int beepBytesLength = (int) (beepDurationSeconds * frameRate) * frameSize;

            // Trim the beep audio to the desired length
            byte[] trimmedBeepAudio = new byte[Math.min(beepBytesLength, beepAudio.length)];
            System.arraycopy(beepAudio, 0, trimmedBeepAudio, 0, trimmedBeepAudio.length);

            for (double timestamp : timestamps) {
                int startByte = (int) (timestamp * frameRate) * frameSize;
                int endByte = Math.min(startByte + trimmedBeepAudio.length, inputAudio.length);

                for (int i = startByte, j = 0; i < endByte && j < trimmedBeepAudio.length; i++, j++) {
                    inputAudio[i] = trimmedBeepAudio[j];
                }
            }
            return inputAudio;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error overlaying beep audio: " + e.getMessage(), e);
        }
    }

    private AudioInputStream convertAudioFormat(AudioInputStream sourceStream, AudioFormat targetFormat) {
        AudioFormat sourceFormat = sourceStream.getFormat();
        if (!sourceFormat.matches(targetFormat)) {
            AudioFormat intermediateFormat = new AudioFormat(
                    targetFormat.getSampleRate(),
                    targetFormat.getSampleSizeInBits(),
                    targetFormat.getChannels(),
                    true,
                    targetFormat.isBigEndian()
            );
            try {
                return AudioSystem.getAudioInputStream(intermediateFormat, sourceStream);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to convert audio format: " + e.getMessage(), e);
            }
        }
        return sourceStream;
    }

    private void writeOutputAudio(String outputFilePath, AudioFormat format, byte[] audioBytes) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioBytes);
             AudioInputStream processedAudioStream = new AudioInputStream(byteArrayInputStream, format, audioBytes.length / format.getFrameSize())) {

            File outputFile = new File(outputFilePath);
            AudioSystem.write(processedAudioStream, AudioFileFormat.Type.WAVE, outputFile);
        } catch (IOException e) {
            throw new IOException("Failed to write output audio file: " + e.getMessage(), e);
        }
    }
}