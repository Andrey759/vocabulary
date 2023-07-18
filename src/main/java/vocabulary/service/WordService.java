package vocabulary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.controller.dto.CardDto;
import vocabulary.controller.enums.AddedOrReset;
import vocabulary.entity.User;
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
    public AddedOrReset addOrReset(String word) {
        Optional<Card> cardOptional = wordRepository.findByUsernameAndWord(User.getCurrent(), word);
        if (cardOptional.isPresent()) {
            cardOptional.get().setStatus(LEARNING);
            wordRepository.save(cardOptional.get());
            return RESET;
        }
        wordRepository.save(Card.create(word));
        return ADDED;
    }

    @Transactional
    public void delete(String word) {
        wordRepository.deleteByUsernameAndWord(User.getCurrent(), word);
    }

    @Transactional
    public CardDto reset(String word) {
        wordRepository.findByUsernameAndWord(User.getCurrent(), word)
                .map(this::fillFieldsForReset)
                .map(wordRepository::save);
        return getCardDtoToRepeat();
    }

    @Transactional
    public CardDto another(String word) {
        return wordRepository.findByUsernameAndWord(User.getCurrent(), word)
                .map(chatGptService::sendAndParseCard)
                .map(wordRepository::save)
                .map(CardDto::from)
                .orElse(EMPTY);
    }

    @Transactional
    public CardDto next(String word) {
        wordRepository.findByUsernameAndWord(User.getCurrent(), word)
                .map(this::fillFieldsForNextStatus)
                .map(wordRepository::save);
        return getCardDtoToRepeat();
    }

    @Transactional(readOnly = true)
    public List<Card> findAll() {
        return wordRepository.findAllByUsername(User.getCurrent());
    }


    private CardDto getCardDtoToRepeat() {
        return wordRepository.findByUsernameAndReadyAtLessThan(User.getCurrent(), LocalDateTime.now())
                .map(CardDto::from)
                .orElse(EMPTY);
    }

    private Card fillFieldsForReset(Card card) {
        card.setSentence(null);
        card.setSentenceHtml(null);
        card.setExplanationHtml(null);
        card.setTranslationHtml(null);
        card.setStatus(LEARNING);
        card.setReadyAt(LEARNING.readyAt());
        return card;
    }

    private Card fillFieldsForNextStatus(Card card) {
        CardStatus newStatus = card.getStatus().nextStatus();
        card.setSentence(null);
        card.setSentenceHtml(null);
        card.setExplanationHtml(null);
        card.setTranslationHtml(null);
        card.setStatus(newStatus);
        card.setReadyAt(newStatus.readyAt());
        return card;
    }
}
