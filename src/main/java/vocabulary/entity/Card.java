package vocabulary.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import vocabulary.entity.enums.CardStatus;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static java.time.temporal.ChronoField.MILLI_OF_DAY;
import static vocabulary.entity.enums.CardStatus.LEARNING;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    private Long id;
    private String username;
    private String word;
    private String sentence;
    private String sentenceHtml;
    private String explanationHtml;
    private String translationHtml;
    @Enumerated(value = STRING)
    private CardStatus status;
    private LocalDateTime readyAt;

    public static Card create(String username, String word) {
        return Card.builder()
                .id(LocalDateTime.now().getLong(MILLI_OF_DAY))
                .username(username)
                .word(word)
                .status(LEARNING)
                .readyAt(LocalDateTime.now())
                .build();
    }
}
