package vocabulary.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vocabulary.service.WordService;

@Component
@RequiredArgsConstructor
public class WordScheduler {
    private final WordService wordService;

    @PostConstruct
    @Scheduled(cron = "0 0 0 * * *")
    public void updateWordCash() {
        wordService.updateCardCash();
    }
}
