package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vocabulary.entity.Message;
import vocabulary.entity.User;
import vocabulary.service.MessageService;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/api/chat")
    public ResponseEntity<List<Message>> getChat(Principal principal) {
        log.info("GET /api/chat");
        List<Message> messages = messageService.getAll(principal.getName());
        if (messages.size() == 0) {
            messages = Collections.singletonList(Message.HELLO);
        }
        log.info("GET /api/chat Response: {}", messages);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/api/chat")
    public ResponseEntity<List<Message>> postChat(Principal principal, @RequestBody String newMessage) {
        log.info("POST /api/chat {}", newMessage);
        List<Message> messages = messageService.saveAndGetAll(principal.getName(), newMessage);
        log.info("POST /api/chat {} Response: {}", newMessage, messages);
        return ResponseEntity.ok(messages);
    }
}
