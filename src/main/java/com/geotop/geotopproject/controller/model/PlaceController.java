package com.geotop.geotopproject.controller.model;

import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.service.PlaceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rest")
public class PlaceController {

    private static Logger LOG = Logger.getLogger(PlaceController.class.getName());

    private PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @RequestMapping(value = "/places", method = RequestMethod.GET)
    public List<Place> getPlacesByType(@RequestParam(value = "type") String type) {
        return placeService.getPlacesByType(type);
    }

    @RequestMapping(value = "/top_places", method = RequestMethod.GET)
    public List<Place> getTopPlacesByType(@RequestParam(value = "type") String type) {
        return placeService.getTopPlacesByType(type);
    }

    @RequestMapping(value = "/place", method = RequestMethod.GET)
    public Place getPlaceById(@RequestParam(value = "id") String id) {
        return placeService.getPlaceById(id);
    }

}
