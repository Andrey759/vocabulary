package vocabulary.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import vocabulary.entity.User;
import vocabulary.entity.enums.Voice;

import java.math.BigDecimal;
import java.util.Optional;

import static vocabulary.entity.enums.Voice.UK_ENGLISH_FEMALE;
import static vocabulary.entity.enums.Voice.UK_ENGLISH_MALE;

@Getter
@RequiredArgsConstructor
@ToString
public class UserDto {
    private final String username;
    private final boolean voiceEnabled;
    private final Voice voiceCard;
    private final Voice voiceChatLeft;
    private final Voice voiceChatRight;
    private final BigDecimal voiceRate;
    private final BigDecimal voiceVolume;

    public static UserDto emptyForUser(String username) {
        return new UserDto(
                username,
                true,
                UK_ENGLISH_MALE,
                UK_ENGLISH_FEMALE,
                UK_ENGLISH_MALE,
                new BigDecimal("0.8"),
                new BigDecimal("1.0")
        );
    }
    public static UserDto from(User user) {
        return new UserDto(
                user.getUsername(),
                Optional.ofNullable(user.getVoiceEnabled()).orElse(true),
                Optional.ofNullable(user.getVoiceCard()).orElse(UK_ENGLISH_MALE),
                Optional.ofNullable(user.getVoiceChatLeft()).orElse(UK_ENGLISH_FEMALE),
                Optional.ofNullable(user.getVoiceChatRight()).orElse(UK_ENGLISH_MALE),
                Optional.ofNullable(user.getVoiceRate()).orElse(new BigDecimal("0.8")),
                Optional.ofNullable(user.getVoiceVolume()).orElse(new BigDecimal("1.0"))
        );
    }
}
