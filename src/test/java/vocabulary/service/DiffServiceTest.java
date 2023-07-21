package vocabulary.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiffServiceTest {
    private final DiffService diffService = new DiffService();

    @Test
    public void test1() {
        String actual = diffService.calculateCorrectedHtml("He is a doctor", "He is the doctor");
        assertEquals("He is <span class=\"rm\">a</span> <span class=\"ins\">the</span> doctor", actual);
    }

    @Test
    public void test2() {
        String actual = diffService.calculateCorrectedHtml("They are doctor", "They are doctors");
        assertEquals("They are doctor<span class=\"ins\">s</span>", actual);
    }

    @Test
    public void test3() {
        String actual = diffService.calculateCorrectedHtml("They are doctors", "They are doctor");
        assertEquals("They are doctor<span class=\"rm\">s</span>", actual);
    }

    @Test
    public void test4() {
        String actual = diffService.calculateCorrectedHtml("They ares doctor", "They are doctors");
        assertEquals("They are<span class=\"rm\">s doctor</span><span class=\"ins\"> doctors</span>", actual);
    }

    @Test
    public void test5() {
        String actual = diffService.calculateCorrectedHtml("They are doctors", "They are the doctors");
        assertEquals("They are <span class=\"ins\">the</span> doctors", actual);
    }

    @Test
    public void test6() {
        String actual = diffService.calculateCorrectedHtml("Can you tell me about geographi?", "Can you tell me about geography?");
        assertEquals("Can you tell me about geograph<span class=\"rm\">i</span><span class=\"ins\">y</span>?", actual);
    }
}
