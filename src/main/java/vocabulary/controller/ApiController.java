package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vocabulary.controller.dto.CardDto;
import vocabulary.controller.enums.AddNewWordResult;
import vocabulary.entity.Word;
import vocabulary.service.ChatGptService;
import vocabulary.service.WordService;

import java.util.List;
import java.util.Objects;

import static vocabulary.controller.enums.AddNewWordResult.ADDED;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiController {
    private final WordService wordService;
    private final ChatGptService chatGptService;

    @GetMapping("/api/add/{text}")
    public ResponseEntity<String> add(@PathVariable String text) {
        log.info("GET /api/add/{}", text);
        AddNewWordResult result = wordService.addNewWord(text);
        log.info("GET /api/delete/{} Response {}", text, result);
        return ResponseEntity.ok(wordService.addNewWord(text) == ADDED ? "✔" : "Status updated");
    }

    @GetMapping("/api/delete/{text}")
    public ResponseEntity<String> delete(@PathVariable String text) {
        log.info("GET /api/delete/{}", text);
        wordService.delete(text);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/api/another/{text}")
    public ResponseEntity<String> another(@PathVariable String text) {
        log.info("GET /api/another/{}", text);
        String chatGptText = chatGptService.getText(text);
        log.info("GET /api/another/{} Response {}", text, chatGptText);
        return ResponseEntity.ok(chatGptText);
    }

    @GetMapping("/api/complete/{text}")
    public ResponseEntity<String> complete(@PathVariable String text) {
        log.info("GET /api/complete/{}", text);
        String wordText = wordService.getNext(text);
        String chatGptText = chatGptService.getText(wordText);
        log.info("GET /api/complete/{} Response {}", text, chatGptText);
        return ResponseEntity.ok(chatGptText);
    }

    @GetMapping("/api/next/")
    public ResponseEntity<CardDto> next() {
        return next("");
    }

    @GetMapping("/api/next/{text}")
    public ResponseEntity<CardDto> next(@PathVariable(required = false) String text) {
        log.info("GET /api/next/{}", text);
        //String text = chatGptService.getText("rehearsal");
        String wordText = wordService.getNext(text);
        String chatGptText = chatGptService.getText(wordText);
        log.info("GET /api/next/{} Response {}", text, chatGptText);
        return ResponseEntity.ok(new CardDto(wordText, chatGptText, null, null));
    }

    @GetMapping("/api/get")
    public ResponseEntity<List<Word>> get() {
        log.info("GET /api/get");
        List<Word> words = wordService.getAll();
        log.info("GET /api/get Response {}", words);
        return ResponseEntity.ok(words);
    }
}
