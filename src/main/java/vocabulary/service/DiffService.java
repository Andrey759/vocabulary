package vocabulary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;
import vocabulary.service.dto.ChunkDto;
import vocabulary.service.dto.DiffDto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.micrometer.common.util.StringUtils.isEmpty;
import static io.micrometer.common.util.StringUtils.isNotEmpty;
import static java.util.Arrays.asList;
import static vocabulary.service.enums.ChunkType.equal;

@Service
@Slf4j
@Deprecated
public class DiffService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Function<String, String> FORMAT_RM = str -> String.format("<span class=\"rm\">%s</span>", str);
    private static final Function<String, String> FORMAT_INS = str -> String.format("<span class=\"ins\">%s</span>", str);

    public String calculateCorrectedHtml(String source, String corrected) {
        DiffDto diff = execute(source, corrected);
        if (diff == null) {
            log.info("Emails for diffchecker.com are over");
            return corrected;
        }
        List<ChunkDto> leftChunks = diff.getRows().get(0).getLeft().getChunks();
        List<ChunkDto> rightChunks = diff.getRows().get(0).getRight().getChunks();

        String[] parts = new String[leftChunks.size() * 2];
        Arrays.fill(parts, "");

        addChunks(parts, leftChunks);
        addInsChunksWithFormat(parts, rightChunks);

        return Arrays.stream(parts)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }

    private void addChunks(String[] parts, List<ChunkDto> chunks) {
        int index = 0;
        for(ChunkDto chunk : chunks) {
            if (chunk.getType() == equal) {
                parts[index + 1] = chunk.getValue().trim();
                index += 2;
            } else {
                parts[index] = chunk.getValue().trim();
            }
        }
    }

    private void addInsChunksWithFormat(String[] parts, List<ChunkDto> chunks) {
        int index = 0;
        for(ChunkDto chunk : chunks) {
            if (chunk.getType() == equal) {
                parts[index + 1] = chunk.getValue().trim();
                index += 2;
            } else {
                parts[index] = format(parts[index], chunk.getValue().trim());
            }
        }
    }

    private String format(String rm, String ins) {
        if (isEmpty(rm) && isEmpty(ins)) {
            return "";
        }
        String commonCenter = commonCenter(rm, ins);
        if (commonCenter.length() >= 2) {
            rm = rm.substring(0, rm.length() - commonCenter.length());
            ins = ins.substring(commonCenter.length());
            return (rm.startsWith(" ") ? " " : "")
                    + (isNotEmpty(rm) ? (asList(".", "?", "!").contains(rm.trim()) ? rm.trim() : FORMAT_RM.apply(rm.trim())) : "")
                    + (rm.endsWith(" ") ? " " : "")
                    + commonCenter
                    + (ins.startsWith(" ") ? " " : "")
                    + (isNotEmpty(ins) ? (asList(".", "?", "!").contains(ins.trim()) ? ins.trim() : FORMAT_INS.apply(ins.trim())) : "")
                    + (ins.endsWith(" ") ? " " : "");
        }

        int commonCharsInTheBeginning = commonCharsInTheBeginning(rm, ins);
        int commonCharsInTheEnding = commonCharsInTheEnding(rm, ins);
        String commonBeginning = rm.substring(0, commonCharsInTheBeginning);
        String commonEnding = rm.substring(rm.length() - commonCharsInTheEnding);
        String rmPart = rm.substring(commonCharsInTheBeginning, rm.length() - commonCharsInTheEnding);
        String insPart = ins.substring(commonCharsInTheBeginning, ins.length() - commonCharsInTheEnding);

        if ((commonCharsInTheBeginning + commonCharsInTheEnding) * 2 <= rmPart.length() + insPart.length()) {
            return (isNotEmpty(rm) ? (rm.startsWith(" ") ? " " + FORMAT_RM.apply(rm.trim()) : FORMAT_RM.apply(rm)) : "")
                    + (isNotEmpty(rm) && isNotEmpty(ins) ? " " : "")
                    + (isNotEmpty(ins) ? (asList(".", "?", "!").contains(ins) ? ins : FORMAT_INS.apply(ins)) : "");
        }
        return commonBeginning
                + (isNotEmpty(rmPart) ? (rmPart.startsWith(" ") ? " " + FORMAT_RM.apply(rmPart.trim()) : FORMAT_RM.apply(rmPart)) : "")
                + (isNotEmpty(rmPart) && isNotEmpty(insPart) && commonCharsInTheBeginning == 0 && commonCharsInTheEnding == 0 ? " " : "")
                + (isNotEmpty(insPart) ? (asList(".", "?", "!").contains(insPart) ? insPart : FORMAT_INS.apply(insPart)) : "")
                + commonEnding;
    }

    private String commonCenter(String rm, String ins) {
        for (int i = 0; i < rm.length() - 1; i++) {
            String common = rm.substring(i);
            if (ins.startsWith(common)) {
                return common;
            }
        }
        return "";
    }

    private int commonCharsInTheBeginning(String rm, String ins) {
        int min = Math.min(rm.length(), ins.length());
        for (int i = 0; i < min; i++) {
            if (rm.charAt(i) != ins.charAt(i)) {
                return i;
            }
        }
        return min;
    }

    private int commonCharsInTheEnding(String rm, String ins) {
        int min = Math.min(rm.length(), ins.length());
        for (int i = 0; i < min; i++) {
            if (rm.charAt(rm.length() - i - 1) != ins.charAt(ins.length() - i - 1)) {
                return i;
            }
        }
        return min;
    }

    private final List<String> EMAIL_LIST = IntStream.range(0, 100)
            .mapToObj(i -> String.format("test%d@mail.ru", i)).toList();
    public final AtomicInteger INDEX = new AtomicInteger(0);
    private DiffDto execute(String left, String right) {
        DiffDto result;
        while ((result = executeForEmail(left, right, EMAIL_LIST.get(INDEX.get()))) == null && INDEX.get() < EMAIL_LIST.size() - 1) {
            INDEX.incrementAndGet();
            log.info("Email for diffchecker.com changed to {}", EMAIL_LIST.get(INDEX.get()));
        }
        return result;
    }

    @SneakyThrows
    private DiffDto executeForEmail(String left, String right, String email) {
        // TODO: email list
        // arebrov89@gmail.com
        HttpPost httpPost = new HttpPost("https://api.diffchecker.com/public/text?output_type=json&email=" + email);
        //httpPost.addHeader("User-Agent", "");

        //List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        //urlParameters.add(new BasicNameValuePair("userName", "Pankaj Kumar"));

        Map<String, Object> map = Map.of("left", left, "right", right);
        HttpEntity httpEntity = new StringEntity(OBJECT_MAPPER.writeValueAsString(map), ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);
        // TODO: remove
//        try (CloseableHttpClient client = HttpClients.createDefault();
//             CloseableHttpResponse response = client.execute(httpPost)
//        ) {
//            String content = EntityUtils.toString(response.getEntity());
//            log.info(content);
//            return content.contains("FREE_DIFF_LIMIT_EXCEEDED")
//                    ? null
//                    : OBJECT_MAPPER.readValue(content, DiffDto.class);
//        }
        return new DiffDto();
    }
}
