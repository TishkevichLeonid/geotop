package com.geotop.geotopproject.model.places;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="collection")
public class Place {

    private static final Logger LOG = LoggerFactory.getLogger(Place.class);

    @Id
    private String id;

    // common data
    private String title;
    private double latitude;
    private double longitude;
    private Integer type;
    private String address;
    private Integer overallCheckinsCount; // will be used in future (when checkins will be collected during some time)
    private Double overallRating;
    private Double overallPopularity;
    private String picture;

//    // vk data
//    private String vkId;
//    @Field("vkCheckins")
//    private List<Checkin> vkCheckins;
//    private String vkRating;
//    private Double vkPopularity;
//
//    // fb data
//    private String fbId;
//    private String fbLink;
//    private Integer fbCheckinsAmount;
//    private Integer fbRatingCount;
//    private Double fbRating;
//    private String fbCategory;
//    private String picture;
//    private Double fbPopularity;

    @Field("vkData")
    private APISpecificData vkData;
    @Field("fbData")
    private APISpecificData fbData;

    //technical
    private Boolean alone;

    public Place() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOverallCheckinsCount() {
        return overallCheckinsCount;
    }

    public void setOverallCheckinsCount(Integer overallCheckinsCount) {
        this.overallCheckinsCount = overallCheckinsCount;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public Double getOverallPopularity() {
        return overallPopularity;
    }

    public void setOverallPopularity(Double overallPopularity) {
        this.overallPopularity = overallPopularity;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public APISpecificData getVkData() {
        return vkData;
    }

    public void setVkData(APISpecificData vkData) {
        this.vkData = vkData;
    }

    public APISpecificData getFbData() {
        return fbData;
    }

    public void setFbData(APISpecificData fbData) {
        this.fbData = fbData;
    }

    public Boolean isAlone() {
        return alone;
    }

    public void setAlone(Boolean alone) {
        this.alone = alone;
    }

    @Override
    public String toString() {
        return String.format("Place[id=%s, title=%s, address=%s, lt=%s, lg=%s]",
                id, title, address, latitude, longitude);
    }

}
