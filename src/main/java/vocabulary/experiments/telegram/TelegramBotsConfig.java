package vocabulary.experiments.telegram;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.List;

@Configuration
@ConditionalOnExpression("${telegram.enabled:false}")
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramBotsConfig {

    @Bean
    public List<TelegramBot> telegramBots(
            TelegramBotsApi telegramBotsApi,
            TelegramMessageService telegramMessageService,
            TelegramProperties telegramProperties) {

        return telegramProperties.getBots()
                .stream()
                .map(bot -> new TelegramBot(telegramBotsApi, telegramMessageService, bot))
                .toList();
    }
}
