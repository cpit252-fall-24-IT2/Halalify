public class FilterFactory {
    public static Filter getFilter(String filterType) {
        switch (filterType) {
            case "MUTE": return new MuteFilter();
            case "BEEP": return new BeepFilter();
            default: throw new IllegalArgumentException("Unknown filter type.");
        }
    }
}