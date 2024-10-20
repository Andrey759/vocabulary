package vocabulary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vocabulary.entity.Card;
import vocabulary.entity.Message;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static vocabulary.controller.enums.MessageOwner.BOT;
import static vocabulary.controller.enums.MessageOwner.USER;

@Service
@RequiredArgsConstructor
public class ParsingService {
    private final DiffService diffService;

    public Card parseAndFillCard(Card card) {
        List<String> lines = receiveLines(card.getResponse());

        String sentence = lines.get(0);
        String sentenceHtml = lines.get(1);
        String explanationHtml = lines.get(2);
        String translationHtml = lines.get(3);
        if (sentence.contains(card.getWord())) {
            sentenceHtml = sentence.replaceAll(card.getWord(), "<b>" + card.getWord() + "</b>");
        }
        if (explanationHtml.contains("means")) {
            explanationHtml = explanationHtml.split("means")[1].trim();
            explanationHtml = explanationHtml.substring(0, 1).toUpperCase() + explanationHtml.substring(1);
        }

        card.setSentence(sentence);
        card.setSentenceHtml(sentenceHtml);
        card.setExplanationHtml(explanationHtml);
        card.setTranslationHtml(translationHtml);
        return card;
    }

    public List<Message> parseMessages(String username, String newMessage, String response) {
        List<String> lines = receiveLines(response);

        int mark = Integer.parseInt(lines.get(0).split("/")[0].replaceAll("\\D+", ""));
        if (mark > 10) {
            mark = mark / 100;
        }
        String answer = lines.get(1);
        String corrected = lines.get(2);
        String perfect = lines.get(3);
        //String correctedHtml = diffService.calculateCorrectedHtml(newMessage, corrected);

        Message userMessageDto = new Message(
                null,
                username,
                USER,
                mark,
                newMessage,
                response,
                corrected,
                null,
                perfect,
                LocalDateTime.now()
        );
        Message botMessageDto = new Message(
                null,
                username,
                BOT,
                0,
                "",
                response,
                answer,
                answer,
                "",
                LocalDateTime.now()
        );

        return List.of(userMessageDto, botMessageDto);
    }

    public List<String> receiveLines(String response) {
        return Arrays.stream(response.split("\n"))
                .map(str -> str.matches("^[0-9] -.+") ? str.substring(3) : str)
                .map(str -> str.matches("^[0-9]-.+") ? str.substring(2) : str)
                .map(str -> str.matches("^[0-9]\\..+") ? str.substring(2) : str)
                .map(str -> str.matches("^[0-9]\\).+") ? str.substring(2) : str)
                .map(str -> str.matches("^[0-9]\\.\\).+") ? str.substring(3) : str)
                .map(str -> str.matches("^[0-9]\\. -.+") ? str.substring(4) : str)
                .map(str -> str.matches("^[0-9]\\.-.+") ? str.substring(3) : str)
                .map(str -> str.contains(":") ? str.substring(str.indexOf(":") + 1) : str)
                .map(String::trim)
                .map(str -> str.startsWith("'") && str.endsWith("'") ? str.substring(1, str.length() - 1) : str)
                .map(str -> str.startsWith("\"") && str.endsWith("\"") ? str.substring(1, str.length() - 1) : str)
                .map(str -> str.replaceAll("\"", ""))
                .map(String::trim)
                .toList();
    }
}
