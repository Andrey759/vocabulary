package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vocabulary.controller.dto.CardDto;
import vocabulary.controller.dto.CardStatusDto;
import vocabulary.controller.enums.AddedOrReset;
import vocabulary.entity.Card;
import vocabulary.entity.User;
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
        AddedOrReset result = wordService.addOrReset(User.getCurrent(), word);
        log.info("GET /api/delete/{} Response: {}", word, result);
        return ResponseEntity.ok(result == ADDED ? "✔" : "Status updated");
    }

    @GetMapping("/api/delete/{word}")
    public ResponseEntity<String> delete(@PathVariable String word) {
        log.info("GET /api/delete/{}", word);
        wordService.delete(User.getCurrent(), word);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/api/reset/{word}")
    public ResponseEntity<CardDto> reset(@PathVariable String word) {
        log.info("GET /api/reset/{}", word);
        CardDto cardDto = wordService.reset(User.getCurrent(), word);
        log.info("GET /api/reset/{} Response: {}", word, cardDto);
        return ResponseEntity.ok(cardDto);
    }

    @GetMapping("/api/another/{word}")
    public ResponseEntity<CardDto> another(@PathVariable String word) {
        log.info("GET /api/another/{}", word);
        CardDto card = wordService.another(User.getCurrent(), word);
        log.info("GET /api/another/{} Response: {}", word, card);
        return ResponseEntity.ok(card);
    }

    @GetMapping({ "/api/next/", "/api/next/{word}" })
    public ResponseEntity<CardDto> next(@PathVariable(required = false) String word) {
        log.info("GET /api/next/{}", word);
        CardDto card = wordService.next(User.getCurrent(), word);
        log.info("GET /api/next/{} Response: {}", word, card);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/api/dict")
    public ResponseEntity<List<Card>> getDict() {
        log.info("GET /api/dict");
        List<Card> cards = wordService.findAll(User.getCurrent());
        log.info("GET /api/dict Response: {}", cards);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/api/dict")
    public ResponseEntity<CardDto> postDict(@RequestBody CardStatusDto cardStatus) {
        log.info("POST /api/dict");
        CardDto card = wordService.changeStatusAndGetCardDto(User.getCurrent(), cardStatus.getWord(), cardStatus.getStatus());
        log.info("POST /api/dict Response: {}", card);
        return ResponseEntity.ok(card);
    }
}
