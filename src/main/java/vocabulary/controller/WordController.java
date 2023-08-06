package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vocabulary.controller.dto.CardDto;
import vocabulary.service.WordService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WordController {
    private final WordService wordService;

    @GetMapping("/api/add/{word}")
    public ResponseEntity<Boolean> add(Principal principal, @PathVariable String word) {
        log.info("[{}] GET /api/add/{}", principal.getName(), word);
        boolean created = wordService.addOrReset(principal.getName(), word);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/api/reset/{word}")
    public ResponseEntity<CardDto> reset(Principal principal, @PathVariable String word) {
        log.info("[{}] GET /api/reset/{}", principal.getName(), word);
        CardDto cardDto = wordService.reset(principal.getName(), word);
        return ResponseEntity.ok(cardDto);
    }

    @GetMapping("/api/another/{word}")
    public ResponseEntity<CardDto> another(Principal principal, @PathVariable String word) {
        log.info("[{}] GET /api/another/{}", principal.getName(), word);
        CardDto card = wordService.another(principal.getName(), word);
        return ResponseEntity.ok(card);
    }

    @GetMapping({ "/api/next/", "/api/next/{word}" })
    public ResponseEntity<CardDto> next(Principal principal, @PathVariable(required = false) String word) {
        log.info("[{}] GET /api/next/{}", principal.getName(), word);
        CardDto card = wordService.next(principal.getName(), word);
        return ResponseEntity.ok(card);
    }
}
