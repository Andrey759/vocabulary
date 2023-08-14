package vocabulary.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import vocabulary.entity.Card;
import vocabulary.entity.enums.CardStatus;

@Getter
@RequiredArgsConstructor
@ToString
public class CardDto {

    private final String word;
    private final String sentence;
    private final String sentenceHtml;
    private final String explanationHtml;
    private final String translationHtml;
    private final CardStatus nextStatus;
    private final Long finishedToday;
    private final Long totalElements;

    public static CardDto from(Card card, Long finishedToday, Long totalElements) {
        return new CardDto(
                card.getWord(),
                card.getSentence(),
                card.getSentenceHtml(),
                card.getExplanationHtml(),
                card.getTranslationHtml(),
                card.getStatus().nextStatus(),
                finishedToday,
                totalElements
        );
    }

    public static CardDto empty(Long finishedToday, Long totalElements) {
        return new CardDto(
                "",
                "",
                "No cards to repeat",
                "",
                "",
                CardStatus.THREE_DAYS,
                finishedToday,
                totalElements
        );
    }
}
