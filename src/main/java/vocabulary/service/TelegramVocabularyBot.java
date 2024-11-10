package vocabulary.service;

import io.github.sashirestela.openai.domain.audio.SpeechRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.stream.Streams;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.thymeleaf.util.StringUtils;
import vocabulary.config.TelegramVocabularyBotProperties;
import vocabulary.controller.dto.CardDto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class TelegramVocabularyBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient client;
    private final CardService cardService;
    private final String startMessage = "Hello!";
    private final String userLanguage = "ru";
    private final SpeechRequest.Voice voice;
    private final Double speed;
    private final boolean logging;

    public TelegramVocabularyBot(
            CardService cardService,
            TelegramVocabularyBotProperties properties) {

        this.client = new OkHttpTelegramClient(properties.getToken());
        this.cardService = cardService;
        //this.startMessage = properties.getStartMessage();
        //this.userLanguage = properties.getUserLanguage();
        this.voice = properties.getVoice();
        this.speed = properties.getSpeed();
        this.logging = properties.getLogging();
    }

    @Override
    @SneakyThrows
    public void consume(Update update) {
        Message message = update.getMessage();
        if (!update.hasMessage() || (!message.hasText() && !message.hasVoice())) {
            return;
        }
        Long chatId = message.getChatId();
        String username = message.getFrom().getUserName();
        String userText = message.getText();

        if (logging) {
            log.info("[{},{}] Text message received: {}", username, chatId, userText);
        }

        if (Objects.equals(userText, "/start")) {
            start(username, chatId);
        } else if (Optional.ofNullable(update.getCallbackQuery())
                .map(CallbackQuery::getData)
                .map("For..."::equals)
                .orElse(false)) {

            client.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text("Choose the repetition delay:")
                    .replyMarkup(new InlineKeyboardMarkup(List.of(
                            new InlineKeyboardRow("For tomorrow"),
                            new InlineKeyboardRow("For five days"),
                            new InlineKeyboardRow("For two weeks"),
                            new InlineKeyboardRow("For one month"),
                            new InlineKeyboardRow("For two months"),
                            new InlineKeyboardRow("For four months"),
                            new InlineKeyboardRow("For one year"),
                            new InlineKeyboardRow("Never repeat")
                    )))
                    .build());
        } else {
            send(username, chatId);
        }
    }

    @SneakyThrows
    public void start(String username, Long chatId) {
        if (StringUtils.isEmpty(startMessage)) {
            return;
        }
        client.execute(SendMessage.builder()
                .chatId(chatId)
                .text(startMessage)
                .build());
        if (logging) {
            log.info("[{},{}] Start message: {}", username, chatId, startMessage);
        }
    }

    @SneakyThrows
    public void send(String username, Long chatId) {
        CardDto cardDto = cardService.getCardDtoToRepeat(username);

        // TODO: Remove "another" and hide it into settings or use an icon
        //createButton("For " + cardDto.getNextStatus().name().toLowerCase().replaceAll("_", " "))
        var replyMarkup = new InlineKeyboardMarkup(List.of(
                raw("For tomorrow", "Another context", "For five days", "⚙")));

        client.execute(SendMessage.builder()
                .chatId(chatId)
                .text("The storm clouds suggested that rain was ***IMMINENT***\\.\n\n||Здесь будет перевод на русский\\.||")
                .parseMode("MarkdownV2")
                .replyMarkup(replyMarkup)
                .build());
    }
//
//    private InlineKeyboardButton createButton(String text) {
//        InlineKeyboardButton button = new InlineKeyboardButton();
//        button.setText(text);
//        button.setCallbackData(text);
//        return button;
//    }
    private static InlineKeyboardRow raw(String... texts) {
        return new InlineKeyboardRow(Streams.of(texts)
                .map(text ->
                        InlineKeyboardButton.builder()
                                .text(text)
                                .callbackData(text.toUpperCase().replace(" ", "_"))
                                .build())
                .collect(Collectors.toList()));
    }
}
