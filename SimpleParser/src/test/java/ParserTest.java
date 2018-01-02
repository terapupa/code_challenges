import com.ef.DbOperations;
import com.ef.Parser;
import org.junit.Assert;
import org.junit.Test;

public class ParserTest {
    private DbOperations dbo = new DbOperations();
    private Parser p = new Parser(dbo);

    @Test
    public void commandLineTest() {
        String[] args = {"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300"};
        p.parseCommandLine(args);
        args = new String[]{"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300", "--accesslog=access.log"};
        p.parseCommandLine(args);
        Assert.assertTrue(true);
    }

    @Test
    public void dbParamsTest() {
        String[] args = {"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300", "--dbServerUrl=dbServerUrl"};
        p.parseCommandLine(args);
        Assert.assertTrue("dbServerUrl".equals(dbo.getDbServerUrl()));
        args = new String[]{"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300", "--dbName=dbName"};
        p.parseCommandLine(args);
        Assert.assertTrue("dbName".equals(dbo.getDbName()));
        args = new String[]{"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300", "--password=password"};
        p.parseCommandLine(args);
        Assert.assertTrue("password".equals(dbo.getDbPassword()));
        args = new String[]{"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold=300", "--userName=userName"};
        p.parseCommandLine(args);
        Assert.assertTrue("userName".equals(dbo.getDbUserName()));
    }

    @Test(expected = RuntimeException.class)
    public void cCommandLineNegativeTest1() {
        p.parseCommandLine(null);
    }

    @Test(expected = RuntimeException.class)
    public void commandLineNegativeTest2() {
        String[] args = {"--threshold=300"};
        p.parseCommandLine(args);
    }

    @Test(expected = RuntimeException.class)
    public void commandLineNegativeTest3() {
        String[] args = {"--threshold=300", "--duration=daily"};
        p.parseCommandLine(args);
    }

    @Test(expected = RuntimeException.class)
    public void commandLineNegativeTest4() {
        String[] args = {"--threshold=300", "--threshold=300"};
        p.parseCommandLine(args);
    }

    @Test(expected = RuntimeException.class)
    public void commandLineNegativeTest5() {
        String[] args = {"--startDate 2017-01-01.13:00:00", "--duration=daily", "--threshold=300"};
        p.parseCommandLine(args);
    }

    @Test(expected = RuntimeException.class)
    public void commandLineNegativeTest6() {
        String[] args = {"--startDate=2017-01-01.13:00:00", "--duration daily", "--threshold=300"};
        p.parseCommandLine(args);
    }

    @Test(expected = RuntimeException.class)
    public void commandLineNegativeTest7() {
        String[] args = {"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold 300"};
        p.parseCommandLine(args);
    }

    @Test(expected = RuntimeException.class)
    public void commandLineNegativeTest8() {
        String[] args = {"--startDate", "--duration=daily", "--threshold=300"};
        p.parseCommandLine(args);
    }

    @Test(expected = RuntimeException.class)
    public void commandLineNegativeTest9() {
        String[] args = {"--startDate=2017-01-01.13:00:00", "--duration= daily1", "--threshold=300"};
        p.parseCommandLine(args);
    }

    @Test(expected = RuntimeException.class)
    public void commandLineNegativeTest10() {
        String[] args = {"--startDate=2017-01-01.13:00:00", "--duration=daily", "--threshold= 300vv"};
        p.parseCommandLine(args);
    }


}
