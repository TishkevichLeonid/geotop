package com.geotop.geotopproject.loader.vk.impl;

import com.geotop.geotopproject.loader.helper.CollisionResolver;
import com.geotop.geotopproject.loader.helper.SentimentAnalyser;
import com.geotop.geotopproject.loader.vk.VKLoader;
import com.geotop.geotopproject.model.places.APISpecificData;
import com.geotop.geotopproject.model.places.Checkin;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.deserializer.PlaceDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class VKLoaderImpl implements VKLoader {

    private static final Logger LOG = LoggerFactory.getLogger(VKLoaderImpl.class);

    private CollisionResolver collisionResolver;
    private PlaceDeserializer placeDeserializer;
    private SentimentAnalyser sentimentAnalyser;

    @Autowired
    public VKLoaderImpl(CollisionResolver collisionResolver, PlaceDeserializer placeDeserializerVK, SentimentAnalyser sentimentAnalyser) {
        this.collisionResolver = collisionResolver;
        this.placeDeserializer = placeDeserializerVK;
        this.sentimentAnalyser = sentimentAnalyser;
    }

    @Override
    public List<Place> loadData() throws Exception {
        List<Place> places = loadPlaces();
        places = collisionResolver.resolvePlaceCollision(places);

        LOG.info("vk loading done");
        return places;
    }

    private List<Place> loadPlaces() throws Exception {
        List<Place> places = new ArrayList<>();

        places = callPlaceAPI(places, 0, PLACE_COUNT, PLACE_MAX_OFFSET);
        for (Place place : places) {
            APISpecificData vkData = place.getVkData();
            List<Checkin> checkins = callCheckinsAPI(vkData.getId());
            vkData.setCheckins(checkins);
            try {
                vkData.setRating(Double.valueOf(sentimentAnalyser.ratePlace(checkins)));
            } catch (NullPointerException e) {
                LOG.error("Rating is null for " + place.getTitle());
            }

            place.setVkData(vkData);
        }

        LOG.info("places loaded");
        return places;
    }

    private List<Checkin> callCheckinsAPI(String placeId) throws Exception {
        String url = String.format(API_CHECKINS_URL, String.valueOf(placeId), "100", ACCESS_TOKEN);
        String json = IOUtils.toString(new URL(url).openStream())
                .replaceAll("\\{\"response\":","")
                .replaceAll("\\{\"count\".*\"items\":","")
                .replaceAll("\\}\\}$","");
        Thread.sleep(300);
        LOG.info("Checkins loaded for " + placeId);
        Type listType = new TypeToken<ArrayList<Checkin>>(){}.getType();
        try {
            return new Gson().fromJson(json, listType);
        } catch (Exception e){
            LOG.error("place " + placeId + ": " + e.getMessage());
            return null;
        }
    }

    private List<Place> callPlaceAPI(List<Place> places, int offset, int count, int maxOffset) throws IOException {
        String url = String.format(API_PLACES_URL, LATTITUDE, LONGTITUDE, offset, count, ACCESS_TOKEN);
        LOG.info(url);
        String json = IOUtils.toString(new URL(url).openStream())
                .replaceAll("\\{\"response\":","")
                .replaceAll("\\{\"count\".*\"items\":","")
                .replaceAll("\\]\\}\\}$",",");
        if(json.endsWith(",")) {
            json = json.substring(0, json.length() - 1) + "]";
        }
        LOG.info(json);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Place.class, placeDeserializer);
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<ArrayList<Place>>(){}.getType();
        List<Place> placeList = gson.fromJson(json, listType);
        places.addAll(placeList);
        if (offset == maxOffset) {
            LOG.info("exit from rec");
            return places;
        }
        LOG.info("rec");
        return callPlaceAPI(places, offset + count, PLACE_COUNT, PLACE_MAX_OFFSET);
    }
}
