package im.denz.jsonlogs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JsonLogProcess {

    public static final String KEY_FILED = "nm";

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static KeyCountResult parseFile(String fileName) {
        KeyCountResult result = new KeyCountResult();
        try (Stream<String> is = Files.lines(Paths.get(fileName))) {
            is.forEach(line -> {
                try {
                    Map<String, String> jsonMap = objectMapper.readValue(line, new TypeReference<Map<String, String>>() {
                    });
                    if (jsonMap.containsKey(KEY_FILED)) {
                        result.add(jsonMap.get(KEY_FILED));
                    }
                } catch (JsonProcessingException ex) {
                    log.debug(ex.getMessage());
                }
            });
        } catch (IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }
        return result;
    }
}
