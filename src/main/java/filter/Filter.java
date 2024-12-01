package filter;

import java.util.List;

public interface Filter {
    void apply(String inputAudioPath, String outputAudioPath, List<Double> badWordTimestamps) throws Exception;
}