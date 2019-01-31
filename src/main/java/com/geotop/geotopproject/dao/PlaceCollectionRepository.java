package com.geotop.geotopproject.dao;

import com.geotop.geotopproject.model.places.PlaceCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlaceCollectionRepository extends MongoRepository<PlaceCollection, String> {
    PlaceCollection findById(Integer id);
    List<PlaceCollection> findByTitle(String title);
    List<PlaceCollection> findAll();
}
