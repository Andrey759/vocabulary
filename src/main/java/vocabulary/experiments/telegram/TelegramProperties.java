package vocabulary.experiments.telegram;

import io.github.sashirestela.openai.domain.audio.SpeechRequest;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("telegram")
@Data
@ToString
public class TelegramProperties {
    private boolean enabled;
    private Bot vocabularyBot;
    private List<Bot> bots;

    @Data
    @ToString
    public static class Bot {
        private String username;
        private String token;
        private String startMessage;
        private String userLanguage;
        private String maxTokens;
        private SpeechRequest.Voice voice;
        private Double speed;
        private Boolean logging;
    }
}
