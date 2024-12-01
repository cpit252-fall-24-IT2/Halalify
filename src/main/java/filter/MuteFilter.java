package filter;

import java.io.IOException;

public class MuteFilter implements Filter {
    @Override
    public void apply(String inputAudioPath, String outputAudioPath) throws IOException {
        // TODO: Implement logic to mute bad words in audio
        System.out.println("Mute filter applied to " + inputAudioPath);
        // Save the modified audio as outputAudioPath
    }
}
