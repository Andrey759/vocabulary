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
        log.info("gpt.message: {}", gptMessageCard);
    }

    // https://platform.openai.com/docs/models/model-endpoint-compatibility
    public Card sendAndParseCard(Card card) {
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

        log.info("New message: {} Response:\n{}", newMessage, response);

        return parsingService.parseMessages(username, response);
    }
    private String receive(String username, String message) {
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(gptModel)
                .messages(Collections.singletonList(new ChatMessage("user", message)))
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
    private boolean isValidCardResponse(String response) {
        List<String> lines = parsingService.receiveLines(response);
        String sentenceHtml = lines.get(1);
        String translationHtml = lines.get(3);
        if (!sentenceHtml.contains("<b>")) {
            return false;
        }
        if (!translationHtml.contains("<b>") || INVALID_TRANSLATION_PATTERN.matcher(translationHtml).find()) {
            return false;
        }
        return true;
    }
}
