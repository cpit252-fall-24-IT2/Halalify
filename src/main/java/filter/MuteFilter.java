package filter;

import javax.sound.sampled.*;
import java.io.*;
import java.util.List;

public class MuteFilter implements Filter {

    @Override
    public void apply(String inputAudioFile, String outputAudioFile, List<Double> badWordTimestamps) throws Exception {
        // Load the input audio file
        File inputFile = new File(inputAudioFile);
        AudioInputStream inputAudioStream = AudioSystem.getAudioInputStream(inputFile);
        AudioFormat format = inputAudioStream.getFormat();

        // Create output file and AudioOutputStream
        File outputFile = new File(outputAudioFile);
        AudioInputStream processedAudioStream = processAudio(inputAudioStream, badWordTimestamps, format);

        // Write the processed audio to the output file
        AudioSystem.write(processedAudioStream, AudioFileFormat.Type.WAVE, outputFile);

        System.out.println("MuteFilter applied successfully. Output file: " + outputAudioFile);
    }

    /**
     * Processes the audio and mutes the specified sections without loading the entire file into memory.
     *
     * @param inputAudioStream Input audio stream
     * @param timestamps List of timestamps (in seconds) where muting should occur
     * @param format Audio format of the input audio
     * @return An AudioInputStream containing the processed audio
     * @throws IOException If an I/O error occurs
     */
    private AudioInputStream processAudio(AudioInputStream inputAudioStream, List<Double> timestamps, AudioFormat format) throws IOException {
        // Sort timestamps to process them in order
        timestamps.sort(Double::compareTo);

        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();
        long totalFrames = inputAudioStream.getFrameLength();

        // Create a new AudioInputStream for the output
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AudioInputStream outputAudioStream = new AudioInputStream(
                new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
                format,
                totalFrames
        );

        byte[] buffer = new byte[frameSize];
        long frameIndex = 0;
        long muteStartFrame = -1;
        long muteEndFrame = -1;

        // Iterate through the input audio and process each frame
        for (double timestamp : timestamps) {
            muteStartFrame = (long) (timestamp * frameRate);
            muteEndFrame = muteStartFrame + (long) frameRate; // Assuming 1-second mute duration

            // Process frames before mute region
            while (frameIndex < muteStartFrame) {
                int bytesRead = inputAudioStream.read(buffer);
                if (bytesRead == -1) break;
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                frameIndex++;
            }

            // Write mute region (zeros)
            while (frameIndex >= muteStartFrame && frameIndex < muteEndFrame && frameIndex < totalFrames) {
                byteArrayOutputStream.write(new byte[frameSize]); // Write zero bytes
                inputAudioStream.skip(frameSize); // Skip frames in the input
                frameIndex++;
            }
        }

        // Process remaining frames after the last mute region
        int bytesRead;
        while ((bytesRead = inputAudioStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
            frameIndex++;
        }

        // Create the final output audio stream
        byte[] processedAudioBytes = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(processedAudioBytes);
        return new AudioInputStream(byteArrayInputStream, format, processedAudioBytes.length / format.getFrameSize());
    }
}