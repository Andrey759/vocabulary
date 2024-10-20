package vocabulary.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import vocabulary.experiments.telegram.TelegramBot;
import vocabulary.experiments.telegram.TelegramMessageService;
import vocabulary.experiments.telegram.TelegramProperties;

import java.util.List;

@Configuration
@ConditionalOnExpression("${telegram.enabled:false}")
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramBotsConfig {

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsApi() {
        return new TelegramBotsLongPollingApplication();
    }

    @Bean
    public List<TelegramBot> telegramBots(
            TelegramBotsLongPollingApplication telegramBotsApplication,
            TelegramMessageService messageService,
            TelegramProperties telegramProperties) {

        return telegramProperties.getBots()
                .stream()
                .map(botProps -> {
                    var bot = new TelegramBot(messageService, botProps);
                    try {
                        telegramBotsApplication.registerBot(botProps.getToken(), bot);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    return bot;
                })
                .toList();
    }
}
