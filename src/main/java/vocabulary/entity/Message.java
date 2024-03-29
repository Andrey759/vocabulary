package vocabulary.entity;

import jakarta.persistence.*;
import lombok.*;
import vocabulary.controller.enums.MessageOwner;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static vocabulary.controller.enums.MessageOwner.BOT;

@Entity(name = "messages")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private static final String HELLO_TEXT = "Hello! I'm ChatGPT. You can start messaging with me, " +
            "and I'll correct your message's grammar. Additionally, you can choose a voice " +
            "and adjust its volume in the settings section.";
    public static final Message HELLO = new Message(
            null,
            "",
            BOT,
            0,
            "",
            "",
            HELLO_TEXT,
            HELLO_TEXT,
            "",
            LocalDateTime.now()
    );

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String username;
    @Enumerated(value = STRING)
    private MessageOwner owner;
    private Integer mark;
    private String message;
    private String response;
    private String corrected;
    private String correctedHtml;
    private String perfect;
    private LocalDateTime createdAt;
}
