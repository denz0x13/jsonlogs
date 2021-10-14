package im.denz.jsonlogs;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TestHelper {
    public static final ThreadLocalRandom random = ThreadLocalRandom.current();

    static EasyRandomParameters parameters = new EasyRandomParameters()
            .seed(123L)
            .objectPoolSize(100)
            .randomizationDepth(2)
            .charset(StandardCharsets.UTF_8)
            .stringLengthRange(5, 50)
            .collectionSizeRange(5, 10)
            .scanClasspathForConcreteTypes(true)
            .overrideDefaultInitialization(false)
            .ignoreRandomizationErrors(true);

    static EasyRandom easyRandom = new EasyRandom(parameters);

    public static long nextInt(int bound) {
        return random.nextInt(bound);
    }

    public static int nextUInt16(int bound){
        bound = Math.abs(bound);
        if(bound == 0) bound = 1;
        if(bound >= 0xFFFF) bound = 0xFFFE;
        return Math.abs(random.nextInt(bound));
    }

    public static <T> T randomObject(Class<T> klazz) {
        return easyRandom.nextObject(klazz);
    }

    public static String randomString(){
        return easyRandom.nextObject(String.class);
    }
    public static String randomFileName(){
        return String.format("%s.%s",randomString(),randomString().substring(3));
    }

    public static <T> List<T> randomObjectList(Class<T> klazz, int maxSize) {
        return IntStream.range(0, maxSize).mapToObj(i -> easyRandom.nextObject(klazz)).collect(Collectors.toList());
    }

    public static String getFixturePath(String name) throws Exception{
        URL url = TestHelper.class.getClassLoader().getResource(String.format("fixtures/%s",name));
        if(Objects.isNull(url)){
            throw new IOException(String.format("fixture not found %s",name));
        }
        return url.getPath();
    }

}
