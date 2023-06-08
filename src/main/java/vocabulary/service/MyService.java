package vocabulary.service;

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MyService {
    // https://platform.openai.com/docs/models/model-endpoint-compatibility
    public String getText(String word) {
        String token = "";
        OpenAiService service = new OpenAiService(token);

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
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
