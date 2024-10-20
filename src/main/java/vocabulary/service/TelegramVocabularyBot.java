package vocabulary.service;

import io.github.sashirestela.openai.domain.audio.SpeechRequest;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.thymeleaf.util.StringUtils;
import vocabulary.config.TelegramVocabularyBotProperties;
import vocabulary.experiments.telegram.TelegramMessageService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnExpression("${telegram.enabled:false}")
@EnableConfigurationProperties(TelegramVocabularyBotProperties.class)
@Slf4j
public class TelegramVocabularyBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient client;
    private final TelegramMessageService messageService;
    private final CardService cardService;
    private final String token;
    private final String startMessage = "Hello!";
    private final String userLanguage = "ru";
    private final SpeechRequest.Voice voice;
    private final Double speed;
    private final boolean logging;

    public TelegramVocabularyBot(
            TelegramMessageService messageService,
            CardService cardService,
            TelegramVocabularyBotProperties properties) {

        this.client = new OkHttpTelegramClient(properties.getToken());
        this.messageService = messageService;
        this.cardService = cardService;
        this.token = properties.getToken();
        //this.startMessage = properties.getStartMessage();
        //this.userLanguage = properties.getUserLanguage();
        this.voice = properties.getVoice();
        this.speed = properties.getSpeed();
        this.logging = properties.getLogging();
    }

    @Override
    public void consume(Update update) {

    }

