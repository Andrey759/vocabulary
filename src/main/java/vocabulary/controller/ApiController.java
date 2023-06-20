package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vocabulary.entity.Word;
import vocabulary.service.ChatGptService;
import vocabulary.service.WordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiController {
    private final WordService wordService;
    private final ChatGptService chatGptService;

    @PostMapping("/api/add")
    public ResponseEntity<String> add(@RequestBody String text) {
        log.info("Body={}", text);
        //return ResponseEntity.ok(wordService.addNewWord(text));
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/api/text")
    public ResponseEntity<String> text() {
        log.info("GET /api/text");
        String text = chatGptService.getText("rehearsal");
        log.info("{}", text);
        return ResponseEntity.ok(text);
    }

    @GetMapping("/api/get")
    public ResponseEntity<List<Word>> get() {
        log.info("GET /api/get/");
        return ResponseEntity.ok(wordService.getAll());
    }

    @GetMapping("/api/save/{word}")
    public ResponseEntity<Word> save(@PathVariable String word) {
        log.info("GET /api/save/" + word);
        return ResponseEntity.ok(wordService.addNewWord(word));
    }
}
