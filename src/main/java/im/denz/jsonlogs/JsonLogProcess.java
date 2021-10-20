package im.denz.jsonlogs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Slf4j
public class JsonLogProcess {

    public static final int FLUX_WINDOWS_SIZE = 100;

    public static final String KEY_FILED = "nm";

    public static final ObjectMapper objectMapper = defaultObjectMapper();

    public static ObjectMapper defaultObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        objectMapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.setDateFormat(new StdDateFormat());
        return objectMapper;
    }

    public static final ForkJoinPool pool = ExecutorConfig.factoryPool(ExecutorConfig.MIN_THREADS_COUNT, "json-logs");

    public static KeyCountResult parseFile(String fileName) {
        AtomicReference<KeyCountResult> result = new AtomicReference<>(new KeyCountResult());
        try (Stream<String> is = Files.lines(Paths.get(fileName))) {
            is.map(line -> CompletableFuture.runAsync(() -> {
                try {
                    Map<String, String> jsonMap = objectMapper.readValue(line, new TypeReference<Map<String, String>>() {
                    });
                    if (jsonMap.containsKey(KEY_FILED)) {
                        result.get().add(jsonMap.get(KEY_FILED));
                    }
                } catch (JsonProcessingException ex) {
                    log.debug(ex.getMessage());
                }
            }, pool)).forEach(CompletableFuture::join);
        } catch (IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }
        return result.get();
    }

    public static KeyCountResultV2 parseFileV2(String fileName) {
        AtomicReference<KeyCountResultV2> result = new AtomicReference<>(new KeyCountResultV2());
        try (Stream<String> is = Files.lines(Paths.get(fileName))) {
            is.map(line -> CompletableFuture.runAsync(() -> {
                try {
                    JsonLogEntry entry = objectMapper.readValue(line, JsonLogEntry.class);
                    result.get().add(entry);
                } catch (JsonProcessingException ex) {
                    log.error(ex.getMessage());
                }
            }, pool)).forEach(CompletableFuture::join);
        } catch (IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }
        return result.get();
    }

    public static KeyCountResult parseFileRx(String fileName) {
        AtomicReference<KeyCountResult> result = new AtomicReference<>(new KeyCountResult());

        Path path = Paths.get(fileName);
        Scheduler scheduler = Schedulers.fromExecutor(pool);
        Flux.using(
                        () -> Files.lines(path),
                        Flux::fromStream,
                        Stream::close
                )
                .doOnError(ex -> log.debug(ex.getMessage(),ex))
                .window(FLUX_WINDOWS_SIZE)
                .flatMap(w -> w.reduce(new ArrayList<String>(), (a, b) -> {
                    a.add(b);
                    return a;
                }))
                .publishOn(scheduler)
                .doOnNext(list -> {
                    list.forEach(line -> {
                        try {
                            Map<String, String> jsonMap = objectMapper.readValue(line, new TypeReference<Map<String, String>>() {
                            });
                            if (jsonMap.containsKey(KEY_FILED)) {
                                result.get().add(jsonMap.get(KEY_FILED));
                            }
                        } catch (JsonProcessingException ex) {
                            log.debug(ex.getMessage());
                        }
                    });

                }).then().block();

        return result.get();
    }
}
