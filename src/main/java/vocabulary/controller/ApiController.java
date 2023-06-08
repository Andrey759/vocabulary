package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vocabulary.service.MyService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiController {
    private final MyService service;

    @GetMapping("/api/text")
    @ResponseBody
    public ResponseEntity<String> api() {
        log.info("GET /api/text");
        String text = service.getText("rehearsal");
        log.info("{}", text);
        return ResponseEntity.ok(text);
    }
}
