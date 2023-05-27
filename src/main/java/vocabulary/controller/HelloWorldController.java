package vocabulary.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HelloWorldController {

    @GetMapping("/")
    public ResponseEntity<String> helloWorld() {
        log.info("GET /");
        return ResponseEntity.ok("Hello World!");
    }
}
