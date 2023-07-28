package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vocabulary.entity.Message;
import vocabulary.entity.User;
import vocabulary.service.MessageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/api/chat")
    public ResponseEntity<List<Message>> getChat() {
        log.info("GET /api/chat");
        List<Message> messages = messageService.getAll(User.getCurrent());
        log.info("GET /api/chat Response: {}", messages);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/api/chat")
    public ResponseEntity<List<Message>> postChat(@RequestBody String newMessage) {
        log.info("POST /api/chat Body: {}", newMessage);
        List<Message> messages = messageService.saveAndGetAll(User.getCurrent(), newMessage);
        log.info("POST /api/chat Body: {} Response: {}", newMessage, messages);
        return ResponseEntity.ok(messages);
    }
}
