package vocabulary.experiments.telegram;

import io.github.sashirestela.openai.domain.audio.SpeechRequest;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@ToString(of = "username")
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient client;
    private final TelegramMessageService messageService;
    private final String username;
    private final String startMessage;
    private final String userLanguage;
    private final SpeechRequest.Voice voice;
    private final Double speed;
    private final boolean logging;

    public TelegramBot(
            TelegramMessageService messageService,
            TelegramProperties.Bot properties) {

        this.client = new OkHttpTelegramClient(properties.getToken());
        this.messageService = messageService;
        this.username = properties.getUsername();
        this.startMessage = properties.getStartMessage();
        this.userLanguage = properties.getUserLanguage();
        this.voice = properties.getVoice();
        this.speed = properties.getSpeed();
        this.logging = Optional.ofNullable(properties.getLogging()).orElse(false);
    }

    @Override
    public void consume(Update update) {
        Message message = update.getMessage();
        if (!update.hasMessage() || (!message.hasText() && !message.hasVoice())) {
            return;
        }
        Long chatId = message.getChatId();
        String username = message.getFrom().getUserName();
        String userText = message.getVoice() != null
                ? speechToText(username, chatId, message.getVoice().getFileId())
                : message.getText();

        if (logging) {
            log.info("[{},{}] {} message received: {}",
                    username, chatId, message.getVoice() != null ? "Voice" : "Text", userText);
        }

        if (Objects.equals(userText, "/start")) {
            start(username, chatId);
        } else if (Objects.equals(userText, "/clear")) {
            clearHistory(username, chatId);
        } else if (userText.startsWith("/system")) {
            saveSystemMessage(username, chatId, userText.substring(8));
        } else if (message.getVoice() != null) {
            generateVoiceAnswer(username, chatId, userText);
        } else {
            generateAnswer(username, chatId, userText);
        }
    }

    @SneakyThrows
    public void start(String username, Long chatId) {
        messageService.clearHistory(chatId);
        messageService.saveAssistantMessage(username, chatId, startMessage);
        client.execute(SendMessage.builder()
                .chatId(chatId)
                .text(startMessage)
                .build());
        if (logging) {
            log.info("[{},{}] Start message: {}", username, chatId, startMessage);
        }
    }

    @SneakyThrows
    public void clearHistory(String username, Long chatId) {
        messageService.clearHistory(chatId);
        client.execute(SendMessage.builder()
                .chatId(chatId)
                .text("Message history cleared")
                .build());
        if (logging) {
            log.info("[{},{}] Message history cleared", username, chatId);
        }
    }

    @SneakyThrows
    public void saveSystemMessage(String username, Long chatId, String messageText) {
        messageService.saveSystemMessage(username, chatId, messageText);
        client.execute(SendMessage.builder()
                .chatId(chatId)
                .text("System message saved")
                .build());
        if (logging) {
            log.info("[{},{}] System message saved: {}", username, chatId, messageText);
        }
    }

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

    @SneakyThrows
    private void generateVoiceAnswer(String username, Long chatId, String userText) {

        String generatedText = messageService.generateAnswer(username, chatId, userText);
        if (logging) {
            log.info("[{},{}] Generated answer: {}", username, chatId, generatedText);
        }

        byte[] bytes = messageService.textToSpeech(generatedText);

        if (logging) {
            log.info("[{},{}] Voice message answer audio in-memory file generated.", username, chatId);
        }
        client.execute(SendVoice.builder()
                .chatId(chatId)
                .voice(new InputFile(new ByteArrayInputStream(bytes), "answer.ogg"))
                .build());

        if (logging) {
            log.info("[{},{}] Voice answer successfully sent.", username, chatId);
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
}
