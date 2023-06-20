package vocabulary.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

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
    private String username = "andrey759";
    private String text;
    @Enumerated(value = EnumType.STRING)
    private WordStatus status;
}
