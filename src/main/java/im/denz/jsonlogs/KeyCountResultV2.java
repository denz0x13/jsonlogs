package im.denz.jsonlogs;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class KeyCountResultV2 {
    public static String NO_EXT = "no_extension";

    protected Map<Integer,Map<String, Set<String>>> extKeys;

    public KeyCountResultV2(){
        extKeys = Collections.synchronizedMap(new HashMap<>());
    }

    public static String getExtension(String keyName){
        String[] splits = keyName.split("\\.");
        if(splits.length > 1){
            return splits[splits.length-1];
        }
        return NO_EXT;
    }

    public synchronized void add(JsonLogEntry entry){
        String ext = getExtension(entry.getNm());
        Integer hour = Instant.ofEpochSecond(entry.getTs()).atZone(ZoneId.of("UTC")).getHour();
        if(!extKeys.containsKey(hour)){
            extKeys.put(hour, Collections.synchronizedMap(new HashMap<>()));
        }
        if(!extKeys.get(hour).containsKey(ext)){
            extKeys.get(hour).put(ext, Collections.synchronizedSet(new HashSet<>()));
        }
        extKeys.get(hour).get(ext).add(entry.getNm());
    }


    public Map<Integer,Map<String, Long>> getExtCount(){
        Map<Integer,Map<String, Long>> result = new HashMap<>();
        extKeys.forEach((k,v) -> {
            result.put(k, v.entrySet().parallelStream().collect(Collectors.toMap(Map.Entry::getKey, e -> (long)e.getValue().size())));
        });
        return result;
    }

}
