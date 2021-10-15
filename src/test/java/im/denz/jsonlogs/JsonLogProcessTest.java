package im.denz.jsonlogs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonLogProcessTest {


    @Test
    void parseFile_L3_Test() throws Exception{
        String fileName = "logs3.txt";
        KeyCountResult result = JsonLogProcess.parseFile(TestHelper.getFixturePath(fileName));
        assertEquals(1,result.getExtCount("pdf"));
        assertEquals(1,result.getExtCount("ext"));
    }

    @Test
    void parseFile_L5_Test() throws Exception{
        String fileName = "logs5.txt";
        KeyCountResult result = JsonLogProcess.parseFile(TestHelper.getFixturePath(fileName));
        assertEquals(2,result.getExtCount("pdf"));
        assertEquals(2,result.getExtCount("ext"));
    }

    @Test
    void parseFile_L3_RX_Test() throws Exception{
        String fileName = "logs3.txt";
        KeyCountResult result = JsonLogProcess.parseFileRx(TestHelper.getFixturePath(fileName));
        assertEquals(1,result.getExtCount("pdf"));
        assertEquals(1,result.getExtCount("ext"));
    }

    @Test
    void parseFile_L5_RX_Test() throws Exception{
        String fileName = "logs5.txt";
        KeyCountResult result = JsonLogProcess.parseFileRx(TestHelper.getFixturePath(fileName));
        assertEquals(2,result.getExtCount("pdf"));
        assertEquals(2,result.getExtCount("ext"));
    }
}