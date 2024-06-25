package vocabulary.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.core.context.SecurityContextHolder;
import vocabulary.entity.enums.Voice;

import java.math.BigDecimal;

import static jakarta.persistence.EnumType.STRING;

@Entity(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String username;
    private String password;
    private Long telegramChatId;
    private Boolean enabled;

    private Boolean voiceEnabled;
    @Enumerated(value = STRING)
    private Voice voiceCard;
    @Enumerated(value = STRING)
    private Voice voiceChatLeft;
    @Enumerated(value = STRING)
    private Voice voiceChatRight;
    private BigDecimal voiceRate;
    private BigDecimal voiceVolume;

    public static String getCurrent() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
