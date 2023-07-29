package vocabulary.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class CardViewDto {
    private final String word;
    private final String sentence;
    private final String sentenceHtml;
    private final String explanationHtml;
    private final String translationHtml;
}
