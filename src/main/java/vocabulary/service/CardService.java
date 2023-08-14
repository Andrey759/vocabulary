package vocabulary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabulary.controller.dto.CardDto;
import vocabulary.controller.dto.DictDto;
import vocabulary.entity.Card;
import vocabulary.entity.enums.CardStatus;
import vocabulary.repository.CardRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static vocabulary.entity.enums.CardStatus.LEARNING;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;
    private final ChatGptService chatGptService;

    @Transactional
    public void updateAllEmptyFromChatGpt() {
        cardRepository.findAllBySentenceIsNull()
                .forEach(chatGptService::sendAndParseCardWithValidation);
    }

    @Transactional
    public boolean addOrReset(String username, String word) {
        Optional<Card> cardOptional = cardRepository.findByUsernameAndWord(username, word);
        if (cardOptional.isPresent()) {
            Card card = cardOptional.get();
            card.setStatus(LEARNING);
            card.setReadyAt(LocalDateTime.now());
            cardRepository.save(card);
            return false;
        }
        cardRepository.save(Card.create(username, word));
        return true;
    }

    @Transactional
    public CardDto deleteAndGetCardDto(String username, String word) {
        cardRepository.deleteByUsernameAndWord(username, word);
        return getCardDtoToRepeat(username);
    }

    @Transactional
    public CardDto reset(String username, String word) {
        cardRepository.findByUsernameAndWord(username, word)
                .map(this::fillFieldsForReset)
                .map(cardRepository::save);
        return getCardDtoToRepeat(username);
    }

    @Transactional
    public CardDto another(String username, String word) {
        return cardRepository.findByUsernameAndWord(username, word)
                .map(chatGptService::sendAndParseCardWithValidation)
                .map(cardRepository::save)
                .map(card -> CardDto.from(card, null, null))
                .orElse(CardDto.empty(null, null));
    }

    @Transactional
    public CardDto next(String username, String word) {
        cardRepository.findByUsernameAndWord(username, word)
                .map(this::fillFieldsForNextStatus)
                .map(cardRepository::save);
        return getCardDtoToRepeat(username);
    }

    @Transactional(readOnly = true)
    public List<DictDto> findAll(String username) {
        return cardRepository.findAllByUsernameOrderByCreatedAtAsc(username)
                .stream()
                .map(DictDto::from)
                .toList();
    }

    @Transactional
    public CardDto changeStatusAndGetCardDto(String username, String word, CardStatus status) {
        cardRepository.findByUsernameAndWord(username, word)
                .map(card -> {
                    card.setStatus(status);
                    card.setReadyAt(status.readyAt(card.getUpdatedAt()));
                    return card;
                })
                .ifPresent(cardRepository::save);
        return getCardDtoToRepeat(username);
    }


    public CardDto getCardDtoToRepeat(String username) {
        Optional<Card> cardOptional = cardRepository.findCardToRepeat(username, LocalDateTime.now());
        long finishedToday = cardRepository.countByUsernameAndUpdatedAtGreaterThanAndUpdatedAtLessThan(
                username, LocalDateTime.now().minusHours(10L), LocalDateTime.now());
        long toRepeat = cardRepository.countCardsToRepeat(username, LocalDateTime.now());
        long totalElements = finishedToday + toRepeat;
        return cardOptional
                .map(card -> CardDto.from(card, finishedToday, totalElements))
                .orElse(CardDto.empty(finishedToday, totalElements));
    }

    private Card fillFieldsForReset(Card card) {
        LocalDateTime now = LocalDateTime.now();
        card.setResponse(null);
        card.setSentence(null);
        card.setSentenceHtml(null);
        card.setExplanationHtml(null);
        card.setTranslationHtml(null);
        card.setStatus(LEARNING);
        card.setUpdatedAt(now);
        card.setReadyAt(LEARNING.readyAt(now));
        card.randomOrder();
        return card;
    }

    private Card fillFieldsForNextStatus(Card card) {
        LocalDateTime now = LocalDateTime.now();
        CardStatus newStatus = card.getStatus().nextStatus();
        card.setResponse(null);
        card.setSentence(null);
        card.setSentenceHtml(null);
        card.setExplanationHtml(null);
        card.setTranslationHtml(null);
        card.setStatus(newStatus);
        card.setUpdatedAt(now);
        card.setReadyAt(newStatus.readyAt(now));
        card.randomOrder();
        return card;
    }
}
