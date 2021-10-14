package im.denz.jsonlogs;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class KeyCountResult {
    public static String NO_EXT = "no_extension";

    protected Map<String, Set<String>> extKeys;

    public KeyCountResult(){
        extKeys = Collections.synchronizedMap(new HashMap<>());
    }

    public static String getExtension(String keyName){
        String[] splits = keyName.split("\\.");
        if(splits.length > 1){
            return splits[splits.length-1];
        }
        return NO_EXT;
    }

    public synchronized void add(String key){
        String ext = getExtension(key);
        if(!extKeys.containsKey(ext)){
            extKeys.put(ext, Collections.synchronizedSet(new HashSet<>()));
        }
        extKeys.get(ext).add(key);
    }

    public Long getExtCount(String ext){
        if(extKeys.containsKey(ext)){
            return (long)extKeys.get(ext).size();
        }
        return 0L;
    }

    public Map<String, Long> getExtCount(){
        return extKeys.entrySet().parallelStream().collect(Collectors.toMap(Map.Entry::getKey, e -> (long)e.getValue().size()));
    }

}
