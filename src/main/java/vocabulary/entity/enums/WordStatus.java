package vocabulary.entity.enums;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Function;

@RequiredArgsConstructor
public enum WordStatus {
    LEARNING(dt -> dt.plusHours(6L)),
    THREE_DAYS(dt -> dt.plusDays(3L)),
    ONE_WEEK(dt -> dt.plusWeeks(1L)),
    TWO_WEEKS(dt -> dt.plusWeeks(2L)),
    ONE_MONTH(dt -> dt.plusMonths(1L)),
    TWO_MONTHS(dt -> dt.plusMonths(2L)),
    FOUR_MONTHS(dt -> dt.plusMonths(4L)),
    ONE_YEAR(dt -> dt.plusYears(1L)),
    INFINITY(dt -> LocalDateTime.MAX),
    ;

    private final Function<LocalDateTime, LocalDateTime> func;

    public WordStatus nextStatus() {
        int index = Arrays.asList(values()).indexOf(this);
        return values()[index + 1];
    }
    public LocalDateTime readyAt() {
        return func.apply(LocalDateTime.now());
    }
}
