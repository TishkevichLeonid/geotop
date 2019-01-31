package com.geotop.geotopproject.model.places.api;

import com.geotop.geotopproject.model.places.APISpecificData;
import com.geotop.geotopproject.model.places.Checkin;

import java.util.List;

public class FbData extends APISpecificData {

    private String id;
    private Integer checkinsCount;
    private Integer ratingCount;
    private String link;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<Checkin> getCheckins() {
        return null;
    }

    @Override
    public void setCheckins(List<Checkin> checkins) {
    }

    @Override
    public Integer getCheckinsCount() {
        return checkinsCount;
    }

    @Override
    public void setCheckinsCount(Integer checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    @Override
    public Integer getRatingCount() {
        return ratingCount;
    }

    @Override
    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }
}
