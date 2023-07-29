package vocabulary.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import vocabulary.entity.enums.CardStatus;

@Getter
@RequiredArgsConstructor
@ToString
public class CardStatusDto {
    private final String word;
    private final CardStatus status;
}
