package wardsAmelia;

import java.util.Map;

public interface Ward {
    String getWardName();
    double getDailyRate();
    Map<Integer, Bed> getBeds();
}
