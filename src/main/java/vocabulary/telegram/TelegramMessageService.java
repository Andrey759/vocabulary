package vocabulary.telegram;

import io.github.sashirestela.openai.domain.chat.Role;
import io.github.sashirestela.openai.domain.chat.message.*;
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
                .messageRole(Role.SYSTEM)
                .text(userMessageText)
                .build();
        telegramMessageRepository.save(systemMessage);
    }

    //@Transactional
    public String generateAnswer(Long chatId, String userMessageText) {
        TelegramMessage userMessage = TelegramMessage.builder()
                .chatId(chatId)
                .messageRole(Role.USER)
                .text(userMessageText)
                .build();

        List<TelegramMessage> messageList = telegramMessageRepository.findByChatId(chatId);
        messageList.add(userMessage);

        List<ChatMsg> chatMessageList = messageList.stream()
                .map(tm -> switch (tm.getMessageRole()) {
                    case SYSTEM -> new ChatMsgSystem(tm.getText());
                    case USER -> new ChatMsgUser(tm.getText());
                    case ASSISTANT -> new ChatMsgAssistant(tm.getText());
                    case TOOL -> throw new RuntimeException("MessageRole TOOL is not supported.");
                })
                .toList();

        String generatedText = chatGptService.receive(
                chatId.toString(),
                chatMessageList);

        TelegramMessage generatedMessage = TelegramMessage.builder()
                .chatId(chatId)
                .messageRole(Role.ASSISTANT)
                .text(generatedText)
                .build();

        telegramMessageRepository.saveAll(Arrays.asList(
                userMessage,
                generatedMessage));

        return generatedText;
    }
}
