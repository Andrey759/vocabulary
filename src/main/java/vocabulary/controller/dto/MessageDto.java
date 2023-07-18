package vocabulary.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import vocabulary.controller.enums.MessageOwner;

import static vocabulary.controller.enums.MessageOwner.*;

@Getter
@RequiredArgsConstructor
@ToString
public class MessageDto {
    public static final MessageDto EMPTY = new MessageDto(BOT, "", "", "");

    private final MessageOwner owner;
    private final String corrected;
    private final String correctedHtml;
    private final String perfect;
}
