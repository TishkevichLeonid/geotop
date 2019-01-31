package com.geotop.geotopproject.loader.facebook;

import com.geotop.geotopproject.loader.CommonLoader;
import org.springframework.stereotype.Component;

public interface FBLoader extends CommonLoader {
    String ACCESS_TOKEN = "147740402514480|WVEnPpUp7zgQSOV78TwkaO2PhAQ";
    String COORDINATES = "59.935634,30.325935";

    String API_BASE = "https://graph.facebook.com/v2.11";
    String API_PLACES_URL = API_BASE +
            "/search" +
            "?type=place" +
            "&center=%s" +
            "&distance=15000" +
            "&limit=100" +
            "&fields=name,link,location,checkins,rating_count,overall_star_rating,category,picture.type(large){url}" +
            "&access_token=%s";

}