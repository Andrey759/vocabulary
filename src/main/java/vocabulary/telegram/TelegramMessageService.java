package vocabulary.telegram;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.service.ChatGptService;

import java.util.Arrays;
import java.util.List;

// public static ReplyKeyboard getPizzaToppingsKeyboard() {
//     KeyboardRow row = new KeyboardRow();
//     row.add("Margherita");
//     row.add("Pepperoni");
//     return new ReplyKeyboardMarkup(List.of(row));
// }
// sendMessage.setText("We love Pizza in here.\nSelect the toppings!");
// sendMessage.setReplyMarkup(KeyboardFactory.getPizzaToppingsKeyboard());
// sender.execute(sendMessage);

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
                .messageRole(ChatMessageRole.SYSTEM)
                .text(userMessageText)
                .build();
        telegramMessageRepository.save(systemMessage);
    }

    @Transactional
    public String generateAnswer(Long chatId, String userMessageText) {
        TelegramMessage userMessage = TelegramMessage.builder()
                .chatId(chatId)
                .messageRole(ChatMessageRole.USER)
                .text(userMessageText)
                .build();

        List<TelegramMessage> messageList = telegramMessageRepository.findByChatId(chatId);
        messageList.add(userMessage);

        List<ChatMessage> chatMessageList = messageList.stream()
                .map(tm -> new ChatMessage(tm.getMessageRole().value(), tm.getText()))
                .toList();

        String generatedText = chatGptService.receive(
                chatId.toString(),
                chatMessageList);

        TelegramMessage generatedMessage = TelegramMessage.builder()
                .chatId(chatId)
                .messageRole(ChatMessageRole.ASSISTANT)
                .text(generatedText)
                .build();

        telegramMessageRepository.saveAll(Arrays.asList(
                userMessage,
                generatedMessage));

        return generatedText;
    }
}
