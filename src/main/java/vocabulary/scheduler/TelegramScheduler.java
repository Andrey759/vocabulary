package vocabulary.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vocabulary.service.TelegramVocabularyBot;

@ConditionalOnExpression("${telegram.enabled:true}")
@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramScheduler {
    private final TelegramVocabularyBot vocabularyBot;

    @PostConstruct
    //@Scheduled(cron = "*/2 * * * * *")
    public void sendFirstCardToAll() {
        //vocabularyBot.send("andrey759", 131304548L);
    }
}
