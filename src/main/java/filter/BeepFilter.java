package filter;

import java.io.IOException;

public class BeepFilter implements Filter {
    @Override
    public void apply(String inputAudioPath, String outputAudioPath) throws IOException {
        // TODO: Implement logic to add beep sounds in audio
        System.out.println("Beep filter applied to " + inputAudioPath);
        // Save the modified audio as outputAudioPath
    }
}
