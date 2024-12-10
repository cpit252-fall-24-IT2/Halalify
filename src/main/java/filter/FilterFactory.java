package filter;

//This is the Factory method design pattern, we made this to give two options to the user.
public class FilterFactory {
    public static Filter getFilter(String filterType) {
        switch (filterType) {
            case "MUTE": return new MuteFilter();
            case "BLEEP": return new BleepFilter();
            default: throw new IllegalArgumentException("Unknown filter type.");
        }
    }
}