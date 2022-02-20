package com.ef.service;

import com.ef.Duration;
import com.ef.domain.BlockedIp;
import com.ef.domain.BlockedIpId;
import com.ef.domain.BlockedIpRepository;
import com.ef.domain.Request;
import com.ef.domain.RequestRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static com.ef.service.Commandline.DURATION_OPT;
import static com.ef.service.Commandline.START_DATE_OPT;
import static com.ef.service.Commandline.THRESHOLD_OPT;
import static com.ef.service.Commandline.printError;

@Component
@Transactional
public class ParsingService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BlockedIpRepository blockedIpRepository;

    @Autowired
    private Environment environment;

    public void parse() {
        if (isAccesslogParamValid()) {
            loadDb();
        }
        calculate();
    }

    private void calculate() {
        Date date = new Date();
        int threshold = 0;
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss")).parse(environment.getProperty(START_DATE_OPT));
            threshold = Integer.parseInt(environment.getProperty(THRESHOLD_OPT));
        } catch (Exception e) {
            printError(e.getMessage(), true);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String startTimeStr = format.format(date);
        Duration duration = Duration.valueOf(environment.getProperty(DURATION_OPT));
        String endTimeStr = format.format(new Date(date.getTime() + Duration.getTimeMillis(duration)));
        List<?> l = requestRepository.findExceededIps(startTimeStr, endTimeStr, threshold);
        for (Object exceededIp : l)
        {
            String ip = (String)((Object[])exceededIp)[0];
            int number = ((BigInteger)((Object[])exceededIp)[1]).intValue();
            String reason = "'" + "From " + startTimeStr + " : " + number +
                    " requests from IP:" + ip + " exceeded " + duration.name() + " threshold=" + threshold + "'";
            System.out.println(reason);

            BlockedIp bip = new BlockedIp();
            bip.setReason(reason);
            BlockedIpId bipId = new BlockedIpId();
            bipId.setIp(ip);
            bipId.setDuration(duration.name());
            bipId.setRequestNumber(number);
            bipId.setThreshold(threshold);
            bipId.setMesuredAt(date);
            bip.setId(bipId);
            blockedIpRepository.save(bip);
        }
    }

    private BufferedReader getAccessLogBufferReader() {
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(environment.getProperty(Commandline.ACCESS_LOG_OPT))));
        } catch (FileNotFoundException e) {
            printError(e.getMessage(), true);
        }
        return null;
    }

    private void loadDb() {
        requestRepository.deleteAllInBatch();
        requestRepository.flush();
        ArrayList<Request> items = new ArrayList<>();
        getAccessLogBufferReader()
                .lines()
                .map(mapToItem)
                .forEach(item -> {
                    if (items.size() == 1000) {
                        requestRepository.saveAll(items);
                        System.out.println(items.size() + " records added to the table 'Request'...");
                        items.clear();
                    }
                    items.add(item);
                });
        requestRepository.saveAll(items);
        System.out.println(items.size() + " records added to the table 'Request'...");
        requestRepository.flush();
    }

    private boolean isAccesslogParamValid() {
        String accesslog = environment.getProperty(Commandline.ACCESS_LOG_OPT);
        if (StringUtils.isEmpty(accesslog)) return false;
        boolean ret = StringUtils.isNotEmpty(accesslog) && (new File(accesslog)).exists();
        if (!ret) {
            printError("file " + accesslog + " not found", true);
        }
        return ret;
    }

    private final Function<String, Request> mapToItem = (line) -> {
        String[] values = StringUtils.split(line, "|");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            return new Request(values[1].trim(), format.parse(values[0].trim()));
        } catch (ParseException e) {
            printError(e.getMessage(), true);
        }
        return null;
    };
}