//    public Ability startBot() {
//        return Ability
//                .builder()
//                .name("start")
//                .info("Starts the bot")
//                .locality(Locality.USER)
//                .privacy(PUBLIC)
//                .action(context -> {
//                    if (!StringUtils.isEmpty(startMessage)) {
//                        silent.send(startMessage, context.chatId());
//                    }
//                })
//                .build();
//    }
//
//    @SneakyThrows
//    @Override
//    public void onUpdateReceived(Update update) {
//        super.onUpdateReceived(update);
//        // TODO: check that the chatId belongs to the user
//        if (Optional.ofNullable(update.getCallbackQuery()).map(CallbackQuery::getData).map("For..."::equals).orElse(false)) {
//            SendMessage sendMessage = new SendMessage();
//            sendMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
//            sendMessage.setText("Choose the repetition delay:");
//            var replyMarkup = new InlineKeyboardMarkup();
//            replyMarkup.setKeyboard(List.of(
//                    Collections.singletonList(createButton("For tomorrow")),
//                    Collections.singletonList(createButton("For five days")),
//                    Collections.singletonList(createButton("For two weeks")),
//                    Collections.singletonList(createButton("For one month")),
//                    Collections.singletonList(createButton("For two months")),
//                    Collections.singletonList(createButton("For four months")),
//                    Collections.singletonList(createButton("For one year")),
//                    Collections.singletonList(createButton("Never repeat"))
//            ));
//            sendMessage.setReplyMarkup(replyMarkup);
//            execute(sendMessage);
//            return;
//        }
//        Message message = update.getMessage();
//        if (message == null) {
//            return;
//        }
//        Long chatId = message.getChatId();
//        String userText = message.getVoice() != null
//                ? speechToText(message.getVoice().getFileId(), chatId)
//                : message.getText();
//
//        silent.send(userText, message.getChatId());
//        if (message.getVoice() != null) {
//            sendVoiceMessage(userText, chatId);
//        }
//    }
//
//    @SneakyThrows
//    private void sendVoice(InputStream inputStream, Long chatId) {
//        SendVoice sendVoice = new SendVoice();
//        sendVoice.setChatId(String.valueOf(chatId));
//        sendVoice.setVoice(new InputFile(inputStream, "answer.ogg"));
//        execute(sendVoice);
//    }
//
////    @Transactional
////    public Reply reply() {
////        return Reply.of(
////                (abilityBot, update) -> {
////                    Long chatId = getChatId(update);
////                    String userMessageText = update.getMessage().getText();
////                    if (Objects.equals(userMessageText, "/clear")) {
////                        clearHistory(chatId);
////                    } else if (userMessageText.startsWith("/system")) {
////                        saveSystemMessage(userMessageText.substring(8), chatId);
////                    } else {
////                        generateAnswer(userMessageText, chatId);
////                    }
////                },
////                Flag.TEXT
////        );
////    }
//
//    public void clearHistory(Long chatId) {
//        messageService.clearHistory(chatId);
//        silent.send("Message history cleared", chatId);
//    }
//
//    public void saveSystemMessage(String userMessageText, Long chatId) {
//        messageService.saveSystemMessage(chatId, userMessageText);
//        silent.send("System message saved", chatId);
//    }
//
//    @SneakyThrows
//    public void send(String username, Long chatId) {
//        //CardDto cardDto = cardService.getCardDtoToRepeat(username);
//
//        //KeyboardButton buttons = new KeyboardButton("aaa");
//        //buttons.setText("For tomorrow");
//        //buttons.add("For tomorrow");
//        //buttons.add("Another context");
//        //buttons.add("For " + cardDto.getNextStatus().name().toLowerCase().replaceAll("_", " "));
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(String.valueOf(chatId));
//        sendMessage.setText("The storm clouds suggested that rain was ***imminent***\\.\n\n||Здесь будет перевод на русский\\.||");
//        sendMessage.enableMarkdownV2(true);
//        var replyMarkup = new InlineKeyboardMarkup();
//        replyMarkup.setKeyboard(List.of(List.of(
//                createButton("For tomorrow"),
//                createButton("Another context"),
//                createButton("For five days")
//                //createButton("For " + cardDto.getNextStatus().name().toLowerCase().replaceAll("_", " "))
//        ), List.of(createButton("For..."), createButton("⚙"))));
//        sendMessage.setReplyMarkup(replyMarkup);
//        execute(sendMessage);
//    }
//
//    private InlineKeyboardButton createButton(String text) {
//        InlineKeyboardButton button = new InlineKeyboardButton();
//        button.setText(text);
//        button.setCallbackData(text);
//        return button;
//    }

    @SneakyThrows
    private void generateAnswer(String username, Long chatId, String userText) {
        Message emptyMessage = client.execute(SendMessage.builder()
                .chatId(chatId)
                .text("...")
                .build());

        String generatedText = messageService.generateAnswer(username, chatId, userText);
        if (logging) {
            log.info("[{},{}] Generated answer: {}", username, chatId, generatedText);
        }

        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(emptyMessage.getMessageId())
                .text(generatedText)
                .build();
        editMessage.enableMarkdown(true);

        client.execute(editMessage);
        if (logging) {
            log.info("[{},{}] Text answer successfully sent.", username, chatId);
        }
    }

    private String speechToText(String username, Long chatId, String fileId) {
        Path outputFilePath = saveFileOnDisk(username, chatId, fileId);

        try {
            String userText = messageService.speechToText(outputFilePath, userLanguage);
            if (logging) {
                log.info("[{},{}] Voice message parsed as text: {}", username, chatId, userText);
            }
            return userText;
        } finally {
            outputFilePath.toFile().deleteOnExit();
            if (logging) {
                log.info("[{},{}] The file {} deleted.", username, chatId, outputFilePath);
            }
        }
    }

    @SneakyThrows
    private Path saveFileOnDisk(String username, Long chatId, String inputFileId) {
        GetFile inputGetFile = new GetFile(inputFileId);
        org.telegram.telegrambots.meta.api.objects.File inputFile = client.execute(inputGetFile);
        File downloaded = client.downloadFile(inputFile);
        String outputPath = downloaded.getPath().substring(0, downloaded.getPath().indexOf(".")) + ".ogg";
        downloaded.renameTo(new File(outputPath));
        if (logging) {
            log.info("[{},{}] Voice message downloaded to {}", username, chatId, outputPath);
        }
        return Path.of(outputPath);
    }

    @Deprecated
    private void sendVoiceMessage(String userText, Long chatId) {
        byte[] bytes = messageService.textToSpeech(userText);

        if (logging) {
            log.info("Voice message answer audio in-memory file generated.");
        }
        //sendVoice(new ByteArrayInputStream(bytes), chatId);

        if (logging) {
            log.info("Voice message answer sent.");
        }
    }
}
