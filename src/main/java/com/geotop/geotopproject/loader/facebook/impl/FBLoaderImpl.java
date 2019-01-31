package com.geotop.geotopproject.loader.facebook.impl;

import com.geotop.geotopproject.loader.facebook.FBLoader;
import com.geotop.geotopproject.loader.helper.CollisionResolver;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.deserializer.PlaceDeserializer;
import com.geotop.geotopproject.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Component
public class FBLoaderImpl implements FBLoader{
    private static final Logger LOG = LoggerFactory.getLogger(FBLoaderImpl.class);

    private CollisionResolver collisionResolver;
    private PlaceDeserializer placeDeserializer;

    @Autowired
    public FBLoaderImpl(CollisionResolver collisionResolver, PlaceDeserializer placeDeserializerFB){
        this.collisionResolver = collisionResolver;
        this.placeDeserializer = placeDeserializerFB;
    }

    @Override
    public List<Place> loadData() throws Exception {
        List<Place> places = loadPlaces();
        places = collisionResolver.resolvePlaceCollision(places);

        LOG.info("fb loading done");
        return places;
    }

    private List<Place> loadPlaces() throws Exception {
        List<Place> places = new ArrayList<>();
        String url = String.format(API_PLACES_URL, COORDINATES, ACCESS_TOKEN);
        places = callPlaceAPI(places, url);
        places = deleteDuplicatePlaces(places);
        return places;
    }

    private List<Place> callPlaceAPI(List<Place> places, String url) throws Exception {
        LOG.info(url);
        String json = Utils.decodeUnicode(IOUtils.toString(new URL(url).openStream()));
        LOG.info(json);

        int nextUrlIndex = json.indexOf("\"next\":");
        if (nextUrlIndex == -1){
            // finish loading and return places
            String data = json
                    .replaceAll("\\{\"data\":","")
                    .replaceAll("]\\}$","]")
                    .replaceAll("\"\\}\\}","\"\\}");
            places.addAll(parseData(data));
            return places;
        } else {
            // load more places
            String nextUrl = json.substring(json.indexOf("\"next\":") + 8)
                    .replaceAll("\"\\}\\}$","")
                    .replaceAll("\\\\", "");
            String data = json
                    .replaceAll("\\{\"data\":","")
                    .replaceAll("],.+\\}$","]")
                    .replaceAll("\"\\}\\}","\"\\}");
            places.addAll(parseData(data));
            return callPlaceAPI(places, nextUrl);
        }
    }

    private List<Place> parseData(String json){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Place.class, placeDeserializer);
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<ArrayList<Place>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    private List<Place> deleteDuplicatePlaces(List<Place> places) throws Exception {
        List<Place> uniquePlace = places.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getFbData().getId()))),
                        ArrayList::new));
        return uniquePlace;
    }
}
