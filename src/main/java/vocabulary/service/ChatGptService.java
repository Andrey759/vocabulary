package vocabulary.service;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vocabulary.entity.Card;
import vocabulary.entity.Message;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatGptService {
    private final ParsingService parsingService;
    private OpenAiService service;
    @Value("${gpt.token}")
    private String gptToken;
    @Value("${gpt.model}")
    private String gptModel;
    @Value("${gpt.message.card}")
    private String gptMessageCard;
    @Value("${gpt.message.chat}")
    private String gptMessageChat;

    @PostConstruct
    public void postConstruct() {
        service = new OpenAiService(gptToken);
        log.info("gpt.token: {}", gptToken);
        log.info("gpt.message.card: {}", gptMessageCard);
        log.info("gpt.message.chat: {}", gptMessageChat);
    }

    // https://platform.openai.com/docs/models/model-endpoint-compatibility
    public Card sendAndParseCardWithValidation(Card card) {
        card = sendAndParseCard(card);
        if (!isValidCardResponse(card)) {
            card = sendAndParseCard(card);  // The first attempt
        }
        if (!isValidCardResponse(card)) {
            card = sendAndParseCard(card);  // The second attempt
        }
        return card;    // If two attempts didn't help, return the result as is
    }

    private Card sendAndParseCard(Card card) {
        String request = String.format(gptMessageCard, card.getWord());
        String response = receive(card.getUsername(), request);

        log.info("Word: {} Response:\n{}", card.getWord(), response);

        card.setResponse(response);
        parsingService.parseAndFillCard(card);
        return card;
    }

    public List<Message> sendAndParseMessage(String username, String newMessage) {
        String request = String.format(gptMessageChat, newMessage);
        String response = receive(username, request);

        log.info("New message: {}\n{}", newMessage, response);

        return parsingService.parseMessages(username, newMessage, response);
    }

    public String receive(String username, String message) {
        return receive(username, Collections.singletonList(new ChatMessage("user", message)));
    }

    public String receive(String username, List<ChatMessage> messageList) {
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(gptModel)
                .messages(messageList)
                .user(username)
                .n(1)
                .build();

        String response = service.createChatCompletion(completionRequest)
                .getChoices()
                .stream()
                .findFirst()
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent)
                .orElse("")
                .replaceAll("\n\n", "\n");

        log.info("ChatGPT response:\n{}", response);
        return response;
    }

    private static final Pattern INVALID_TRANSLATION_PATTERN = Pattern.compile("<b>[A-z0-9-' ]+</b>");
    private boolean isValidCardResponse(Card card) {
        if (!card.getSentenceHtml().contains("<b>")) {
            return false;
        }
        if (!card.getTranslationHtml().contains("<b>")
                || INVALID_TRANSLATION_PATTERN.matcher(card.getTranslationHtml()).find()) {
            return false;
        }
        return true;
    }
}
