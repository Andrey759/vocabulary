package vocabulary.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class CardDto {
    private final String word;
    private final String text;
    private final String explanation;
    private final String translation;
}
