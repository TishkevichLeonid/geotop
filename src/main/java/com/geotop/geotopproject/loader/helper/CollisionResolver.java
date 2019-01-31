package com.geotop.geotopproject.loader.helper;

import com.geotop.geotopproject.loader.helper.yandex.YGeoCode;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

@Component
public class CollisionResolver {

    private static final Logger LOG = LoggerFactory.getLogger(CollisionResolver.class);

    /**
     * Default percentage to ensure that names of the places are the same.
     */
    private static final double DEFAULT_NAME = 0.5;
    private static final double NOT_SAME_BUT_NEAR = 0.3;

    /**
     * Default distance to ensure that places are the same.
     */
    private static final double DEFAULT_DISTANCE = 0.1;
    private static final double NOT_NEAR_BUT_ALONE = 0.2;

    /**
     * Default count of neighbours in the same coordinates
     */
    private static final int NEIGHBOURS_COUNT = 3;

    /**
     * Coefficient for coordinates transformation.
     */
    private static final double MAGIC_CONST = 1.852;

    /**
     * CityDictionary contains a set of words need to be ignored during title comparison
     */
    private CityDictionary cityDictionary;
    private PlaceMerger placeMerger;
    private YGeoCode yGeoCode;

    @Autowired
    public CollisionResolver(CityDictionary cityDictionary, YGeoCode yGeoCode, PlaceMerger placeMerger) {
        this.cityDictionary = cityDictionary;
        this.yGeoCode = yGeoCode;
        this.placeMerger = placeMerger;
    }

    public List<Place> resolvePlaceCollision(List<Place> places) {
        return collapsePlaces(places);
    }

    private List<Place> collapsePlaces(List<Place> places) {
        ArrayList<Integer> group = new ArrayList<>(places.size());
        int last = 1;

        for (int i = 0; i < places.size(); i++) {
            boolean collapsed = false;
            Place place = places.get(i);
            String newTitle = cityDictionary.removeCityWords(place.getTitle());
            for (int j = 0; j < i; j++) {
                Place mPlace = places.get(j);
                String mNewTitle = cityDictionary.removeCityWords(mPlace.getTitle());

                float knear = getKNear(place, mPlace);
                boolean near = isNear(knear, DEFAULT_DISTANCE);
                boolean notSoNear = isNear(knear, NOT_NEAR_BUT_ALONE);

                double ksim = getKSimilarity(newTitle, mNewTitle);
                boolean same = isSame(ksim, DEFAULT_NAME);
                boolean notSoSame = isSame(ksim, NOT_SAME_BUT_NEAR);

                if (near && same) {
                    group.add(group.get(j));
                    collapsed = true;
                    break;
                }
                if (notSoNear && notSoSame) {
                    if (place.isAlone() == null) {
                        place.setAlone(isAlone(place));
                    }
                    if (mPlace.isAlone() == null) {
                        mPlace.setAlone(isAlone(mPlace));
                    }
                    boolean alone1 = place.isAlone();
                    boolean alone2 = mPlace.isAlone();

                    if (alone1 && alone2) {
                        LOG.info(String.format("Place '%s' and place '%s' are alone", place.getTitle(), mPlace.getTitle()));
                        group.add(group.get(j));
                        collapsed = true;
                        break;
                    }
                }
            }
            if (!collapsed) {
                group.add(last++);
            }
        }

        return getCollapsedPlaces(places, group, last);
    }

    private List<Place> getCollapsedPlaces(List<Place> places, List<Integer> groups, int lastIdx) {
        List<Place> collapsedPlaces = new ArrayList<>();

        for (int i = 1; i < lastIdx; i++) {
            boolean first = true;
            Place place = new Place();

            for (int j = 0; j < groups.size(); j++) {
                Place mPlace = places.get(j);
                if (groups.get(j) == i) {
                    if (!first) {
                        // merge places
                        place = placeMerger.mergePlaces(place, mPlace);

                        LOG.info(String.format("Place %s collapsed with %s", mPlace.getTitle(), place.getTitle()));
                    } else {
                        place = mPlace;
                        first = false;
                    }
                }
            }

            collapsedPlaces.add(place);
        }

        return collapsedPlaces;
    }

    private float getKNear(Place mark1, Place mark2) {
        double lat1 = (PI / 180) * mark1.getLatitude();
        double lat2 = (PI / 180) * mark2.getLatitude();
        double lon1 = (PI / 180) * mark1.getLongitude();
        double lon2 = (PI / 180) * mark2.getLongitude();

        double tmp = acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon1 - lon2));

        return (float) (tmp * (181 / PI) * 60 * MAGIC_CONST);
    }

    private boolean isNear(double knear, double pctg) {
        return knear <= pctg;
    }

    private boolean isSame(double ksim, double pctg) {
        return ksim > pctg;
    }

    private double getKSimilarity(String place1, String place2) {
       return compareTitles(place1, place2);
    }

    private boolean isAlone(Place place) {
        int neighboursCount;

        double lon = place.getLongitude();
        double lat = place.getLatitude();

        try {
            neighboursCount = yGeoCode.countNearby(lon, lat);
        } catch (Exception e) {
            LOG.error("Error while requesting yandex api", e);
            neighboursCount = NEIGHBOURS_COUNT + 1;
        }
        return neighboursCount <= NEIGHBOURS_COUNT;
    }

    private double compareTitles(String title1, String title2) {
        StringPairComparator comparator = new StringPairComparator();
        TransliterationHelper transliterationHelper = new TransliterationHelper();
        title1 = transliterationHelper.cyr2lat(title1);
        title2 = transliterationHelper.cyr2lat(title2);

        if (StringUtils.isEmpty(title1.replaceAll("\\s+", "")) || StringUtils.isEmpty(title2.replaceAll("\\s+", ""))) {
            return 0;
        }
        return comparator.compareStrings(title1, title2);
    }

}
