package vocabulary.service;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import vocabulary.controller.dto.CardDto;
import vocabulary.entity.Card;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ChatGptService {
    @Value("${gpt.token}")
    private String gptToken;
    @Value("${gpt.model}")
    private String gptModel;
    @Value("${gpt.message}")
    private String gptMessage;
    private OpenAiService service;

    @PostConstruct
    public void postConstruct() {
        service = new OpenAiService(gptToken);
        log.info("gpt.message: {}", gptMessage);
    }

    public Card sendRequest(Card card) {
        CardDto dto = sendRequest(card.getUsername(), card.getWord());
        card.setSentence(dto.getSentence());
        card.setSentenceHtml(dto.getSentenceHtml());
        card.setExplanationHtml(dto.getExplanationHtml());
        card.setTranslationHtml(dto.getTranslationHtml());
        return card;
    }

    // https://platform.openai.com/docs/models/model-endpoint-compatibility
    public CardDto sendRequest(String username, String word) {
        if (StringUtils.isEmpty(word)) {
            return CardDto.EMPTY;
        }
        log.info("Word: {} Sending request to gpt...", word);

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(gptModel)
                .messages(buildMessage(word))
                .user(username)
                .n(1)
                .build();

        String response = service.createChatCompletion(completionRequest)
                .getChoices()
                .stream()
                .findFirst()
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent)
                .orElse(null)
                .replaceAll("\n\n", "\n");

        log.info("Word: {} Received result: {}", word, response);

        List<String> lines = Arrays.stream(response.split("\n"))
                .map(str -> str.substring(4))
                .toList();

        CardDto dto = new CardDto(
                word,
                lines.get(0),
                lines.get(1),
                lines.get(2),
                lines.get(3)
        );
        log.info("Word: {} Parsed result: {}", word, dto);

        return dto;
    }

    private List<ChatMessage> buildMessage(String word) {
        return Collections.singletonList(new ChatMessage("user", buildMessageContent(word)));
    }
    private String buildMessageContent(String word) {
        return String.format(gptMessage, word);
    }
}
