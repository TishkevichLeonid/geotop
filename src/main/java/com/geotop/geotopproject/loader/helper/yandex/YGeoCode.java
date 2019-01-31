package com.geotop.geotopproject.loader.helper.yandex;

import java.util.ArrayList;
import java.util.List;

public interface YGeoCode {
    String API_BASE = "https://geocode-maps.yandex.ru/1.x/?format=json";

    String API_GEOCODE = API_BASE +
            "&geocode=%s,%s" +
            "&kind=house" +
            "&spn=0.002,0.002";

    String F_COUNT = "found";

    List<String> JSON_XPATH = new ArrayList<String>() {{
        add("response");
        add("GeoObjectCollection");
        add("metaDataProperty");
        add("GeocoderResponseMetaData");
    }};

    int countNearby(double longitude, double latitude) throws Exception;
}
