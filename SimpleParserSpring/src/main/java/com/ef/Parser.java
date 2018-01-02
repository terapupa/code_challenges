package com.ef;

import com.ef.service.Commandline;
import com.ef.service.ParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class Parser implements CommandLineRunner {

    @Autowired
    ParsingService parsingService;

    @Autowired
    private Commandline commandline;

    public static void main(String[] args) {
        SpringApplication.run(Parser.class, args);
    }

    @Override
    public void run(String... strings) {
        if (commandline.parseCommandLine(strings)) {
            parsingService.parse();
        }
    }
}
