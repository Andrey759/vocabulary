package vocabulary.service;

import org.junit.jupiter.api.Test;
import vocabulary.entity.Card;
import vocabulary.entity.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParsingServiceTest {
    private final ParsingService parsingService = new ParsingService(new DiffService());

    @Test
    public void test1() {
        String response = "1. She added just a smidge of salt to the soup to enhance its flavor.\n" +
                "2. She added just a <b>smidge</b> of salt to the soup to enhance its flavor.\n" +
                "a very small, tiny or insignificant amount.\n" +
                "4. Она добавила всего лишь немного соли в суп, чтобы усилить его вкус.";

        Card card = new Card();
        card.setResponse(response);
        parsingService.parseAndFillCard(card);

        assertEquals(card.getSentence(),
                "She added just a smidge of salt to the soup to enhance its flavor.");
    }

    @Test
    public void test2() {
        String newMessage = "What you can tell me about living in London?";
        String response = "1 - Grammar in my message: 8\n" +
                "2 - Answer: Living in London is a wonderful experience! It's a vibrant city with a rich history, diverse culture, and endless opportunities for work and entertainment.\n" +
                "3 - Correction: 'What can you tell me about living in London?'\n" +
                "4 - Perfect correction: 'What can you tell me about what it's like to live in London?'";

        Card card = new Card();
        card.setResponse(response);
        List<Message> messages = parsingService.parseMessages("user", newMessage, response);
        String actual = messages.get(0).getCorrectedHtml();

        assertEquals("What <span class=\"rm\">you</span> can" +
                        " <span class=\"ins\">you</span> tell me about living in London?",
                actual);
    }
}
