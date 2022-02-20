import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ef.DbOperations;
import com.ef.Parser;
import org.junit.jupiter.api.Test;

public class ParserTest {
    private final DbOperations dbo = new DbOperations();
    private final Parser p = new Parser(dbo);

    @Test
    public void commandLineTest() {
        String[] args = {"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300"};
        p.parseCommandLine(args);
        args = new String[]{"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300", "--accesslog=access.log"};
        p.parseCommandLine(args);
        assertTrue(true);
    }

    @Test
    public void dbParamsTest() {
        String[] args = {"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300", "--dbServerUrl=dbServerUrl"};
        p.parseCommandLine(args);
        assertEquals("dbServerUrl", dbo.getDbServerUrl());
        args = new String[]{"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300", "--dbName=dbName"};
        p.parseCommandLine(args);
        assertEquals("dbName", dbo.getDbName());
        args = new String[]{"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300", "--password=password"};
        p.parseCommandLine(args);
        assertEquals("password", dbo.getDbPassword());
        args = new String[]{"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300", "--userName=userName"};
        p.parseCommandLine(args);
        assertEquals("userName", dbo.getDbUserName());
    }

    @Test
    public void cCommandLineNegativeTest1() {
        assertThrows(RuntimeException.class, () -> p.parseCommandLine(null));
    }

    @Test
    public void commandLineNegativeTest2() {
        assertThrows(RuntimeException.class, () -> {
            String[] args = {"--threshold=300"};
            p.parseCommandLine(args);
        });

    }

    @Test
    public void commandLineNegativeTest3() {
        assertThrows(RuntimeException.class, () -> {
            String[] args = {"--threshold=300", "--duration=daily"};
            p.parseCommandLine(args);
        });

    }

    @Test
    public void commandLineNegativeTest4() {
        assertThrows(RuntimeException.class, () -> {
            String[] args = {"--threshold=300", "--threshold=300"};
            p.parseCommandLine(args);
        });
    }

    @Test
    public void commandLineNegativeTest5() {
        assertThrows(RuntimeException.class, () -> {
            String[] args = {"--startDate 2017-01-01.13:00:00", "--duration=daily", "--threshold=300"};
            p.parseCommandLine(args);
        });

    }

    @Test
    public void commandLineNegativeTest6() {
        assertThrows(RuntimeException.class, () -> {
            String[] args = {"--startDate=2017-01-01.13:00:00", "--duration daily", "--threshold=300"};
            p.parseCommandLine(args);
        });

    }

    @Test
    public void commandLineNegativeTest7() {
        assertThrows(RuntimeException.class, () -> {
            String[] args = {"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold 300"};
            p.parseCommandLine(args);
        });

    }

    @Test
    public void commandLineNegativeTest8() {
        assertThrows(RuntimeException.class, () -> {
            String[] args = {"--startDate", "--duration=daily", "--threshold=300"};
            p.parseCommandLine(args);
        });
    }

    @Test
    public void commandLineNegativeTest9() {
        assertThrows(RuntimeException.class, () -> {
            String[] args = {"--startDate=2017-01-01.13:00:00", "--duration= daily1", "--threshold=300"};
            p.parseCommandLine(args);
        });
    }

    @Test
    public void commandLineNegativeTest10() {
        assertThrows(RuntimeException.class, () -> {
            String[] args = {"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold= 300vv"};
            p.parseCommandLine(args);
        });
    }


}
