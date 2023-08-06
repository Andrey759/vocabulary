package vocabulary.service;

import org.junit.jupiter.api.Test;
import vocabulary.controller.dto.CardDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParsingServiceTest {
    private final ParsingService parsingService = new ParsingService(new DiffService());

    @Test
    public void test1() {
        String source = "1. She added just a smidge of salt to the soup to enhance its flavor.\n" +
                "2. She added just a <b>smidge</b> of salt to the soup to enhance its flavor.\n" +
                "a very small, tiny or insignificant amount.\n" +
                "4. Она добавила всего лишь немного соли в суп, чтобы усилить его вкус.";

        CardDto cardDto = parsingService.parseCardDto("smidge", source);

        assertEquals(cardDto.getSentence(),
                "She added just a smidge of salt to the soup to enhance its flavor.");
    }
}
