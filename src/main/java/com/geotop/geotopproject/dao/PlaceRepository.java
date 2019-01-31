package com.geotop.geotopproject.dao;

import com.geotop.geotopproject.model.places.Place;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlaceRepository extends MongoRepository<Place, String>, PlaceOperations {
    List<Place> findByTitle(String title);
}
