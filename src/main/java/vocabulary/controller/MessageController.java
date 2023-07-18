package vocabulary.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vocabulary.entity.Message;
import vocabulary.entity.User;
import vocabulary.service.MessageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @GetMapping({ "/api/chat/", "/api/chat/{newMessage}" })
    public ResponseEntity<List<Message>> chat(@PathVariable(required = false) String newMessage) {
        log.info("GET /api/chat/{}", newMessage);
        List<Message> messages = messageService.saveAndGetAll(User.getCurrent(), newMessage);
        log.info("GET /api/next/{} Response {}", newMessage, messages);
        return ResponseEntity.ok(messages);
    }
}
