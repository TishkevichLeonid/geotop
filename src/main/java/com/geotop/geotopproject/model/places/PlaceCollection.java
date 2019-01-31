package com.geotop.geotopproject.model.places;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;


@Document(collection="collection")
public class PlaceCollection {
    @Id
    private Integer id;
    private String title;
    @Field("places")
    private List<Place> places;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public void addPlace(Place place) {
        places.add(place);
    }

    @Override
    public String toString() {
        return String.format("PlaceType[id=%s, title=%s]", id, title);
    }
}
