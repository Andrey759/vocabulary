package vocabulary.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import vocabulary.entity.Card;
import vocabulary.entity.enums.CardStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@ToString
public class DictDto {

    private final String word;
    private final CardStatus status;
    private final boolean ready;

    public static DictDto from(Card card) {
        return new DictDto(
                card.getWord(),
                card.getStatus(),
                card.getReadyAt().isBefore(LocalDateTime.now())
                        && card.getSentence() != null
        );
    }
}
