package com.geotop.geotopproject.loader.helper;

import com.geotop.geotopproject.model.places.APISpecificData;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PopularityRatingProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(PopularityRatingProcessor.class);

    private static final String VK_KEY = "vk";
    private static final String FB_KEY = "fb";
    private static final int RATE_MIN = 2;
    private static final int RATE_MAX = 5;
    private static final double COEFFICIENT_AVERAGE_CHECKINS = 1.5;
    private static final double MAX_POPULARITY = 5.0;

    public List<Place> indexPlaces(List<Place> places) {
        Map<String, Boundary> map = getCheckinsCountBoundaries(places);

        places.forEach(place -> countOverallRatingAndPopularity(place, map));

        return places;
    }

    private Place countOverallRatingAndPopularity(Place place, Map<String, Boundary> popularityMap) {
        place.setOverallPopularity(countOverallPopularity(place, popularityMap));
        place.setOverallRating(countOverallRating(place));

        return place;
    }

    private double countOverallRating(Place place) {
        double index = 0;
        double vkIndex = 0;
        double fbIndex = 0;

        APISpecificData vkData = place.getVkData();
        APISpecificData fbData = place.getFbData();

        // count VK rating
        if (vkData != null) {
            vkIndex = vkData.getRating();
            index = vkIndex;
        }
        // count FB rating
        if (fbData != null) {
            fbIndex = fbData.getRating();
            index = fbIndex;
        }

        // count overall popularity
        if (vkIndex != 0 && fbIndex != 0) {
            index = (vkIndex + fbIndex) / 2;
        }

        return Utils.round(index, 2);
    }


    private double countOverallPopularity(Place place, Map<String, Boundary> popularityMap) {
        double index = 0;
        double vkIndex = 0;
        double fbIndex = 0;

        APISpecificData vkData = place.getVkData();
        APISpecificData fbData = place.getFbData();

        // count VK popularity
        if (vkData != null) {
            Boundary boundary = popularityMap.get(VK_KEY);
            index = countPopularity(vkData, boundary);
            vkIndex = index;
            vkData.setPopularity(index);
        }

        // count FB popularity
        if (fbData != null) {
            Boundary boundary = popularityMap.get(FB_KEY);
            index = countPopularity(fbData, boundary);
            fbIndex = index;
            fbData.setPopularity(index);
        }

        // count overall popularity
        if (vkIndex != 0 && fbIndex != 0) {
            index = (vkIndex + fbIndex) / 2;
        }

        return Utils.round(index, 2);
    }

    private double countPopularity(APISpecificData data, Boundary boundary) {
        int amountCheckins = data.getCheckinsCount();
        if (amountCheckins >= (boundary.average * COEFFICIENT_AVERAGE_CHECKINS)){
            return MAX_POPULARITY;
        } else {
            return countIndex(amountCheckins, boundary.max, RATE_MAX, RATE_MIN);
        }
    }

    private double countIndex(int currentAmount, int maxCheckinsCount, int maxRate, int minRate) {
        if (maxCheckinsCount == 0) {
            return 0;
        }

        // workaround to get close value (for example it gives 2.78 instead of 2.0)
        double diffRate = maxRate - minRate;
        double chis = diffRate * (double) currentAmount;
        double drob = chis / (double) maxCheckinsCount;
        double index = drob + minRate;

        return Utils.round(index, 2);
    }


    private Map<String, Boundary> getCheckinsCountBoundaries(List<Place> places) {
        Map<String, Boundary> map = new HashMap<>();

        map.put(VK_KEY, getVkInfo(places));
        map.put(FB_KEY, getFbInfo(places));

        return map;
    }

    private Boundary getVkInfo(List<Place> places) {
        Place vkPlace;
        int max = 0;
        vkPlace = places.stream()
                .filter(place -> place.getVkData() != null)
                .max(Comparator.comparingInt(c -> c.getVkData().getCheckinsCount()))
                .orElse(null);
        if (vkPlace != null) {
            max = vkPlace.getVkData().getCheckinsCount();
        }

        double avg = getAverageAmountVK(places);

        return new Boundary(avg, max);
    }

    private Boundary getFbInfo(List<Place> places) {
        Place fbPlace;
        int max = 0;
        fbPlace = places.stream()
                .filter(place -> place.getFbData() != null)
                .max(Comparator.comparingInt(c -> c.getFbData().getCheckinsCount()))
                .orElse(null);
        if (fbPlace != null) {
            max = fbPlace.getFbData().getCheckinsCount();
        }

        double avg = getAverageAmountFB(places);

        return new Boundary(avg, max);
    }


    private double getAverageAmountVK(List<Place> places){
        double sum = 0;
        double count = 0;
        for (Place place: places){
            if (place.getVkData() != null){
                sum += place.getVkData().getCheckinsCount();
                count++;
            }
        }
        return sum / count;
    }

    private double getAverageAmountFB(List<Place> places){
        double sum = 0;
        double count = 0;
        for (Place place: places){
            if (place.getFbData() != null){
                sum += place.getFbData().getCheckinsCount();
                count++;
            }
        }
        return sum / count;
    }


    private class Boundary {
        double average;
        int max;

        Boundary(double average, int max) {
            this.average = average;
            this.max = max;
        }
    }

}
