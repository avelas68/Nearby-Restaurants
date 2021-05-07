package com.example.nearbyrestaurants;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nearbyrestaurants.PlaceModels.PlaceData;
import com.example.nearbyrestaurants.PlaceModels.PlaceDataList;
import com.example.nearbyrestaurants.PlaceModels.ListImplementation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.nearbyrestaurants.AppConfiguration.GEOMETRY;
import static com.example.nearbyrestaurants.AppConfiguration.LATITUDE;
import static com.example.nearbyrestaurants.AppConfiguration.LOCATION;
import static com.example.nearbyrestaurants.AppConfiguration.LONGITUDE;
import static com.example.nearbyrestaurants.AppConfiguration.NAME;
import static com.example.nearbyrestaurants.AppConfiguration.OK;
import static com.example.nearbyrestaurants.AppConfiguration.PLACE_ID;
import static com.example.nearbyrestaurants.AppConfiguration.RATING;
import static com.example.nearbyrestaurants.AppConfiguration.STATUS;
import static com.example.nearbyrestaurants.AppConfiguration.ADDRESS;
import static com.example.nearbyrestaurants.AppConfiguration.ZERO_RESULTS;
import static com.example.nearbyrestaurants.AppConfiguration.PRICE;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final float DEFAULT_ZOOM = 15f;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private ImageView mGPS;
    private ImageView menuButton;
    private Button getListButton;
    private PlaceData mPlace;
    private LatLng currentlatlng;

    public PlaceDataList restaurants;
    public float SHOWING_RESTAURANTS_ZOOM = 14f;
    public int PROXIMITY_RADIUS = 1609;
    public int cost = 1;
    public String cuisine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restaurants = ListImplementation.sharedInstance();
        mGPS = (ImageView) findViewById(R.id.gps);
        menuButton = (ImageView) findViewById(R.id.title);
        getListButton = (Button) findViewById(R.id.get_list);
        getLocationPermission();
        Bundle bundle = getIntent().getExtras();
        cost = bundle.getInt("price");
        PROXIMITY_RADIUS = 1609 * (bundle.getInt("distance"));
        cuisine = bundle.getString("cuisine");
    }

    private void init() {
        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainMenu.class);
                startActivity(i);
            }
        });

        getListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RecyclerViewActivity.class);
                startActivity(i);
            }
        });
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            final Place place = places.get(0);
            try {
                mPlace = new PlaceData(place.getName().toString(), place.getAddress().toString(),
                        place.getId().toString(), place.getRating(), place.getLatLng(), 0, place.getPriceLevel());

            } catch (NullPointerException e) {
            }
            currentlatlng = mPlace.getLatLng();
            moveCameraZoom(currentlatlng, DEFAULT_ZOOM, null);
            places.release();
        }
    };

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            currentlatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            moveCameraZoom(currentlatlng, DEFAULT_ZOOM, null);
                            loadNearByPlaces(currentlatlng.latitude, currentlatlng.longitude);
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to get current location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
    }

    private void moveCameraZoom(LatLng latLng, float zoom, Circle circle) {
        int zoomLevel;
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
        MarkerOptions options = new MarkerOptions().position(latLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(options);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void loadNearByPlaces(double latitude, double longitude) {

        StringBuilder restaurantUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        restaurantUrl.append("query=" + cuisine + "+restaurant");
        restaurantUrl.append("&location=").append(latitude).append(",").append(longitude);
        restaurantUrl.append("&radius=").append(PROXIMITY_RADIUS);
        restaurantUrl.append("&types=restaurant");
        restaurantUrl.append("&sensor=true");
        restaurantUrl.append("&key=" + getResources().getString(R.string.places_api_key));


        JsonObjectRequest request = new JsonObjectRequest(restaurantUrl.toString(),

                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject result) {
                        parseLocationResult(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }

    private void parseLocationResult(JSONObject result) {
        String place_id, placeAddress = "", placeName = null;
        double placeRating = 0, placeDistance, placePrice;
        double latitude, longitude;

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {

                mMap.clear();
                if (!restaurants.getPlaces().isEmpty()) {
                    restaurants.getPlaces().clear();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    place_id = place.getString(PLACE_ID);

                    if (!place.isNull(NAME)) {
                        placeName = place.getString(NAME);
                    }
                    if (!place.isNull(RATING)) {
                        placeRating = place.getDouble(RATING);
                    }
                    if (!place.isNull(ADDRESS)) {
                        placeAddress = place.getString(ADDRESS);
                    }
                    if (!place.isNull(PRICE)) {
                        placePrice = place.getInt(PRICE);
                    } else {
                        placePrice = 0;
                    }

                    if (placePrice > cost) {
                        continue;
                    }

                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION).getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION).getDouble(LONGITUDE);
                    LatLng latLng = new LatLng(latitude, longitude);
                    double R = 6371; // Radius of the earth in km
                    double dLat = (latitude - currentlatlng.latitude) * (Math.PI / 180);
                    double dLon = (longitude - currentlatlng.longitude) * (Math.PI / 180);
                    double a =
                            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                    Math.cos(latitude * (Math.PI / 180)) * Math.cos(currentlatlng.latitude * (Math.PI / 180)) *
                                            Math.sin(dLon / 2) * Math.sin(dLon / 2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                    if (((R * c) * 1000) > PROXIMITY_RADIUS) {
                        continue;
                    }
                    placeDistance = (R * c) / 1.609344; // Distance in km

                    PlaceData places = new PlaceData(placeName, placeAddress, place_id, placeRating, latLng, placeDistance, (int) placePrice);
                    restaurants.addPlace(places);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(placeName);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    mMap.addMarker(markerOptions);
                }
                Circle circle = mMap.addCircle(new CircleOptions()
                        .radius(PROXIMITY_RADIUS).center(currentlatlng).strokeColor(Color.BLACK).fillColor(0x30ff0000)
                );
                moveCameraZoom(currentlatlng, SHOWING_RESTAURANTS_ZOOM, circle);
                Toast.makeText(getBaseContext(), restaurants.getPlaces().size() + " restaurants found!", Toast.LENGTH_LONG).show();

            } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
                Toast.makeText(getBaseContext(), "No restaurants found!",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}