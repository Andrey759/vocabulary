package vocabulary.service;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ChatGptService {
    // https://platform.openai.com/docs/models/model-endpoint-compatibility
    public String getText(String word) {
        String token = "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";
        OpenAiService service = new OpenAiService(token);

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-4")
                .messages(Collections.singletonList(new ChatMessage("user", "Generate please a simple sentence with the word '" + word + "' which would be clear to understand that word meaning.")))
                //.echo(false)
                .user("anonim1234567890987654321")
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
