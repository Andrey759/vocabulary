package vocabulary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.controller.dto.CardDto;
import vocabulary.controller.enums.AddNewWordResult;
import vocabulary.entity.Word;
import vocabulary.entity.enums.WordStatus;
import vocabulary.repository.WordRepository;

import java.time.LocalDateTime;
import java.util.*;

import static vocabulary.controller.enums.AddNewWordResult.ADDED;
import static vocabulary.controller.enums.AddNewWordResult.STATUS_UPDATED;
import static vocabulary.entity.enums.WordStatus.LEARNING;

@Service
@RequiredArgsConstructor
public class WordService {
    private final Map<String, List<CardDto>> cardCash = new HashMap<>();
    private final WordRepository wordRepository;
    private final ChatGptService chatGptService;

    @Transactional(readOnly = true)
    public void updateCardCash() {
        List<CardDto> cardsOld = cardCash.getOrDefault("andrey759", Collections.emptyList());
        List<CardDto> cardsNew = wordRepository.findByReadyAtLessThan(LocalDateTime.now())
                .stream()
                .map(word -> cardsOld.stream()
                        .filter(card -> Objects.equals(card.getWord(), word.getText()))
                        .findFirst()
                        .orElse(chatGptService.createCard(word.getText()))
                )
                .toList();
        cardCash.put("andrey759", cardsNew);
    }

    @Transactional
    public AddNewWordResult addNewWord(String word) {
        Optional<Word> wordOptional = wordRepository.findByText(word);
        if (wordOptional.isPresent()) {
            wordOptional.get().setStatus(LEARNING);
            wordRepository.save(wordOptional.get());
            updateCardCash();
            return STATUS_UPDATED;
        }

        wordRepository.save(Word.create(word));
        updateCardCash();
        return ADDED;
    }

    @Transactional
    public void delete(String text) {
        wordRepository.deleteByText(text);
    }

    @Transactional
    public CardDto getNext(String word) {

        updateCardCash();

        CardDto card = getUserCards().stream()
                .filter(w -> Objects.equals(w.getWord(), word))
                .findFirst()
                .orElse(null);

        if (card != null) {
            nextStatus(card.getWord());
            updateCardCash();
        }
        return getUserCards().size() >= 1 ? getUserCards().get(0) : CardDto.EMPTY;
    }

    @Transactional(readOnly = true)
    public List<Word> getAll() {
        return wordRepository.findAll();
    }

    private List<CardDto> getUserCards() {
        return cardCash.get("andrey759");
    }
    private void nextStatus(String wordValue) {
        wordRepository.findByText(wordValue)
                .map(word -> {
                    WordStatus newStatus = word.getStatus().nextStatus();
                    word.setStatus(newStatus);
                    word.setUpdatedAt(LocalDateTime.now());
                    word.setReadyAt(newStatus.readyAt());
                    return word;
                })
                .map(wordRepository::save);
    }
}
