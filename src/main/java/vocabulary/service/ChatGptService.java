package vocabulary.service;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Collections;

@Service
public class ChatGptService {
    @Value("${gpt.token}")
    private String gptToken;
    private OpenAiService service;

    @PostConstruct
    public void postConstruct() {
        service = new OpenAiService(gptToken);
    }

    // https://platform.openai.com/docs/models/model-endpoint-compatibility
    public String getText(String word) {
        if (StringUtils.isEmpty(word)) {
            return "";
        }

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(Collections.singletonList(new ChatMessage("user", "Generate please a simple sentence with the word '" + word + "' which would be clear to understand that word meaning.")))
                //.echo(false)
                .user("andrey759")
                .n(1)
                .build();
        return service.createChatCompletion(completionRequest)
                .getChoices()
                .stream()
                .findFirst()
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent)
                .orElse(null);
    }
}
