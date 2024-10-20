package vocabulary.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiffServiceTest {
    private final DiffService diffService = new DiffService();

    @Test
    @Disabled
    public void test1() {
        String actual = diffService.calculateCorrectedHtml(
                "He is a doctor", "He is the doctor");
        assertEquals("He is <span class=\"rm\">a</span> <span class=\"ins\">the</span> doctor",
                actual);
    }

    @Test
    @Disabled
    public void test2() {
        String actual = diffService.calculateCorrectedHtml(
                "They are doctor", "They are doctors");
        assertEquals("They are doctor<span class=\"ins\">s</span>",
                actual);
    }

    @Test
    @Disabled
    public void test3() {
        String actual = diffService.calculateCorrectedHtml(
                "They are doctors", "They are doctor");
        assertEquals("They are doctor<span class=\"rm\">s</span>",
                actual);
    }

    @Test
    @Disabled
    public void test4() {
        String actual = diffService.calculateCorrectedHtml(
                "They ares doctor", "They are doctors");
        assertEquals("They <span class=\"rm\">ares doctor</span> <span class=\"ins\">are doctors</span>",
                actual);
    }

    @Test
    @Disabled
    public void test5() {
        String actual = diffService.calculateCorrectedHtml(
                "They are doctors", "They are the doctors");
        assertEquals("They are <span class=\"ins\">the</span> doctors",
                actual);
    }

    @Test
    @Disabled
    public void test6() {
        String actual = diffService.calculateCorrectedHtml(
                "Can you tell me about geographi?", "Can you tell me about geography?");
        assertEquals("Can you tell me about geograph<span class=\"rm\">i</span><span class=\"ins\">y</span>?",
                actual);
    }

    @Test
    @Disabled
    public void test7() {
        String actual = diffService.calculateCorrectedHtml(
                "Could you please tell me about geography", "Could you please tell me about geography?");
        assertEquals("Could you please tell me about geography?",
                actual);
    }

    @Test
    @Disabled
    public void test8() {
        String actual = diffService.calculateCorrectedHtml(
                "Could you tell me about geography", "Can you tell me about geography");
        assertEquals("<span class=\"rm\">Could</span> <span class=\"ins\">Can</span> you tell me about geography",
                actual);
    }

    @Test
    @Disabled
    public void test9() {
        String actual = diffService.calculateCorrectedHtml(
                "Tell me the most popular capitals.", "Tell me which capitals are the most popular.");
        assertEquals("Tell me <span class=\"ins\">which capitals are</span> the " +
                "most popular <span class=\"rm\">capitals</span>.",
                actual);
    }

    @Test
    @Disabled
    public void test10() {
        String actual = diffService.calculateCorrectedHtml(
                "What you can tell me about living in London?",
                "What can you tell me about living in London?");
        assertEquals("What <span class=\"rm\">you</span> can" +
                        " <span class=\"ins\">you</span> tell me about living in London?",
                actual);
    }
}
