package vocabulary.service;

import io.github.sashirestela.openai.domain.audio.SpeechRequest;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.facilities.filedownloader.DownloadFileException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.thymeleaf.util.StringUtils;
import vocabulary.config.TelegramVocabularyBotProperties;
import vocabulary.experiments.telegram.TelegramMessageService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static org.apache.http.HttpStatus.SC_OK;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@ConditionalOnExpression("${telegram.enabled:false}")
@EnableConfigurationProperties(TelegramVocabularyBotProperties.class)
@Slf4j
public class TelegramVocabularyBot extends AbilityBot {
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();
    private final TelegramBotsApi botsApi;
    private final TelegramMessageService messageService;
    private final String token;
    private final String startMessage = "Hello!";
    private final String userLanguage = "ru";
    private final SpeechRequest.Voice voice;
    private final Double speed;
    private final boolean logging;

    public TelegramVocabularyBot(
            TelegramBotsApi botsApi,
            TelegramMessageService messageService,
            TelegramVocabularyBotProperties properties) {
        super(properties.getToken(), properties.getUsername());
        this.botsApi = botsApi;
        this.messageService = messageService;
        this.token = properties.getToken();
        //this.startMessage = properties.getStartMessage();
        //this.userLanguage = properties.getUserLanguage();
        this.voice = properties.getVoice();
        this.speed = properties.getSpeed();
        this.logging = Optional.ofNullable(properties.getLogging()).orElse(false);
    }

    @PostConstruct
    public void postConstruct() throws TelegramApiException {
        botsApi.registerBot(this);
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info("Starts the bot")
                .locality(Locality.USER)
                .privacy(PUBLIC)
                .action(context -> {
                    if (!StringUtils.isEmpty(startMessage)) {
                        silent.send(startMessage, context.chatId());
                    }
                })
                .build();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        super.onUpdateReceived(update);
        Message message = update.getMessage();
        if (message == null) {
            return;
        }
        Long chatId = message.getChatId();
        String userText = message.getVoice() != null
                ? speechToText(message.getVoice().getFileId(), chatId)
                : message.getText();

        silent.send(userText, message.getChatId());
        if (message.getVoice() != null) {
            sendVoiceMessage(userText, chatId);
        }
    }

    @SneakyThrows
    private void sendVoice(InputStream inputStream, Long chatId) {
        SendVoice sendVoice = new SendVoice();
        sendVoice.setChatId(String.valueOf(chatId));
        sendVoice.setVoice(new InputFile(inputStream, "answer.ogg"));
        execute(sendVoice);
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
                        saveSystemMessage(userMessageText.substring(8), chatId);
                    } else {
                        generateAnswer(userMessageText, chatId);
                    }
                },
                Flag.TEXT
        );
    }

    public void clearHistory(Long chatId) {
        messageService.clearHistory(chatId);
        silent.send("Message history cleared", chatId);
    }

    public void saveSystemMessage(String userMessageText, Long chatId) {
        messageService.saveSystemMessage(chatId, userMessageText);
        silent.send("System message saved", chatId);
    }

    @SneakyThrows
    private void generateAnswer(String userMessageText, Long chatId) {
        if (logging) {
            log.info("Message received: {}", userMessageText);
        }
        Optional<Message> emptyMessageOptional = silent.send("...", chatId);
        if (emptyMessageOptional.isEmpty()) {
            return;
        }

        String generatedText = messageService.generateAnswer(userMessageText, chatId);
        if (logging) {
            log.info("Generated answer: {}", generatedText);
        }

        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(emptyMessageOptional.get().getMessageId())
                .text(generatedText)
                .build();

        editMessage.enableMarkdown(true);

        silent.execute(editMessage);
        if (logging) {
            log.info("The answer successfully sent.");
        }
    }

    @Override
    public long creatorId() {
        return 2L;
    }

    @SneakyThrows
    private Path saveFileOnDisk(String inputFileId) {
        GetFile inputGetFile = new GetFile(inputFileId);
        org.telegram.telegrambots.meta.api.objects.File inputFile = execute(inputGetFile);
        String outputFilePath = "/home/andrey/" + inputFile.getFileId() + ".ogg";
        File outputFile = downloadFile(inputFile, new File(outputFilePath));
        if (logging) {
            log.info("Voice message downloaded to {}", outputFilePath);
        }
        return Path.of(outputFile.getPath());
    }

    private String speechToText(String fileId, Long chatId) {
        if (logging) {
            log.info("[{}] Voice message received.", chatId);
        }
        Path outputFilePath = saveFileOnDisk(fileId);

        try {
            String userText = messageService.speechToText(outputFilePath, userLanguage);
            if (logging) {
                log.info("Voice message parsed as text: {}", userText);
            }
            return userText;
        } finally {
            outputFilePath.toFile().deleteOnExit();
            log.info("The file {} deleted.", outputFilePath);
        }
    }

    private void sendVoiceMessage(String userText, Long chatId) {
        byte[] bytes = messageService.textToSpeech(userText);

        if (logging) {
            log.info("Voice message answer audio in-memory file generated.");
        }
        sendVoice(new ByteArrayInputStream(bytes), chatId);

        if (logging) {
            log.info("Voice message answer sent.");
        }
    }

    private InputStream getFileDownloadStream(org.telegram.telegrambots.meta.api.objects.File inputFile) {
        String fileUrl = inputFile.getFileUrl(token);
        try {
            HttpResponse response = HTTP_CLIENT.execute(new HttpGet(fileUrl));
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == SC_OK) {
                return response.getEntity().getContent();
            } else {
                throw new TelegramApiException("Unexpected Status code while downloading file. Expected 200 got " + statusCode);
            }
        } catch (IOException | TelegramApiException e) {
            throw new DownloadFileException("Error downloading file", e);
        }
    }
}
