package vocabulary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.entity.Word;
import vocabulary.repository.WordRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {
    private final WordRepository wordRepository;

    @Transactional
    public Word addNewWord(String newWord) {
        Long id = LocalDateTime.now().getLong(ChronoField.MILLI_OF_DAY);
        return wordRepository.save(Word.builder().id(id).text(newWord).build());
    }

    @Transactional
    public List<Word> getAll() {
        return wordRepository.findAll();
    }
}
