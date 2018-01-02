package com.ef.service;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class Commandline {
    private static final String DB_SERVER_URL = "spring.datasource.url";
    private static final String DB_USERNAME = "spring.datasource.username";
    private static final String DB_PASSWORD = "spring.datasource.password";

    public static final String THRESHOLD_OPT = "threshold";
    public static final String DURATION_OPT = "duration";
    public static final String START_DATE_OPT = "startDate";
    public static final String ACCESS_LOG_OPT = "accesslog";

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
            String dur = commandLine.getOptionValue(DURATION_OPT);
            if (dur == null) {
                throw new ParseException("Missing parameter : " + DURATION_OPT);
            }
            String date = commandLine.getOptionValue(START_DATE_OPT);
            if (date == null) {
                throw new ParseException("Missing parameter : " + START_DATE_OPT);
            }
            (new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss")).parse(date);

        } catch (ParseException | IllegalArgumentException | java.text.ParseException e) {
            printError(e.getMessage(), true);
            return false;
        }
        return true;
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
                .desc("--spring.datasource.url=<jdbc:mysql://localhost/db>?createDatabaseIfNotExist=true")
                .required(false)
                .build());

        options.addOption(org.apache.commons.cli.Option.builder()
                .longOpt(DB_USERNAME)
                .hasArg()
                .desc("--spring.datasource.username=<name>")
                .required(false)
                .build());

        options.addOption(org.apache.commons.cli.Option.builder()
                .longOpt(DB_PASSWORD)
                .hasArg()
                .desc("--spring.datasource.password=<name>")
                .required(false)
                .build());
        return options;
    }

}
