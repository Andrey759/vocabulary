package vocabulary.entity.enums;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Function;

@RequiredArgsConstructor
public enum CardStatus {
    LEARNING(dt -> dt.plusHours(10L)),
    //THREE_DAYS(dt -> dt.plusDays(3L).minusHours(6L)),
    FIVE_DAYS(dt -> dt.plusDays(5L).minusHours(6L)),
    //ONE_WEEK(dt -> dt.plusWeeks(1L).minusHours(6L)),
    //TEN_DAYS(dt -> dt.plusDays(10L).minusHours(6L)),
    TWO_WEEKS(dt -> dt.plusWeeks(2L).minusHours(6L)),
    ONE_MONTH(dt -> dt.plusMonths(1L).minusHours(6L)),
    TWO_MONTHS(dt -> dt.plusMonths(2L).minusHours(6L)),
    FOUR_MONTHS(dt -> dt.plusMonths(4L).minusHours(6L)),
    ONE_YEAR(dt -> dt.plusYears(1L).minusHours(6L)),
    INFINITY(dt -> LocalDateTime.MAX),
    ;

    private final Function<LocalDateTime, LocalDateTime> func;

    public CardStatus nextStatus() {
        CardStatus[] values = values();
        int index = Arrays.asList(values).indexOf(this);
        return index == values.length - 1
                ? values[index]
                : values[index + 1];
    }
    public LocalDateTime readyAt(LocalDateTime updatedAt) {
        return func.apply(updatedAt);
    }
}
