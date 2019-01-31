package com.geotop.geotopproject.model.places.api;

import com.geotop.geotopproject.model.places.APISpecificData;
import com.geotop.geotopproject.model.places.Checkin;

import java.util.List;

public class VkData extends APISpecificData {
    private String id;
    private List<Checkin> checkins;
    private Integer checkinsCount;

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
        return checkins;
    }

    @Override
    public void setCheckins(List<Checkin> checkins) {
        this.checkins = checkins;
        if (checkins != null) {
            this.checkinsCount = checkins.size();
        }
    }

    @Override
    public Integer getCheckinsCount() {
        return (checkinsCount == null) ? 0 : this.checkinsCount;
    }

    @Override
    public void setCheckinsCount(Integer checkinsCount) {
    }

    @Override
    public Integer getRatingCount() {
        return null;
    }

    @Override
    public String getLink() {
        return null;
    }

    @Override
    public void setRatingCount(Integer ratingCount) {
    }

    @Override
    public void setLink(String link) {

    }


}
