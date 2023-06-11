package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
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
