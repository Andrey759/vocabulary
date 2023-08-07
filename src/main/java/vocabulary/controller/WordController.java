package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vocabulary.controller.dto.CardDto;
import vocabulary.service.WordService;

import java.security.Principal;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WordController {
    private static final Pattern VALID_WORD_PATTERN = Pattern.compile("[A-z0-9-' ]+");
    private final WordService wordService;

    @GetMapping( "/api/word")
    public ResponseEntity<CardDto> getWord(Principal principal) {
        log.info("[{}] GET /api/word", principal.getName());
        CardDto card = wordService.getCardDtoToRepeat(principal.getName());
        return ResponseEntity.ok(card);
    }

    @PostMapping("/api/word")
    public ResponseEntity<Boolean> postWord(Principal principal, @RequestBody String word) {
        log.info("[{}] POST /api/word {}", principal.getName(), word);
        if (word == null || word.length() < 3 || !VALID_WORD_PATTERN.matcher(word).matches()) {
            throw new RuntimeException("Word must be not empty");
        }
        boolean created = wordService.addOrReset(principal.getName(), word);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/api/word/reset")
    public ResponseEntity<CardDto> postReset(Principal principal, @RequestBody String word) {
        log.info("[{}] POST /api/word/reset {}", principal.getName(), word);
        CardDto cardDto = wordService.reset(principal.getName(), word);
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping("/api/word/another")
    public ResponseEntity<CardDto> postAnother(Principal principal, @RequestBody String word) {
        log.info("[{}] POST /api/word/another {}", principal.getName(), word);
        CardDto card = wordService.another(principal.getName(), word);
        return ResponseEntity.ok(card);
    }

    @PostMapping("/api/word/next")
    public ResponseEntity<CardDto> postNext(Principal principal, @RequestBody String word) {
        log.info("[{}] POST /api/word/next {}", principal.getName(), word);
        CardDto card = wordService.next(principal.getName(), word);
        return ResponseEntity.ok(card);
    }
}
