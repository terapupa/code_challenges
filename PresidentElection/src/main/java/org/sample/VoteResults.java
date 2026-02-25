package org.sample;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VoteResults {

    private Map<String, Integer> winners(String fullFileName) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(fullFileName));
        return br.lines().
                map(mapToItem).
                filter(x -> x.getOffice().contains("PRESIDENT AND VICE PRESIDENT OF THE UNITED STATES")).
                collect(Collectors.groupingBy(ElectionItem::getCandidate, Collectors.
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

    private final Function<String, ElectionItem> mapToItem = (line) -> {
        String[] p = line.split(",");
        try {
            return new ElectionItem(p[3], p[4], Integer.parseInt(p[6]));
        } catch (NumberFormatException e) {
            return new ElectionItem(p[3], p[4], -1);
        }
    };

    private static class ElectionItem {
        private final String office;
        private final String candidate;
        private Integer votes = -1;

        public ElectionItem(String office,
                            String candidate, Integer votes) {
            this.office = office;
            this.candidate = candidate;
            this.votes = votes;
        }

        String getOffice() {
            return this.office;
        }

        String getCandidate() {
            return this.candidate;
        }

        Integer getVotes() {
            return this.votes;
        }

    }

    public static void main(String[] args) {
        try {
            VoteResults t = new VoteResults();
            Map<String, Integer> resMap = t.winners("/Users/terapupa/workspace/personal/code_challenges/PresidentElection/src/main/resources/2012_general.csv");
            for (String key : resMap.keySet()) {
                System.out.println("Name : " + key + " Votes:" + resMap.get(key));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
