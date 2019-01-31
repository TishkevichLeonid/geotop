package com.geotop.geotopproject.loader.helper.yandex;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class YGeoCodeImpl implements YGeoCode {

    @Override
    public int countNearby(double longitude, double latitude) throws Exception {
        String url = String.format(API_GEOCODE, longitude, latitude);
        return getCountFromJson(callGeoCodeApi(url));
    }

    private JsonObject callGeoCodeApi(String url) throws Exception {
        String json = IOUtils.toString(new URL(url).openStream());
        return new Gson().fromJson(json, JsonObject.class);
    }

    private int getCountFromJson(JsonObject jsonObject) {
        //TODO: replace this workaround
        for (String node : JSON_XPATH) {
            jsonObject = jsonObject.getAsJsonObject(node);
        }
        return jsonObject.getAsJsonPrimitive(F_COUNT).getAsInt();
    }
}
