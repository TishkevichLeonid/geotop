package com.geotop.geotopproject.loader.vk;

import com.geotop.geotopproject.loader.CommonLoader;
import com.geotop.geotopproject.model.places.Checkin;
import com.geotop.geotopproject.model.places.Place;
import org.springframework.stereotype.Component;

import java.util.List;

public interface VKLoader extends CommonLoader {
    String ACCESS_TOKEN = "ca28670e9ed42a77b9b3e26a1cf749bc8d675a4bf201970efb85a4a7dab3cc32489bfaa0a196409922eb4";
    String LATTITUDE = "59.935634";
    String LONGTITUDE = "30.325935";
    int PLACE_COUNT = 1000;
    int PLACE_MAX_OFFSET = 0;

    String API_BASE = "https://api.vk.com/method";
    String API_PLACES_URL = API_BASE +
                        "/places.search" +
                        "?latitude=%s" +
                        "&longitude=%s" +
                        "&radius=3" +
                        "&offset=%d" +
                        "&count=%d" +
                        "&access_token=%s" +
                        "&v=5.60";
    String API_CHECKINS_URL = API_BASE +
                        "/places.getCheckins?" +
                        "&place=%s" +
                        "&count=%s" +
                        "&access_token=%s" +
                        "&v=5.60";

}
