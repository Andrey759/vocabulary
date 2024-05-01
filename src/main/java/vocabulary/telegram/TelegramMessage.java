package vocabulary.telegram;

import io.github.sashirestela.openai.domain.chat.Role;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;

@Entity(name = "telegram_messages")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    @Enumerated(value = STRING)
    private Role messageRole;
    private String text;
}
