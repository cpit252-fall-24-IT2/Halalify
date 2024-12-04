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

        // Convert the input audio stream to a byte array
        byte[] inputAudioBytes = inputAudioStream.readAllBytes();
        AudioFormat format = inputAudioStream.getFormat();

        // Apply muting to the specified sections
        byte[] processedAudioBytes = muteSections(inputAudioBytes, badWordTimestamps, format);

        // Write the processed audio to the output file
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(processedAudioBytes);
        AudioInputStream processedAudioStream = new AudioInputStream(byteArrayInputStream, format, processedAudioBytes.length / format.getFrameSize());
        File outputFile = new File(outputAudioFile);
        AudioSystem.write(processedAudioStream, AudioFileFormat.Type.WAVE, outputFile);

        System.out.println("MuteFilter applied successfully. Output file: " + outputAudioFile);
    }

    /**
     * Mutes specified sections in the audio by replacing bytes with zero.
     *
     * @param inputAudioBytes Byte array of the input audio
     * @param timestamps List of timestamps (in seconds) where muting should occur
     * @param format Audio format of the input audio
     * @return Byte array of the processed audio with muted sections
     */
    private byte[] muteSections(byte[] inputAudioBytes, List<Double> timestamps, AudioFormat format) {
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();

        // Process each timestamp to calculate the byte range for muting
        for (double timestamp : timestamps) {
            int startByte = (int) (timestamp * frameRate) * frameSize;
            int muteDurationFrames = (int) frameRate; // Assuming muting for 1 second
            int endByte = startByte + muteDurationFrames * frameSize;

            // Ensure we don't go out of bounds
            if (startByte >= inputAudioBytes.length) {
                System.out.println("Timestamp " + timestamp + " is beyond the audio length. Skipping.");
                continue;
            }
            if (endByte > inputAudioBytes.length) {
                endByte = inputAudioBytes.length;
            }

            // Replace bytes with zero for the mute range
            for (int i = startByte; i < endByte; i++) {
                inputAudioBytes[i] = 0;
            }
        }

        return inputAudioBytes;
    }
}