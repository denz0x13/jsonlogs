package im.denz.jsonlogs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonLogProcessTest {


    @Test
    void parseFile_L3_Test() throws Exception{
        String fileName = "logs3.txt";
        KeyCountResult result = JsonLogProcess.parseFile(TestHelper.getFixturePath(fileName));
        assertFalse(result.getKeyCount().isEmpty());
        assertEquals(1,result.getExtCount("pdf"));
        assertEquals(1,result.getExtCount("ext"));
    }

    @Test
    void parseFile_L5_Test() throws Exception{
        String fileName = "logs5.txt";
        KeyCountResult result = JsonLogProcess.parseFile(TestHelper.getFixturePath(fileName));
        assertFalse(result.getKeyCount().isEmpty());
        assertEquals(2,result.getExtCount("pdf"));
        assertEquals(2,result.getExtCount("ext"));
    }
}