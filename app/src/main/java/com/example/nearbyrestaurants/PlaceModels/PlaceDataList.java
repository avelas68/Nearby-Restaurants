package com.example.nearbyrestaurants.PlaceModels;

import java.util.List;

public interface PlaceDataList {
    List<PlaceData> getPlaces();

    void addPlace(PlaceData newPlace);

    List<PlaceData> sortDistance();

    List<PlaceData> sortRating();

    List<PlaceData> sortPrice();
}