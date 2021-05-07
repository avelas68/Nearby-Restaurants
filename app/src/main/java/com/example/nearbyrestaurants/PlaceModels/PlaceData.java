package com.example.nearbyrestaurants.PlaceModels;

import com.google.android.gms.maps.model.LatLng;

public class PlaceData {
    private String name;
    private String address;
    private String id;

    private double rating;
    private LatLng latLng;
    private double distance;
    private int price_level;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public double getDistance() {
        return distance;
    }

    public String getID() {
        return id;
    }

    public int getPrice() {
        return price_level;
    }

    public PlaceData(String name, String address, String id, double rating, LatLng latLng, double distance, int price_level) {
        this.name = name;
        this.address = address;
        this.id = id;
        this.rating = rating;
        this.latLng = latLng;
        this.distance = distance;
        this.price_level = price_level;
    }

    @Override
    public String toString() {
        return "PlaceDetail{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", rating=" + rating +
                ", latLng=" + latLng +
                ", distance=" + distance +
                ", price=" + price_level +
                '}';
    }
}