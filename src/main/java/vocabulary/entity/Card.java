package vocabulary.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import vocabulary.entity.enums.CardStatus;

import java.time.LocalDateTime;
import java.util.Random;

import static jakarta.persistence.EnumType.STRING;
import static java.time.temporal.ChronoField.MILLI_OF_DAY;
import static vocabulary.entity.enums.CardStatus.LEARNING;

@Entity(name = "cards")
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
    private String response;
    private String sentence;
    private String sentenceHtml;
    private String explanationHtml;
    private String translationHtml;
    @Enumerated(value = STRING)
    private CardStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime readyAt;
    private Integer repeatOrder;

    public static Card create(String username, String word) {
        LocalDateTime now = LocalDateTime.now();
        return Card.builder()
                .id(LocalDateTime.now().getLong(MILLI_OF_DAY))
                .username(username)
                .word(word)
                .status(LEARNING)
                .createdAt(now)
                .updatedAt(now)
                .readyAt(now)
                .repeatOrder(new Random().nextInt(1000))
                .build();
    }

    public void randomOrder() {
        this.repeatOrder = new Random().nextInt(1000);
    }
}
