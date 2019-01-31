package com.geotop.geotopproject.service;

import com.geotop.geotopproject.dao.PlaceCollectionRepository;
import com.geotop.geotopproject.loader.CommonLoader;
import com.geotop.geotopproject.loader.helper.CollisionResolver;
import com.geotop.geotopproject.loader.helper.PopularityRatingProcessor;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.PlaceCollection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LoaderService {

    private static final Logger LOG = LoggerFactory.getLogger(LoaderService.class);

    private PlaceCollectionRepository placeCollectionRepository;
    private CollisionResolver collisionResolver;
    private PopularityRatingProcessor popularityRatingProcessor;

    public String INIT_COLLECTION_JSON = "[" +
            "{\"id\":1,\"title\":\"Cafe\",\"places\":[]}," +
            "{\"id\":2,\"title\":\"Club\",\"places\":[]}," +
            "{\"id\":3,\"title\":\"Entertainment\",\"places\":[]}," +
            "{\"id\":4,\"title\":\"Bar\",\"places\":[]}," +
            "{\"id\":5,\"title\":\"Movie Theater\",\"places\":[]}," +
            "{\"id\":6,\"title\":\"Hotel\",\"places\":[]}," +
            "{\"id\":7,\"title\":\"Educational Institution\",\"places\":[]}," +
            "{\"id\":8,\"title\":\"Museum\",\"places\":[]}," +
            "{\"id\":9,\"title\":\"Theater\",\"places\":[]}," +
            "{\"id\":10,\"title\":\"Park\",\"places\":[]}," +
            "{\"id\":11,\"title\":\"Other\",\"places\":[]}" +
            "]";

    @Autowired
    public LoaderService(PlaceCollectionRepository placeCollectionRepository, CollisionResolver collisionResolver, PopularityRatingProcessor popularityRatingProcessor) {
        this.placeCollectionRepository = placeCollectionRepository;
        this.collisionResolver = collisionResolver;
        this.popularityRatingProcessor = popularityRatingProcessor;
    }

    public List<Place> load(CommonLoader loader) throws Exception {
        return loader.loadData();
    }

    public void save(List<PlaceCollection> placeCollections) throws Exception {
        placeCollectionRepository.save(placeCollections);
    }

    public List<PlaceCollection> categorizeAndIndexPlaces(List<Place> places) {
        List<PlaceCollection> placeCollections = placeCollectionRepository.findAll();
        placeCollections.forEach(placeType -> {
            List<Place> placesFiltered = places.stream().filter(place -> place.getType().equals(placeType.getId())).collect(Collectors.toList());
            List<Place> placesPopularityIndexed = popularityRatingProcessor.indexPlaces(placesFiltered);
            placeType.setPlaces(placesPopularityIndexed);
        });

        return placeCollections;
    }

    public void initPlacesTypes() {
        Type listType = new TypeToken<ArrayList<PlaceCollection>>(){}.getType();
        List<PlaceCollection> placeCollections = new Gson().fromJson(INIT_COLLECTION_JSON, listType);
        for (PlaceCollection placeCollection : placeCollections) {
            LOG.info("initPlaceTypes: " + placeCollection.getTitle());
            if (placeCollectionRepository.findById(placeCollection.getId()) == null) {
                placeCollectionRepository.insert(placeCollection);
            }
        }
    }

    public List<Place> resolveCollision(List<Place> places) {
        return collisionResolver.resolvePlaceCollision(places);
    }

    @SafeVarargs
    public final List<Place> collectPlaces(List<Place>... lists) {
        List<List<Place>> listPlaces = Arrays.asList(lists);
        Stream<Place> stream = listPlaces.stream().flatMap(Collection::stream);
        return stream.collect(Collectors.toList());
    }

}
