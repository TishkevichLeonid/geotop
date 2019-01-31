package com.geotop.geotopproject.model.places;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="collection")
public abstract class APISpecificData {

    public abstract List<Checkin> getCheckins();
    public abstract Integer getCheckinsCount();
    public abstract Integer getRatingCount();
    public abstract String getLink();
    public abstract void setCheckins(List<Checkin> checkins);
    public abstract void setCheckinsCount(Integer checkinsCount);
    public abstract void setRatingCount(Integer count);
    public abstract void setLink(String link);

    @Id
    private String id;
    private Double rating;
    private Double popularity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getRating() {
        return (this.rating == null) ? 0 : this.rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }
}
