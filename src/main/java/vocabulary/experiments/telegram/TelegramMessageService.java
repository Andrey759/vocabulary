package vocabulary.experiments.telegram;

import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.AssistantMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.SystemMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.UserMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.service.ChatGptService;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static io.github.sashirestela.openai.domain.chat.ChatMessage.ChatRole.*;

@Service
@RequiredArgsConstructor
public class TelegramMessageService {
    private final TelegramMessageRepository telegramMessageRepository;
    private final ChatGptService chatGptService;

    @Transactional
    public void clearHistory(Long chatId) {
        telegramMessageRepository.deleteByChatId(chatId);
    }

    @Transactional
    public void saveSystemMessage(Long chatId, String userMessageText) {
        TelegramMessage systemMessage = TelegramMessage.builder()
                .chatId(chatId)
                .messageRole(SYSTEM)
                .text(userMessageText)
                .build();
        telegramMessageRepository.save(systemMessage);
    }

    @Transactional
    public String generateAnswer(Long chatId, Path audioFilePath, String language) {
        String userMessageText = chatGptService.speechToText(audioFilePath, language);
        return generateAnswer(userMessageText, chatId);
    }

    @Transactional
    public String generateAnswer(String userMessageText, Long chatId) {
        TelegramMessage userMessage = TelegramMessage.builder()
                .chatId(chatId)
                .messageRole(USER)
                .text(userMessageText)
                .build();

        List<TelegramMessage> messageList = telegramMessageRepository.findByChatId(chatId);
        messageList.add(userMessage);

        List<ChatMessage> chatMessageList = messageList.stream()
                .map(tm -> switch (tm.getMessageRole()) {
                    case SYSTEM -> SystemMessage.of(tm.getText());
                    case USER -> UserMessage.of(tm.getText());
                    case ASSISTANT -> AssistantMessage.of(tm.getText());
                    case TOOL -> throw new RuntimeException("MessageRole TOOL is not supported.");
                })
                .toList();

        String generatedText = chatGptService.receive(
                chatId.toString(),
                chatMessageList);

        TelegramMessage generatedMessage = TelegramMessage.builder()
                .chatId(chatId)
                .messageRole(ASSISTANT)
                .text(generatedText)
                .build();

        telegramMessageRepository.saveAll(Arrays.asList(
                userMessage,
                generatedMessage));

        return generatedText;
    }

    public String speechToText(Path path, String language) {
        return chatGptService.speechToText(path, language);
    }

    public byte[] textToSpeech(String input) {
        return chatGptService.textToSpeech(input);
    }
}
