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

    @GetMapping("/api/complete/{word}")
    public ResponseEntity<CardDto> complete(@PathVariable String word) {
        log.info("GET /api/complete/{}", word);
        CardDto card = wordService.getNext(word);
        log.info("GET /api/complete/{} Response {}", word, card);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/api/next/")
    public ResponseEntity<CardDto> next() {
        return next("");
    }

    @GetMapping("/api/next/{word}")
    public ResponseEntity<CardDto> next(@PathVariable(required = false) String word) {
        log.info("GET /api/next/{}", word);
        //String text = chatGptService.getText("rehearsal");
        CardDto card = wordService.getNext(word);
        log.info("GET /api/next/{} Response {}", word, card);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/api/get")
    public ResponseEntity<List<Word>> get() {
        log.info("GET /api/get");
        List<Word> words = wordService.getAll();
        log.info("GET /api/get Response {}", words);
        return ResponseEntity.ok(words);
    }
}
