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
import vocabulary.controller.dto.MessageDto;
import vocabulary.entity.Card;
import vocabulary.entity.Message;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoField.MILLI_OF_DAY;
import static vocabulary.controller.enums.MessageOwner.BOT;
import static vocabulary.controller.enums.MessageOwner.USER;

@Service
@Slf4j
public class ChatGptService {
    @Value("${gpt.token}")
    private String gptToken;
    @Value("${gpt.model}")
    private String gptModel;
    @Value("${gpt.message.card}")
    private String gptMessageCard;
    @Value("${gpt.message.chat}")
    private String gptMessageChat;
    private OpenAiService service;

    @PostConstruct
    public void postConstruct() {
        service = new OpenAiService(gptToken);
        log.info("gpt.message: {}", gptMessageCard);
    }

    public Card sendAndParseCard(Card card) {
        CardDto dto = sendAndParseCard(card.getUsername(), card.getWord());
        card.setSentence(dto.getSentence());
        card.setSentenceHtml(dto.getSentenceHtml());
        card.setExplanationHtml(dto.getExplanationHtml());
        card.setTranslationHtml(dto.getTranslationHtml());
        return card;
    }

    // https://platform.openai.com/docs/models/model-endpoint-compatibility
    public CardDto sendAndParseCard(String username, String word) {
        if (StringUtils.isEmpty(word)) {
            return CardDto.EMPTY;
        }
        log.info("Word: {} Sending request to gpt...", word);

        List<String> lines = receiveLines(username + "-cards", String.format(gptMessageCard, word));

        CardDto cardDto = new CardDto(
                word,
                lines.get(0),
                lines.get(1),
                lines.get(2),
                lines.get(3)
        );
        log.info("Word: {} Parsed result: {}", word, cardDto);

        return cardDto;
    }

    public List<Message> sendAndParseMessage(String username, String source) {
        if (StringUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        log.info("Message: {} Sending request to gpt...", source);

        List<String> lines = receiveLines(username, String.format(gptMessageChat, source));

        Message userMessageDto = new Message(
                LocalDateTime.now().getLong(MILLI_OF_DAY),
                username,
                USER,
                lines.get(1),
                lines.get(1),
                lines.get(2)
        );
        Message botMessageDto = new Message(
                LocalDateTime.now().getLong(MILLI_OF_DAY),
                username,
                BOT,
                "",
                lines.get(0),
                ""
        );
        log.info("Message: {} Parsed result: {} {}", source, userMessageDto, botMessageDto);

        return List.of(userMessageDto, botMessageDto);
    }

    private List<String> receiveLines(String username, String message) {
        return Arrays.stream(receive(username, message).split("\n"))
                .map(str -> str.startsWith("\\d") ? str.substring(4) : str)
                .toList();
    }
    private String receive(String username, String message) {
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(gptModel)
                .messages(Collections.singletonList(new ChatMessage("user", message)))
                .user(username)
                .n(1)
                .build();

        return service.createChatCompletion(completionRequest)
                .getChoices()
                .stream()
                .findFirst()
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent)
                .orElse(null)
                .replaceAll("\n\n", "\n");
    }
}
