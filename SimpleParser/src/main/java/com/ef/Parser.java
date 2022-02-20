package com.ef;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Function;

public class Parser {

    private static final String DB_SERVER_URL = "dbServerUrl";
    private static final String THRESHOLD_OPT = "threshold";
    private static final String DURATION_OPT = "duration";
    private static final String START_DATE_OPT = "startDate";
    private static final String ACCESS_LOG_OPT = "accesslog";
    private static final String DB_NAME = "dbName";
    private static final String DB_USERNAME = "userName";
    private static final String DB_PASSWORD = "password";

    private Integer threshold;
    private Date startDate;
    private Duration duration;
    private String accesslog;
    private DbOperations dbOperations;

    public enum Duration {
        hourly, daily;

        public static long getTimeMillis(Duration d) {
            long t = 0;
            if (d.equals(Duration.hourly)) {
                t = 1000 * 60 * 60;
            } else if (d.equals(Duration.daily)) {
                t = 1000 * 60 * 60 * 24;
            }
            return t;
        }
    }

    public Parser() {
        dbOperations = new DbOperations();
    }

    /**
     * For unit test purpose
     *
     * @param dbo - DbOperations injection
     */
    public Parser(DbOperations dbo) {
        dbOperations = dbo;
    }

    /**
     * Parse an input log file, create and fill DB and calculate blocked IPs
     *
     * @param args - command line args
     */
    public void parse(String[] args) {
        if (parseCommandLine(args)) {
            boolean loadDb = isAccesslogParamValid();
            dbOperations.checkDBExistsAndCreate(loadDb);
            if (loadDb) {
                loadDb();
            }
            dbOperations.calculateBlockedIpAddr(startDate, duration, threshold);
        }
    }

    private boolean isAccesslogParamValid() {
        if (StringUtils.isEmpty(accesslog)) return false;
        boolean ret = StringUtils.isNotEmpty(accesslog) && (new File(accesslog)).exists();
        if (!ret) {
            printError("file " + accesslog + " not found", true);
        }
        return ret;
    }

    private BufferedReader getAccessLogBufferReader() {
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(accesslog)));
        } catch (FileNotFoundException e) {
            printError(e.getMessage(), true);
        }
        return null;
    }

    private void loadDb() {
        ArrayList<RequestItem> items = new ArrayList<>();
        try {
            dbOperations.createConnectionAndStatement();
            getAccessLogBufferReader()
                    .lines()
                    .map(mapToItem)
                    .forEach(item -> {
                        if (items.size() == 1000) {
                            dbOperations.insertRequestsNoConnection(items);
                            items.clear();
                        }
                        items.add(item);
                    });
            dbOperations.insertRequestsNoConnection(items);
        } catch (Exception e) {
            printError(e.getMessage(), true);
        } finally {
            dbOperations.closeConnectionAndStatement();
        }
    }

    private Function<String, RequestItem> mapToItem = (line) -> {
        String[] values = StringUtils.split(line, "|");
        return new RequestItem(values[1].trim(), values[0].trim());
    };

    /**
     * Commandline parameter parser.
     *
     * @param args
     * @return
     */
    public boolean parseCommandLine(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
            commandLine = parser.parse(getCommandLineOptions(), args);
            Long th = (Long) commandLine.getParsedOptionValue(THRESHOLD_OPT);
            if (th == null) {
                throw new ParseException("Missing parameter : " + THRESHOLD_OPT);
            }
            threshold = th.intValue();
            String dur = commandLine.getOptionValue(DURATION_OPT);
            if (dur == null) {
                throw new ParseException("Missing parameter : " + DURATION_OPT);
            }
            duration = Duration.valueOf(dur);
            String date = commandLine.getOptionValue(START_DATE_OPT);
            if (date == null) {
                throw new ParseException("Missing parameter : " + START_DATE_OPT);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
            startDate = format.parse(date);
            accesslog = commandLine.getOptionValue(ACCESS_LOG_OPT);

            String dbServerUrl = commandLine.getOptionValue(DB_SERVER_URL);
            if (StringUtils.isNotEmpty(dbServerUrl)) {
                dbOperations.setDbServerUrl(dbServerUrl);
            }
            String dbName = commandLine.getOptionValue(DB_NAME);
            if (StringUtils.isNotEmpty(dbName)) {
                dbOperations.setDbName(dbName);
            }
            String dbPassword = commandLine.getOptionValue(DB_PASSWORD);
            if (StringUtils.isNotEmpty(dbPassword)) {
                dbOperations.setDbPassword(dbPassword);
            }
            String dbUserName = commandLine.getOptionValue(DB_USERNAME);
            if (StringUtils.isNotEmpty(dbUserName)) {
                dbOperations.setDbUserName(dbUserName);
            }
        } catch (ParseException | IllegalArgumentException | java.text.ParseException e) {
            printError(e.getMessage(), true);
            return false;
        }
        return true;
    }

    private static Options getCommandLineOptions() {
        org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

        options.addOption(org.apache.commons.cli.Option.builder()
                .longOpt(START_DATE_OPT)
                .hasArg()
                .desc("--startDate=<yyyy-MM-dd.HH:mm:ss> (mandatory)")
                .build());

        options.addOption(org.apache.commons.cli.Option.builder()
                .longOpt(DURATION_OPT)
                .hasArg()
                .desc("--duration=<hourly|daily> (mandatory)")
                .build());

        options.addOption(org.apache.commons.cli.Option.builder()
                .longOpt(THRESHOLD_OPT)
                .hasArg()
                .desc("--threshold=<number> (mandatory)")
                .type(Number.class)
                .build());

        options.addOption(org.apache.commons.cli.Option.builder()
                .longOpt(ACCESS_LOG_OPT)
                .hasArg()
                .desc("--accesslog=<full file name>")
                .required(false)
                .build());

        options.addOption(org.apache.commons.cli.Option.builder()
                .longOpt(DB_SERVER_URL)
                .hasArg()
                .desc("--dbServerUrl=<jdbc:mysql://localhost/>")
                .required(false)
                .build());

        options.addOption(org.apache.commons.cli.Option.builder()
                .longOpt(DB_NAME)
                .hasArg()
                .desc("--dbName=<name>")
                .required(false)
                .build());

        options.addOption(org.apache.commons.cli.Option.builder()
                .longOpt(DB_USERNAME)
                .hasArg()
                .desc("--userName=<name>")
                .required(false)
                .build());

        options.addOption(org.apache.commons.cli.Option.builder()
                .longOpt(DB_PASSWORD)
                .hasArg()
                .desc("--password=<name>")
                .required(false)
                .build());
        return options;
    }

    /**
     * Print error message
     *
     * @param msg            - message
     * @param throwException if <code>throwException</code>=true - throws RuntimeException
     */
    public static void printError(String msg, boolean throwException) {
        System.err.println("Parser failed. Reason: " + msg);
        printHelp();
        if (throwException) {
            throw new RuntimeException(msg);
        }
    }

    /**
     * Print commandline parameters
     */
    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("com.ef.Parser", getCommandLineOptions());
    }

    public static void main(String[] args) {
        Parser p = new Parser();
        p.parse(args);
    }

}
