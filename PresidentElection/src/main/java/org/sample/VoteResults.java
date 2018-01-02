package org.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VoteResults {

    private Map<String, Integer> winners(String fullFileName) throws FileNotFoundException {
        InputStream is = new FileInputStream(new File(fullFileName));
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        return br.lines().
                map(mapToItem).
                filter(x -> x.getOffice().contains("PRESIDENT AND VICE PRESIDENT OF THE UNITED STATES")).
                collect(Collectors.groupingBy(ElectionItem::getCanidate, Collectors.
                        reducing(0, ElectionItem::getVotes, Integer::sum))).
                entrySet().
                stream().
                sorted(Map.Entry.
                        comparingByValue(Collections.reverseOrder())).
                limit(3).
                collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Function<String, ElectionItem> mapToItem = (line) -> {
        String[] p = line.split(",");
        try {
            return new ElectionItem(p[3], p[4], p[5], Integer.parseInt(p[6]));
        } catch (NumberFormatException e) {
            return new ElectionItem(p[3], p[4], p[5], -1);
        }
    };

    private class ElectionItem {
        private String office;
        private String canidate;
        private String party;
        private Integer votes = -1;

        public ElectionItem(String office,
                            String canidate, String party, Integer votes) {
            this.office = office;
            this.canidate = canidate;
            this.party = party;
            this.votes = votes;
        }

        String getOffice() {
            return this.office;
        }

        String getCanidate() {
            return this.canidate;
        }

        Integer getVotes() {
            return this.votes;
        }

    }

    public static void main(String[] args) {
        try {
            VoteResults t = new VoteResults();
            Map<String, Integer> resMap = t.winners("/Users/vsamot200/Downloads/2012_general.csv");
            for (String key : resMap.keySet()) {
                System.out.println("Name : " + key + " Votes:" + resMap.get(key));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
