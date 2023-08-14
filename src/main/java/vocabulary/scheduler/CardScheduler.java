package vocabulary.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vocabulary.service.CardService;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardScheduler {
    private final CardService cardService;

    @Scheduled(cron = "*/2 * * * * *")
    public void updateAllEmptyFromChatGpt() {
        cardService.updateAllEmptyFromChatGpt();
    }
}
