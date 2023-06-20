package vocabulary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.entity.Word;
import vocabulary.repository.WordRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoField.MILLI_OF_DAY;
import static vocabulary.entity.WordStatus.NOT_STARTED;

@Service
@RequiredArgsConstructor
public class WordService {
    private final Map<String, List<Word>> wordsCash = new HashMap<>();
    private final WordRepository wordRepository;

    @Transactional(readOnly = true)
    public void updateWordCash() {
        wordsCash.put("andrey759", wordRepository.findAll());
    }

    @Transactional
    public Word addNewWord(String newWord) {
        Long id = LocalDateTime.now().getLong(MILLI_OF_DAY);
        return wordRepository.save(Word.builder().id(id).text(newWord).status(NOT_STARTED).build());
    }

    @Transactional
    public List<Word> getAll() {
        return wordRepository.findAll();
    }
}
