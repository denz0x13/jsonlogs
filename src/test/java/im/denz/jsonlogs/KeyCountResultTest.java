package im.denz.jsonlogs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class KeyCountResultTest {

    List<List<String>> keysSets;
    int setCount;
    KeyCountResult subject;

    @BeforeEach
    void setUp() {
        keysSets = new ArrayList<>();
        setCount = TestHelper.nextUInt16(10)+1;
        IntStream.range(0,setCount).boxed().forEach(i -> {
            String key = TestHelper.randomFileName();
            keysSets.add(Collections.nCopies(TestHelper.nextUInt16(10)+1,key));
        });
        subject = new KeyCountResult();
    }

    @Test
    void add() {
        List<String> keys = keysSets.stream().flatMap(Collection::stream).collect(Collectors.toList());
        Collections.shuffle(keys);
        keys.forEach(subject::add);

        keysSets.forEach(kl -> {
            String key = kl.get(0);
            assertTrue(subject.getKeyCount().containsKey(key));
            assertEquals(kl.size(),subject.getKeyCount().get(key));
            String ext = KeyCountResult.getExtension(key);
            assertEquals(1,subject.getExtCount(ext));
        });

    }
}