package com.geotop.geotopproject.model.places.deserializer;

import com.geotop.geotopproject.loader.helper.CityDictionary;
import com.geotop.geotopproject.model.places.APISpecificData;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.api.VkData;
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

@Component("placeDeserializerVK")
public class PlaceDeserializerVK implements PlaceDeserializer {

    private CityDictionary cityDictionary;

    @Autowired
    public PlaceDeserializerVK(CityDictionary cityDictionary) {
        this.cityDictionary = cityDictionary;
    }

    @Override
    public Place deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        Place place = new Place();

        place.setId(Utils.generateID());
        setVkData(place, obj);
        return place;
    }

    private void setVkData(Place place, JsonObject obj){
        JsonElement vkId = obj.get("id");
        APISpecificData vkData = new VkData();
        vkData.setId(vkId.getAsString());

        JsonElement title = obj.get("title");
        place.setTitle(title.getAsString());

        JsonElement vkLatitude = obj.get("latitude");
        place.setLatitude(vkLatitude.getAsDouble());

        JsonElement vkLongitude = obj.get("longitude");
        place.setLongitude(vkLongitude.getAsDouble());

        JsonElement typeElem = obj.get("type");
        Integer type = typeElem.getAsInt();
        place.setType(type);
        if (type == 1 || type == 2 || type == 8 || type == 21 ||
                type == 9 || type == 10 || type == 15){
            place.setType(11);
        }
        List<Set<String>> cityTypes = cityDictionary.getCityTypes();
        for (int i = 0; i < cityTypes.size(); i++){
            for (String value : cityTypes.get(i)){
                if (title.getAsString().toLowerCase().matches(Utils.wrapBoundaries(value))){
                    place.setType(i + 1);
                }
            }
        }
        JsonElement address = obj.get("address");
        if (address == null){
            place.setAddress("");
        } else {
            place.setAddress(address.getAsString());
        }

        place.setVkData(vkData);
    }
}
