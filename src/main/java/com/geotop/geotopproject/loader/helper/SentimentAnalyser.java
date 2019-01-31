package com.geotop.geotopproject.loader.helper;

import com.geotop.geotopproject.model.places.Checkin;
import com.geotop.geotopproject.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SentimentAnalyser {
    private Map<String, Double> dict;

    @PostConstruct
    public void init(){parseSentimentDictionary();}

    private void parseSentimentDictionary(){
        try {
            dict = createSentimentDictionary();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String ratePlace(List<Checkin> checkins){
        if (checkins == null || checkins.isEmpty()){
            return null;
        }

        double sumRating = 0;
        double countRatedCheckins = 0;

        for (Checkin checkin: checkins){
            String text = checkin.getText();
            if (text == null || text.isEmpty() || text.equals("")){
                continue;
            }
            double checkinRating = rateReview(text);
            if (checkinRating != 0){
                sumRating += checkinRating;
                countRatedCheckins++;
            }
        }

        if (sumRating == 0.0 || countRatedCheckins == 0.0){
            return null;
        } else {
            return String.format("%.1f", sumRating / countRatedCheckins).replaceAll(",",".");
        }
    }

    private double rateReview(String text) {
        String[] words = text.split("\\s");
        double sumRating = 0;
        double countRatedWords = 0;

        for (Map.Entry<String, Double> entry: dict.entrySet()){
            String key = entry.getKey();
            double value = entry.getValue();

            for (String word: words){
                if (key.equals(word)){
                    sumRating += value;
                    countRatedWords++;
                }
            }
        }

        if (sumRating == 0.0 || countRatedWords == 0.0){
            return 0;
        } else {
            return Utils.round(sumRating / countRatedWords, 2);
        }
    }

    private Map<String, Double> createSentimentDictionary() throws IOException {
        Resource res = new ClassPathResource("dictionary/sentimentDict");
        BufferedReader br = new BufferedReader(new FileReader(res.getFile()));
        String line;
        Map<String, Double> sum = new HashMap<>();
        Map<String, Double> count = new HashMap<>();

        // parse file
        while ((line = br.readLine()) != null){
            String[] fields = line.split("\t", -1);
            String key = fields[0];
            double value = Double.parseDouble(fields[1]);

            // workaround to remove neutral words
            if (value == 3) continue;

            if (!sum.containsKey(key)){
                sum.put(key, value);
            } else {
                double oldValue = sum.get(key);
                sum.put(key, oldValue + value);
            }

            if (!count.containsKey(key)){
                count.put(key, 1d);
            } else {
                double oldValue = count.get(key);
                count.put(key, oldValue + 1);
            }
        }

        // iterate parsed map and init map for dictionary
        Map<String, Double> dict = new HashMap<>();
        for (Map.Entry<String, Double> entrySum : sum.entrySet()){
            String key = entrySum.getKey();
            double valueSum = entrySum.getValue();
            double valueCount = count.get(key);

            double sentValue = Utils.round(valueSum / valueCount, 2);
            dict.put(key, sentValue);
        }

        return dict;
    }
}
