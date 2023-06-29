package vocabulary.entity.enums;

import java.util.Arrays;

public enum WordStatus {
    NOT_STARTED,
    LEARNING,
    THREE_DAYS,
    ONE_WEEK,
    TWO_WEEKS,
    ONE_MONTH,
    TWO_MONTHS,
    FOUR_MONTHS,
    ONE_YEAR,
    INFINITY,
    ;

    public WordStatus next() {
        int index = Arrays.asList(values()).indexOf(this);
        return values()[index + 1];
    }
}
