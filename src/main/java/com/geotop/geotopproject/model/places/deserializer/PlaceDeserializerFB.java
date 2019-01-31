package com.geotop.geotopproject.model.places.deserializer;

import com.geotop.geotopproject.loader.helper.CityDictionary;
import com.geotop.geotopproject.model.places.APISpecificData;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.api.FbData;
import com.geotop.geotopproject.utils.Utils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

@Component("placeDeserializerFB")
public class PlaceDeserializerFB implements PlaceDeserializer {

    private CityDictionary cityDictionary;

    @Autowired
    public PlaceDeserializerFB(CityDictionary cityDictionary) {
        this.cityDictionary = cityDictionary;
    }

    @Override
    public Place deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        Place place = new Place();

        place.setId(Utils.generateID());
        setFbData(place, obj);
        return place;
    }

    private void setFbData(Place place, JsonObject obj){
        JsonElement fbId = obj.get("id");
        APISpecificData fbData = new FbData();
        fbData.setId(fbId.getAsString());

        JsonElement category = obj.get("category");
        String categoryStr = category.getAsString();
//        place.setFbCategory(categoryStr);

        List<Set<String>> cityTypes = cityDictionary.getCityTypesFB();
        for (int i = 0; i < cityTypes.size(); i++){
            for (String value : cityTypes.get(i)){
                if (categoryStr.toLowerCase().matches(Utils.wrapBoundaries(value))){
                    place.setType(i + 1);
                }
            }
        }

        if (place.getType() == null) {
            place.setType(11);
        }

        JsonElement name = obj.get("name");
        place.setTitle(name.getAsString());

        JsonElement link = obj.get("link");
        fbData.setLink(link.getAsString().replaceAll("\\\\",""));

        JsonObject location = obj.getAsJsonObject("location");
        JsonElement latitude = location.get("latitude");
        place.setLatitude(latitude.getAsDouble());
        JsonElement longitude = location.get("longitude");
        place.setLongitude(longitude.getAsDouble());
        JsonElement street = location.get("street");
        if (street == null){
            place.setAddress("");
        } else {
            place.setAddress(street.getAsString());
        }

        JsonElement checkins = obj.get("checkins");
        fbData.setCheckinsCount(checkins.getAsInt());

        JsonElement ratingCount = obj.get("rating_count");
        fbData.setRatingCount(ratingCount.getAsInt());

        JsonElement overallStarRating = obj.get("overall_star_rating");
        if (overallStarRating == null){
            fbData.setRating(null);
        } else {
            fbData.setRating(overallStarRating.getAsDouble());
        }

        place.setFbData(fbData);

        JsonObject picture = obj.getAsJsonObject("picture");
        JsonElement urlpicture = picture.get("url");
        place.setPicture(urlpicture.getAsString().replaceAll("\\\\",""));
    }
}
