package vocabulary.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vocabulary.service.DiffService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiffScheduler {
    private final DiffService diffService;

    @Scheduled(cron = "0 0 0 2 * *")
    public void setZeroIndex() {
        log.info("Set email list index for diffchecker.com to 0");
        diffService.INDEX.set(0);
    }
}
