package com.geotop.geotopproject.dao.impl;

import com.geotop.geotopproject.dao.PlaceOperations;
import com.geotop.geotopproject.exception.NotFoundException;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.PlaceCollection;
import com.geotop.geotopproject.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class PlaceRepositoryImpl implements PlaceOperations {
    private static final Logger LOG = LoggerFactory.getLogger(PlaceRepositoryImpl.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public PlaceRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Place findById(String id) throws NotFoundException {
        TypedAggregation<PlaceCollection> agg = newAggregation(
                PlaceCollection.class,
                match(Criteria.where("places._id").is(id)),
                unwind("places"),
                match(Criteria.where("places._id").is(id)),
                group().addToSet("places").as("places")
        );

        AggregationResults<PlaceCollection> aggPlaceCollection = mongoTemplate.aggregate(agg, PlaceCollection.class);
        List<PlaceCollection> mappedResults = aggPlaceCollection.getMappedResults();

        Place place;
        try {
            PlaceCollection placeCollection = Utils.getSingle(mappedResults);
            List<Place> places = placeCollection.getPlaces();
            place = Utils.getSingle(places);
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage(), e.getCause());
        }

        return place;
    }

    @Override
    public List<Place> getAllPlacesByType(String type) {
        Aggregation agg = newAggregation(
                match(Criteria.where("title").is(type)),
                unwind("places"),
                project().and("_id").as("id")
                        .and("places.title").as("title")
                        .and("places.longitude").as("longitude")
                        .and("places.latitude").as("latitude")
                        .and("places.address").as("address")
        );

        AggregationResults<Place> places = mongoTemplate.aggregate(agg, PlaceCollection.class, Place.class);

        return places.getMappedResults();
    }
}
