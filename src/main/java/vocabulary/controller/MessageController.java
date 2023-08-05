package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vocabulary.entity.Message;
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
        log.info("[{}] GET /api/chat", principal.getName());
        List<Message> messages = messageService.getAll(principal.getName());
        if (messages.size() == 0) {
            messages = Collections.singletonList(Message.HELLO);
        }
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/api/chat")
    public ResponseEntity<List<Message>> postChat(Principal principal, @RequestBody String newMessage) {
        log.info("[{}] POST /api/chat {}", principal.getName(), newMessage);
        List<Message> messages = messageService.saveAndGetAll(principal.getName(), newMessage);
        return ResponseEntity.ok(messages);
    }
}
