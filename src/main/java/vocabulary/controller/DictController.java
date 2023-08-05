package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vocabulary.controller.dto.CardDto;
import vocabulary.controller.dto.CardStatusDto;
import vocabulary.entity.Card;
import vocabulary.service.WordService;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DictController {
    private final WordService wordService;

    @GetMapping("/api/dict")
    public ResponseEntity<List<Card>> getDict(Principal principal) {
        log.info("[{}] GET /api/dict", principal.getName());
        List<Card> cards = wordService.findAll(principal.getName());
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/api/dict")
    public ResponseEntity<CardDto> postDict(Principal principal, @RequestBody CardStatusDto cardStatus) {
        log.info("[{}] POST /api/dict", principal.getName());
        CardDto card = wordService.changeStatusAndGetCardDto(principal.getName(), cardStatus.getWord(), cardStatus.getStatus());
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/api/dict")
    public ResponseEntity<Integer> deleteDict(Principal principal, @RequestBody String word) {
        log.info("[{}] DELETE /api/dict", principal.getName());
        Integer deleted = wordService.delete(principal.getName(), word);
        return ResponseEntity.ok(deleted);
    }
}
