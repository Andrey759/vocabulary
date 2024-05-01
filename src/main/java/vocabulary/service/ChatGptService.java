package vocabulary.service;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import io.github.sashirestela.openai.domain.chat.content.ContentPartText;
import io.github.sashirestela.openai.domain.chat.message.ChatMsg;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgUser;
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
    private SimpleOpenAI openai;
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
        this.openai = SimpleOpenAI.builder()
                .apiKey(gptToken)
                //.organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
                //.projectId(System.getenv("OPENAI_PROJECT_ID"))
                .build();
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
        return receive(username, Collections.singletonList(new ChatMsgUser(message)));
    }

    public String receive(String username, List<ChatMsg> messageList) {
        ChatRequest chatRequest = ChatRequest.builder()
                .model(gptModel)
                .messages(messageList)
                .user(username)
                .temperature(0.0)   // Rhe level of randomness in the generated text.
                                    // A higher temperature value will result in more diverse and creative responses
                //.maxTokens(500)   // max words or characters (need to test)
                .n(1)            // number of responses
                .build();

        String response = openai.chatCompletions().create(chatRequest).join().firstContent();

        response = response.replaceAll("\n\n", "\n");   // TODO: move to a specific service

        log.info("ChatGPT response:\n{}", response);
        return response;
    }

    private static final Pattern INVALID_TRANSLATION_PATTERN = Pattern.compile("<b>[A-z0-9-' ]+</b>");
    private boolean isValidCardResponse(Card card) {     // TODO: move to a specific service
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
