package com.geotop.geotopproject.loader.helper;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Component
public class CityDictionary {
    private Set<String> cityStreets;
    private List<Set<String>> cityTypes;
    private List<Set<String>> cityTypesFB;
    private Set<String> cityDictionary;

    @PostConstruct
    public void init() {
        parseCityDictionary();
    }

    public Set<String> getCityDictionary() { return cityDictionary;}

    public Set<String> getCityStreets() {
        return cityStreets;
    }

    public List<Set<String>> getCityTypes(){
        return cityTypes;
    }

    public List<Set<String>> getCityTypesFB(){
        return cityTypesFB;
    }

    private void parseCityDictionary(){
        try {
            parseCityStreets();
            parseCityTypes();
            parseCityTypesFB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        createCityDictionary();
    }

    private void createCityDictionary(){
        cityDictionary = new HashSet<>();
        cityDictionary.addAll(cityStreets);
        for (int i = 0; i < cityTypes.size() - 3; i++){
            cityDictionary.addAll(cityTypes.get(i));
        }
    }

    private void parseCityStreets() throws IOException {
        cityStreets = new HashSet<>();
        Resource res = new ClassPathResource("dictionary/cityStreets");
        BufferedReader br = new BufferedReader(new FileReader(res.getFile()));
        String line;
        while ((line = br.readLine()) != null){
            cityStreets.add(line);
        }
    }

    private void parseCityTypes() throws IOException {
        cityTypes = new ArrayList<>();
        Resource res = new ClassPathResource("dictionary/cityTypes");
        BufferedReader br = new BufferedReader(new FileReader(res.getFile()));
        String line;
        while ((line = br.readLine()) != null){
            if (line.isEmpty()){
                cityTypes.add(new HashSet<>());
            } else {
                cityTypes.get(cityTypes.size() - 1).add(line);
            }
        }
    }


    private void parseCityTypesFB() throws IOException {
        cityTypesFB = new ArrayList<>();
        Resource res = new ClassPathResource("dictionary/cityTypesFB");
        BufferedReader br = new BufferedReader(new FileReader(res.getFile()));
        String line;
        while ((line = br.readLine()) != null){
            if (line.isEmpty()){
                cityTypesFB.add(new HashSet<>());
            } else {
                cityTypesFB.get(cityTypesFB.size() - 1).add(line);
            }
        }
    }

    public String removeCityWords(String place){
        place = place.toLowerCase();
        String[] title = place.split("[-,\\s+]");
        for (String word: title){
            if (cityDictionary.contains(word)){
                place = place.replaceAll(word, "");
            }
        }
        return place;
    }
}
