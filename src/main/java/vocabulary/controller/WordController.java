package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vocabulary.controller.dto.CardDto;
import vocabulary.controller.enums.AddedOrReset;
import vocabulary.entity.Card;
import vocabulary.service.WordService;

import java.util.List;

import static vocabulary.controller.enums.AddedOrReset.ADDED;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WordController {
    private final WordService wordService;

    @GetMapping("/api/add/{word}")
    public ResponseEntity<String> add(@PathVariable String word) {
        log.info("GET /api/add/{}", word);
        AddedOrReset result = wordService.addOrReset(word);
        log.info("GET /api/delete/{} Response {}", word, result);
        return ResponseEntity.ok(result == ADDED ? "✔" : "Status updated");
    }

    @GetMapping("/api/delete/{word}")
    public ResponseEntity<String> delete(@PathVariable String word) {
        log.info("GET /api/delete/{}", word);
        wordService.delete(word);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/api/reset/{word}")
    public ResponseEntity<CardDto> reset(@PathVariable String word) {
        log.info("GET /api/reset/{}", word);
        CardDto cardDto = wordService.reset(word);
        log.info("GET /api/reset/{} Response {}", word, cardDto);
        return ResponseEntity.ok(cardDto);
    }

    @GetMapping("/api/another/{word}")
    public ResponseEntity<CardDto> another(@PathVariable String word) {
        log.info("GET /api/another/{}", word);
        CardDto card = wordService.another(word);
        log.info("GET /api/another/{} Response {}", word, card);
        return ResponseEntity.ok(card);
    }

    @GetMapping({ "/api/next/", "/api/next/{word}" })
    public ResponseEntity<CardDto> next(@PathVariable(required = false) String word) {
        log.info("GET /api/next/{}", word);
        CardDto card = wordService.next(word);
        log.info("GET /api/next/{} Response {}", word, card);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/api/get")
    public ResponseEntity<List<Card>> get() {
        log.info("GET /api/get");
        List<Card> cards = wordService.findAll();
        log.info("GET /api/get Response {}", cards);
        return ResponseEntity.ok(cards);
    }
}
