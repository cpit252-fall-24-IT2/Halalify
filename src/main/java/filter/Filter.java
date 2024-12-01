package filter;

public interface Filter {
    void apply(String inputAudioPath, String outputAudioPath) throws Exception;
}