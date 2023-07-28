package vocabulary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.entity.Message;
import vocabulary.repository.MessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatGptService chatGptService;

    @Transactional(readOnly = true)
    public List<Message> getAll(String username) {
        return messageRepository.findAllByUsername(username);
    }

    @Transactional
    public List<Message> saveAndGetAll(String username, String source) {
        List<Message> newMessages = chatGptService.sendAndParseMessage(username, source);
        messageRepository.saveAll(newMessages);
        return messageRepository.findAllByUsername(username);
    }
}
