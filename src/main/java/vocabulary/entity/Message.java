package vocabulary.entity;

import jakarta.persistence.*;
import lombok.*;
import vocabulary.controller.enums.MessageOwner;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    private Long id;
    private String username;
    @Enumerated(value = STRING)
    private MessageOwner owner;
    private Integer mark;
    private String corrected;
    private String correctedHtml;
    private String perfect;
}
