package vocabulary.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.controller.enums.AddNewWordResult;
import vocabulary.entity.Word;
import vocabulary.repository.WordRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static java.time.temporal.ChronoField.MILLI_OF_DAY;
import static vocabulary.controller.enums.AddNewWordResult.ADDED;
import static vocabulary.controller.enums.AddNewWordResult.STATUS_UPDATED;
import static vocabulary.entity.enums.WordStatus.NOT_STARTED;

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
    public AddNewWordResult addNewWord(String text) {
        Optional<Word> wordOptional = wordRepository.findByText(text);
        if (wordOptional.isPresent()) {
            wordOptional.get().setStatus(NOT_STARTED);
            wordRepository.save(wordOptional.get());
            updateWordCash();
            return STATUS_UPDATED;
        }

        Long id = LocalDateTime.now().getLong(MILLI_OF_DAY);
        wordRepository.save(Word.builder().id(id).text(text).status(NOT_STARTED).build());
        updateWordCash();
        return ADDED;
    }

    @Transactional
    public void delete(String text) {
        wordRepository.deleteByText(text);
    }

    @Transactional
    public String getNext(String text) {

        updateWordCash();

        Word word = getUserWords().stream()
                .filter(w -> Objects.equals(w.getText(), text))
                .findFirst()
                .orElse(null);

        if (word != null) {
            wordRepository.save(word.nextStatus());
            updateWordCash();
        }
        return getUserWords().size() >= 1 ? getUserWords().get(0).getText() : null;
    }

    @Transactional(readOnly = true)
    public List<Word> getAll() {
        return wordRepository.findAll();
    }

    private List<Word> getUserWords() {
        return wordsCash.get("andrey759");
    }
}
