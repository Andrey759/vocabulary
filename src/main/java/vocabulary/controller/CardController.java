package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vocabulary.controller.dto.CardDto;
import vocabulary.entity.enums.CardStatus;
import vocabulary.service.CardService;

import java.security.Principal;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CardController {
    private static final Pattern VALID_WORD_PATTERN = Pattern.compile("[A-z0-9-' ]+");
    private final CardService cardService;

    @GetMapping( "/api/card")
    public ResponseEntity<CardDto> getCard(Principal principal) {
        log.info("[{}] GET /api/card", principal.getName());
        CardDto cardDto = cardService.getCardDtoToRepeat(principal.getName());
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping("/api/card")
    public ResponseEntity<Boolean> postCard(Principal principal, @RequestBody String word) {
        log.info("[{}] POST /api/card {}", principal.getName(), word);
        if (word == null || word.length() < 3 || !VALID_WORD_PATTERN.matcher(word).matches()) {
            throw new RuntimeException("Word must be not empty");
        }
        boolean created = cardService.addOrReset(principal.getName(), word);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/api/card/reset")
    public ResponseEntity<CardDto> postReset(Principal principal, @RequestBody String word) {
        log.info("[{}] POST /api/card/reset {}", principal.getName(), word);
        CardDto cardDto = cardService.reset(principal.getName(), word);
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping("/api/card/another")
    public ResponseEntity<CardDto> postAnother(Principal principal, @RequestBody String word) {
        log.info("[{}] POST /api/card/another {}", principal.getName(), word);
        CardDto cardDto = cardService.another(principal.getName(), word);
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping("/api/card/next")
    public ResponseEntity<CardDto> postNext(Principal principal, @RequestBody String word) {
        log.info("[{}] POST /api/card/next {}", principal.getName(), word);
        CardDto cardDto = cardService.next(principal.getName(), word);
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping("/api/card/status/{newStatus}")
    public ResponseEntity<CardDto> postNext(Principal principal, @RequestBody String word, @PathVariable CardStatus newStatus) {
        log.info("[{}] POST /api/card/status/{} {}", principal.getName(), newStatus, word);
        CardDto cardDto = cardService.status(principal.getName(), word, newStatus);
        return ResponseEntity.ok(cardDto);
    }
}
