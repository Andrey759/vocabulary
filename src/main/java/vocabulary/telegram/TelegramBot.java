package vocabulary.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;
import java.util.Optional;

import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Slf4j
public class TelegramBot extends AbilityBot {
    private final TelegramMessageService telegramMessageService;
    //private final Map<Long, UserState> chatStates;

    public TelegramBot(
            @Value("${telegram.token}") String token,
            @Value("${telegram.username}") String username,
            TelegramMessageService telegramMessageService) {

        super(token, username);
        this.telegramMessageService = telegramMessageService;
        //this.chatStates = super.db.getMap("chatStates");
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info("Starts the bot")
                .locality(Locality.USER)
                .privacy(PUBLIC)
                .action(context -> {
                    silent.send("Hello! How can I assist you today?", context.chatId());
                })
                .build();
    }

    @Transactional
    public Reply reply() {
        return Reply.of(
                (abilityBot, update) -> {
                    Long chatId = getChatId(update);
                    String userMessageText = update.getMessage().getText();
                    if (Objects.equals(userMessageText, "/clear")) {
                        clearHistory(chatId);
                    } else if (userMessageText.startsWith("/system")) {
                        saveSystemMessage(chatId, userMessageText.substring(8));
                    } else {
                        generateAnswer(chatId, userMessageText);
                    }
                },
                Flag.TEXT
        );
    }

    public void clearHistory(Long chatId) {
        telegramMessageService.clearHistory(chatId);
        silent.send("The history of messages has cleared", chatId);
    }

    public void saveSystemMessage(Long chatId, String userMessageText) {
        telegramMessageService.saveSystemMessage(chatId, userMessageText);
        silent.send("The system message has saved", chatId);
    }

    private void generateAnswer(Long chatId, String userMessageText) {
        Optional<Message> emptyMessageOptional = silent.send("...", chatId);
        if (emptyMessageOptional.isEmpty()) {
            return;
        }

        String generatedText = telegramMessageService.generateAnswer(chatId, userMessageText);

        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(emptyMessageOptional.get().getMessageId())
                .text(generatedText)
                .build();
        silent.execute(editMessage);
    }

    @Override
    public long creatorId() {
        return 1L;
    }
}
