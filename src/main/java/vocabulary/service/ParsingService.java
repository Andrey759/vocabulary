package vocabulary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vocabulary.controller.dto.CardDto;
import vocabulary.entity.Message;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoField.MILLI_OF_DAY;
import static vocabulary.controller.enums.MessageOwner.BOT;
import static vocabulary.controller.enums.MessageOwner.USER;

@Service
@RequiredArgsConstructor
public class ParsingService {
    private final DiffService diffService;

    public CardDto parseCardDto(String username, String word, String response) {
        List<String> lines = receiveLines(response.replaceAll("\n\n", "\n"));

        String sentence = lines.get(0);
        String sentenceHtml = lines.get(1);
        String explanationHtml = lines.get(2);
        String translationHtml = lines.get(3);
        if (explanationHtml.contains("means")) {
            explanationHtml = explanationHtml.split("means")[1].trim();
        }

        return new CardDto(
                word,
                sentence,
                sentenceHtml,
                explanationHtml,
                translationHtml
        );
    }

    public List<Message> parseMessages(String username, String response) {
        List<String> lines = receiveLines(response.replaceAll("\n\n", "\n"));

        int mark = Integer.parseInt(lines.get(0).split("/")[0].replaceAll("\\D+", ""));
        if (mark > 10) {
            mark = mark / 100;
        }
        String answer = lines.get(1);
        String corrected = lines.get(2);
        String perfect = lines.get(3);
        String correctedHtml = diffService.calculateCorrectedHtml(response, corrected);

        Message userMessageDto = new Message(
                LocalDateTime.now().getLong(MILLI_OF_DAY),
                username,
                USER,
                mark,
                response,
                corrected,
                correctedHtml,
                perfect,
                LocalDateTime.now()
        );
        Message botMessageDto = new Message(
                LocalDateTime.now().getLong(MILLI_OF_DAY) + 1,
                username,
                BOT,
                null,
                response,
                answer,
                answer,
                "",
                LocalDateTime.now()
        );

        return List.of(userMessageDto, botMessageDto);
    }

    private List<String> receiveLines(String source) {
        return Arrays.stream(source.split("\n"))
                .map(str -> str.matches("^[0-9] -.+") ? str.substring(3) : str)
                .map(str -> str.matches("^[0-9]-.+") ? str.substring(2) : str)
                .map(str -> str.matches("^[0-9]\\..+") ? str.substring(2) : str)
                .map(str -> str.matches("^[0-9]\\).+") ? str.substring(2) : str)
                .map(str -> str.matches("^[0-9]\\.\\).+") ? str.substring(3) : str)
                .map(str -> str.matches("^[0-9]\\. -.+") ? str.substring(4) : str)
                .map(str -> str.matches("^[0-9]\\.-.+") ? str.substring(3) : str)
                .map(str -> str.contains(":") ? str.substring(str.indexOf(":") + 1) : str)
                .map(str -> str.replaceAll("\"", ""))
                .map(str -> str.replaceAll("'", ""))
                .map(String::trim)
                .toList();
    }
}
