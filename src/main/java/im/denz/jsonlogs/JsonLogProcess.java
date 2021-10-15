package im.denz.jsonlogs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static final ObjectMapper objectMapper = new ObjectMapper();

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

    public static KeyCountResult parseFileRx(String fileName) {
        AtomicReference<KeyCountResult> result = new AtomicReference<>(new KeyCountResult());

        Path path = Paths.get(fileName);
        Scheduler scheduler = Schedulers.fromExecutor(pool);
        Flux.using(
                        () -> Files.lines(path),
                        Flux::fromStream,
                        Stream::close
                )
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
