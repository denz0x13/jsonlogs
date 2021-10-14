package im.denz.jsonlogs;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KeyCountResult {
    public static String NO_EXT = "no_extension";
    @Getter
    protected Map<String,Long> keyCount;
    @Getter
    protected Map<String,Long> extCount;
    public KeyCountResult(){
        keyCount = Collections.synchronizedMap(new HashMap<>());
        extCount = Collections.synchronizedMap(new HashMap<>());
    }

    public static String getExtension(String keyName){
        String[] splits = keyName.split("\\.");
        if(splits.length > 1){
            return splits[splits.length-1];
        }
        return NO_EXT;
    }

    public synchronized void add(String key){
        if(keyCount.containsKey(key)){
            keyCount.put(key,keyCount.get(key)+1);
            return;
        }
        keyCount.put(key,1L);
        String ext = getExtension(key);
        if(extCount.containsKey(ext)){
            extCount.put(ext, extCount.get(ext)+1);
            return;
        }
        extCount.put(ext,1L);
    }

    public Long getExtCount(String ext){
        if(extCount.containsKey(ext)){
            return extCount.get(ext);
        }
        return 0L;
    }

}
