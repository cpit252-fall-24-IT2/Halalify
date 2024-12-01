package filter;

import javax.sound.sampled.*;
import java.io.*;
import java.util.List;

public class BeepFilter implements Filter {

    private final String beepFilePath = "C:\\Users\\saadn\\Downloads\\y2mate.com - BEEP Beep sound effect.wav";

    @Override
    public void apply(String inputAudioFile, String outputAudioFile, List<Double> profanityTimestamps) throws Exception {
        // Load input audio
        File inputFile = new File(inputAudioFile);
        AudioInputStream inputAudioStream = AudioSystem.getAudioInputStream(inputFile);

        // Load and normalize beep audio
        File beepFile = new File(beepFilePath);
        AudioInputStream beepStream = AudioSystem.getAudioInputStream(beepFile);
        AudioInputStream normalizedBeepStream = convertAudioFormat(beepStream, inputAudioStream.getFormat());

        // Convert streams to byte arrays
        byte[] inputAudioBytes = inputAudioStream.readAllBytes();
        byte[] beepAudioBytes = normalizedBeepStream.readAllBytes();

        // Overlay beep sound at profanity timestamps
        byte[] processedAudioBytes = addBeep(inputAudioBytes, beepAudioBytes, profanityTimestamps, inputAudioStream.getFormat());

        // Write processed audio to output file
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(processedAudioBytes);
        AudioInputStream processedAudioStream = new AudioInputStream(byteArrayInputStream, inputAudioStream.getFormat(), processedAudioBytes.length / inputAudioStream.getFormat().getFrameSize());
        File outputFile = new File(outputAudioFile);
        AudioSystem.write(processedAudioStream, AudioFileFormat.Type.WAVE, outputFile);

        System.out.println("BeepFilter applied successfully. Output file: " + outputAudioFile);
    }

    private byte[] addBeep(byte[] inputAudio, byte[] beepAudio, List<Double> timestamps, AudioFormat format) {
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();

        // Convert timestamps to byte positions
        for (double timestamp : timestamps) {
            int startByte = (int) (timestamp * frameRate) * frameSize;
            int endByte = startByte + beepAudio.length;

            // Ensure we don't exceed the input audio length
            if (endByte > inputAudio.length) {
                endByte = inputAudio.length;
            }

            // Overlay beep audio
            for (int i = startByte, j = 0; i < endByte && j < beepAudio.length; i++, j++) {
                inputAudio[i] = beepAudio[j];
            }
        }

        return inputAudio;
    }

    private AudioInputStream convertAudioFormat(AudioInputStream sourceStream, AudioFormat targetFormat) throws Exception {
        AudioFormat sourceFormat = sourceStream.getFormat();

        if (!sourceFormat.matches(targetFormat)) {
            System.out.println("Converting beep audio to match input audio format...");
            AudioFormat intermediateFormat = new AudioFormat(
                    targetFormat.getSampleRate(),
                    targetFormat.getSampleSizeInBits(),
                    targetFormat.getChannels(),
                    true,
                    targetFormat.isBigEndian()
            );

            return AudioSystem.getAudioInputStream(intermediateFormat, sourceStream);
        }
        return sourceStream;
    }
}