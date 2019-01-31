package com.geotop.geotopproject.loader.helper;

import com.geotop.geotopproject.model.places.APISpecificData;
import com.geotop.geotopproject.model.places.Checkin;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.api.FbData;
import com.geotop.geotopproject.model.places.api.VkData;
import com.geotop.geotopproject.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.geotop.geotopproject.utils.Utils.nvl;

@Component
public class PlaceMerger {
    private static final Logger LOG = LoggerFactory.getLogger(PlaceMerger.class);

    private static final int TYPE_OTHER = 11;

    public Place mergePlaces(Place target, Place collapsed) {
        List<APISpecificData> targetSpecificDataList = collectAPIData(target);
        List<APISpecificData> collapsedSpecificDataList = collectAPIData(collapsed);

        target = mergeTypes(target, collapsed);

        targetSpecificDataList = mergeApiSpecificData(targetSpecificDataList, collapsedSpecificDataList);
        for (APISpecificData data : targetSpecificDataList) {
            if (data instanceof VkData) {
                target.setVkData(data);
            }
            if (data instanceof FbData) {
                target.setFbData(data);
            }
        }

        target.setVkData(nvl(target.getVkData(), collapsed.getVkData()));
        target.setFbData(nvl(target.getFbData(), collapsed.getFbData()));


        target.setAlone(nvl(target.isAlone(), collapsed.isAlone()));
        target.setPicture(nvl(target.getPicture(), collapsed.getPicture()));

        return target;
    }

    private Place mergeTypes(Place target, Place collapsed) {
        Integer type = target.getType();
        Integer mType = collapsed.getType();

        // Set valid type if collapsed place has type = 11 (Other)
        // VK type is not preferred
        if ((!type.equals(mType) && type == TYPE_OTHER) ||
                (target.getFbData() == null && collapsed.getFbData() != null && !mType.equals(TYPE_OTHER))) {
            target.setType(mType);
        }

        return target;
    }

    private List<APISpecificData> mergeApiSpecificData(List<APISpecificData> target, List<APISpecificData> collapsed) {
        for (APISpecificData targetData : target) {
            for (APISpecificData collapsedData : collapsed) {
                if (collapsedData != null) {
                    if (targetData.getClass().equals(collapsedData.getClass())) {
                        targetData = mergeApiSpecificData(targetData, collapsedData);
                    }
                }
            }
        }

        return target;
    }

    private APISpecificData mergeApiSpecificData(APISpecificData target, APISpecificData collapsed) {
        target = mergeCheckins(target, collapsed);
        target = mergeRating(target, collapsed);

        return target;
    }

    private APISpecificData mergeCheckins(APISpecificData target, APISpecificData collapsed) {
        List<Checkin> targetCheckins = target.getCheckins();
        List<Checkin> collapsedCheckins = collapsed.getCheckins();
        Integer targetCheckinsCount = target.getCheckinsCount();
        Integer collapsedCheckinsCount = collapsed.getCheckinsCount();

        if (targetCheckins == null) {
            target.setCheckins(collapsedCheckins);
        } else {
            if (collapsedCheckins != null) {
                targetCheckins.addAll(collapsedCheckins);
                target.setCheckins(targetCheckins);
            }
        }

        if (targetCheckinsCount == null) {
            target.setCheckinsCount(collapsedCheckinsCount);
        } else {
            target.setCheckinsCount(targetCheckinsCount + collapsedCheckinsCount);
        }

        return target;
    }

    private APISpecificData mergeRating(APISpecificData target, APISpecificData collapsed) {
        Double targetRating = target.getRating();
        Double collapsedRating = collapsed.getRating();

        if (targetRating == null) {
            target.setRating(collapsedRating);
        } else {
            target.setRating(Utils.average(targetRating, collapsedRating));
        }

        return mergeRatingCount(target, collapsed);
    }

    private APISpecificData mergeRatingCount(APISpecificData target, APISpecificData collapsed) {
        Integer fbRatingCountTarget = target.getRatingCount();
        Integer fbRatingCountCollapsed = collapsed.getRatingCount();
        if (fbRatingCountTarget == null) {
            target.setRatingCount(fbRatingCountCollapsed);
        } else {
            target.setRatingCount(fbRatingCountTarget + fbRatingCountCollapsed);
        }

        return target;
    }

    private List<APISpecificData> collectAPIData(Place place) {
        return Stream.of(
                place.getVkData(), place.getFbData()
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
