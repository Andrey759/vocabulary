package vocabulary.config;

import io.github.sashirestela.openai.domain.audio.SpeechRequest;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("telegram.vocabulary-bot")
@Data
@ToString
public class TelegramVocabularyBotProperties {
    private boolean enabled;
    private String username;
    private String token;
    //private String startMessage;
    //private String userLanguage;
    private String maxTokens;
    private SpeechRequest.Voice voice;
    private Double speed;
    private Boolean logging = false;
}
