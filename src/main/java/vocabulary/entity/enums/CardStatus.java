package vocabulary.entity.enums;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Function;
import java.util.random.RandomGenerator;

@RequiredArgsConstructor
public enum CardStatus {
    LEARNING(dt -> dt.plusDays(1L).minusHours(6L).plusMinutes(RandomGenerator.getDefault().nextInt(60))),
    THREE_DAYS(dt -> dt.plusDays(3L).minusHours(6L).plusMinutes(RandomGenerator.getDefault().nextInt(60))),
    ONE_WEEK(dt -> dt.plusWeeks(1L).minusHours(6L).plusMinutes(RandomGenerator.getDefault().nextInt(60))),
    TWO_WEEKS(dt -> dt.plusWeeks(2L).minusHours(6L).plusMinutes(RandomGenerator.getDefault().nextInt(60))),
    ONE_MONTH(dt -> dt.plusMonths(1L).minusHours(6L).plusMinutes(RandomGenerator.getDefault().nextInt(60))),
    TWO_MONTHS(dt -> dt.plusMonths(2L).minusHours(6L).plusMinutes(RandomGenerator.getDefault().nextInt(60))),
    FOUR_MONTHS(dt -> dt.plusMonths(4L).minusHours(6L).plusMinutes(RandomGenerator.getDefault().nextInt(60))),
    ONE_YEAR(dt -> dt.plusYears(1L).minusHours(6L).plusMinutes(RandomGenerator.getDefault().nextInt(60))),
    INFINITY(dt -> LocalDateTime.MAX),
    ;

    private final Function<LocalDateTime, LocalDateTime> func;

    public CardStatus nextStatus() {
        int index = Arrays.asList(values()).indexOf(this);
        return values()[index + 1];
    }
    public LocalDateTime readyAt(LocalDateTime updatedAt) {
        return func.apply(updatedAt);
    }
}
