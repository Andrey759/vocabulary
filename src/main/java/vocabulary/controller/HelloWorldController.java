package vocabulary.controller;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class HelloWorldController {

    @GetMapping("/")
    //@ModelAttribute("aaa") Aaa aaa
    public String helloWorld(Model model) {
        model.addAttribute("text", "My text");
        return "index";
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<String> api() {
        String token = "";
        OpenAiService service = new OpenAiService(token);

        System.out.println("\nCreating completion...");
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("ada")
                .prompt("Somebody once told me the world is gonna roll me")
                .echo(false)
                .user("anonim1234567890987654321")
                .n(3)
                .build();
        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);

        return ResponseEntity.ok("OK");
    }
}
