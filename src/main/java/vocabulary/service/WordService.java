package vocabulary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.controller.dto.CardDto;
import vocabulary.controller.enums.AddedOrReset;
import vocabulary.entity.Card;
import vocabulary.entity.enums.CardStatus;
import vocabulary.repository.WordRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static vocabulary.controller.dto.CardDto.EMPTY;
import static vocabulary.controller.enums.AddedOrReset.ADDED;
import static vocabulary.controller.enums.AddedOrReset.RESET;
import static vocabulary.entity.enums.CardStatus.LEARNING;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordService {
    private final WordRepository wordRepository;
    private final ChatGptService chatGptService;

    @Transactional
    public void updateAllEmptyFromChatGpt() {
        wordRepository.findAllBySentenceIsNull()
                .forEach(chatGptService::sendAndParseCard);
    }

    @Transactional
    public AddedOrReset addOrReset(String username, String word) {
        Optional<Card> cardOptional = wordRepository.findByUsernameAndWord(username, word);
        if (cardOptional.isPresent()) {
            cardOptional.get().setStatus(LEARNING);
            wordRepository.save(cardOptional.get());
            return RESET;
        }
        wordRepository.save(Card.create(username, word));
        return ADDED;
    }

    @Transactional
    public void delete(String username, String word) {
        wordRepository.deleteByUsernameAndWord(username, word);
    }

    @Transactional
    public CardDto reset(String username, String word) {
        wordRepository.findByUsernameAndWord(username, word)
                .map(this::fillFieldsForReset)
                .map(wordRepository::save);
        return getCardDtoToRepeat(username);
    }

    @Transactional
    public CardDto another(String username, String word) {
        return wordRepository.findByUsernameAndWord(username, word)
                .map(chatGptService::sendAndParseCard)
                .map(wordRepository::save)
                .map(CardDto::from)
                .orElse(EMPTY);
    }

    @Transactional
    public CardDto next(String username, String word) {
        wordRepository.findByUsernameAndWord(username, word)
                .map(this::fillFieldsForNextStatus)
                .map(wordRepository::save);
        return getCardDtoToRepeat(username);
    }

    @Transactional(readOnly = true)
    public List<Card> findAll(String username) {
        return wordRepository.findAllByUsername(username);
    }

    @Transactional
    public CardDto changeStatusAndGetCardDto(String username, String word, CardStatus status) {
        wordRepository.findByUsernameAndWord(username, word)
                .map(card -> {
                    card.setStatus(status);
                    card.setReadyAt(status.readyAt(card.getUpdatedAt()));
                    return card;
                })
                .ifPresent(wordRepository::save);
        return getCardDtoToRepeat(username);
    }


    private CardDto getCardDtoToRepeat(String username) {
        return wordRepository.findByUsernameAndReadyAtLessThanOrderByRepeatOrderAsc(username, LocalDateTime.now())
                .stream()
                .findFirst()
                .map(CardDto::from)
                .orElse(EMPTY);
    }

    private Card fillFieldsForReset(Card card) {
        LocalDateTime updatedAt = LocalDateTime.now();
        card.setSentence(null);
        card.setSentenceHtml(null);
        card.setExplanationHtml(null);
        card.setTranslationHtml(null);
        card.setStatus(LEARNING);
        card.setUpdatedAt(updatedAt);
        card.setReadyAt(LEARNING.readyAt(updatedAt));
        card.randomOrder();
        return card;
    }

    private Card fillFieldsForNextStatus(Card card) {
        LocalDateTime updatedAt = LocalDateTime.now();
        CardStatus newStatus = card.getStatus().nextStatus();
        card.setSentence(null);
        card.setSentenceHtml(null);
        card.setExplanationHtml(null);
        card.setTranslationHtml(null);
        card.setStatus(newStatus);
        card.setUpdatedAt(updatedAt);
        card.setReadyAt(newStatus.readyAt(updatedAt));
        card.randomOrder();
        return card;
    }
}
