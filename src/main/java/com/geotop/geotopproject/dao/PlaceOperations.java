package com.geotop.geotopproject.dao;

import com.geotop.geotopproject.exception.NotFoundException;
import com.geotop.geotopproject.model.places.Place;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlaceOperations {
    Place findById(String id) throws NotFoundException;
    List<Place> getAllPlacesByType(String type);
}
