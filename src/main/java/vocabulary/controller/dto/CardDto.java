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
    public static CardDto EMPTY = new CardDto(
            "", "", "", "", "", "", CardStatus.THREE_DAYS);

    private final String word;
    private final String response;
    private final String sentence;
    private final String sentenceHtml;
    private final String explanationHtml;
    private final String translationHtml;
    private final CardStatus nextStatus;

    public static CardDto from(Card card) {
        return new CardDto(
                card.getWord(),
                card.getResponse(),
                card.getSentence(),
                card.getSentenceHtml(),
                card.getExplanationHtml(),
                card.getTranslationHtml(),
                card.getStatus().nextStatus()
        );
    }
}
