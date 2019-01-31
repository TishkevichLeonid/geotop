package com.geotop.geotopproject.service;

import com.geotop.geotopproject.dao.PlaceCollectionRepository;
import com.geotop.geotopproject.dao.PlaceRepository;
import com.geotop.geotopproject.exception.ExceptionMessageConstants;
import com.geotop.geotopproject.exception.NotFoundException;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.PlaceCollection;
import com.geotop.geotopproject.utils.Utils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceService {
    private static Logger LOG = Logger.getLogger(PlaceService.class.getName());

    private PlaceRepository placeRepository;
    private PlaceCollectionRepository placeCollectionRepository;

    @Autowired
    public PlaceService(PlaceRepository placeRepository, PlaceCollectionRepository placeCollectionRepository) {
        this.placeRepository = placeRepository;
        this.placeCollectionRepository = placeCollectionRepository;
    }

    public List<Place> getTopPlacesByType(String type) throws NotFoundException {
        List<Place> places = getPlacesByType(type);

        return places.stream()
                .sorted((p1, p2) -> Double.compare((p2.getOverallPopularity() + p2.getOverallRating()),
                        (p1.getOverallPopularity() + p1.getOverallRating())))
                .collect(Collectors.toList());
    }

    public List<Place> getPlacesByType(String type) throws NotFoundException {
        List<PlaceCollection> placeCollections = placeCollectionRepository.findByTitle(type);
        if (placeCollections == null || placeCollections.isEmpty()) {
            throw new NotFoundException(String.format(ExceptionMessageConstants.NF_TYPE_NOT_FOUND, type));
        }
        List<Place> places;
        try {
            places = Utils.getSingle(placeCollections).getPlaces();
            if (places == null || places.isEmpty()) {
                throw new NotFoundException(String.format(ExceptionMessageConstants.NF_PLACES_NOT_FOUND, type));
            }
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage(), e.getCause());
        }

        return places;
    }

    public Place getPlaceById(String id) throws NotFoundException {
        Place place;
        try {
            place = placeRepository.findById(id);
        } catch (NotFoundException e) {
            LOG.error(e.getMessage());
            throw new NotFoundException(String.format(ExceptionMessageConstants.NF_PLACE_NOT_FOUND, id), e.getCause());
        }

        return place;
    }
}
