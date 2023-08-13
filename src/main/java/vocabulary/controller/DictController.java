package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vocabulary.controller.dto.CardDto;
import vocabulary.controller.dto.CardStatusDto;
import vocabulary.controller.dto.DictDto;
import vocabulary.service.CardService;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DictController {
    private final CardService cardService;

    @GetMapping("/api/dict")
    public ResponseEntity<List<DictDto>> getDict(Principal principal) {
        log.info("[{}] GET /api/dict", principal.getName());
        List<DictDto> dictDtos = cardService.findAll(principal.getName());
        return ResponseEntity.ok(dictDtos);
    }

    @PostMapping("/api/dict")
    public ResponseEntity<CardDto> postDict(Principal principal, @RequestBody CardStatusDto cardStatus) {
        log.info("[{}] POST /api/dict", principal.getName());
        CardDto cardDto = cardService.changeStatusAndGetCardDto(
                principal.getName(), cardStatus.getWord(), cardStatus.getStatus());
        return ResponseEntity.ok(cardDto);
    }

    @DeleteMapping("/api/dict")
    public ResponseEntity<CardDto> deleteDict(Principal principal, @RequestBody String word) {
        log.info("[{}] DELETE /api/dict", principal.getName());
        CardDto cardDto = cardService.deleteAndGetCardDto(principal.getName(), word);
        return ResponseEntity.ok(cardDto);
    }
}
