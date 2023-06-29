package vocabulary.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import vocabulary.entity.enums.WordStatus;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoField.MILLI_OF_DAY;
import static vocabulary.entity.enums.WordStatus.LEARNING;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Word {
    @Id
    private Long id;
    private String username;
    private String text;
    @Enumerated(value = EnumType.STRING)
    private WordStatus status;
    private LocalDateTime updatedAt;
    private LocalDateTime readyAt;

    public static Word create(String word) {
        return Word.builder()
                .id(LocalDateTime.now().getLong(MILLI_OF_DAY))
                .username("andrey759")
                .text(word)
                .status(LEARNING)
                .updatedAt(LocalDateTime.now())
                .readyAt(LocalDateTime.now())
                .build();
    }
}
