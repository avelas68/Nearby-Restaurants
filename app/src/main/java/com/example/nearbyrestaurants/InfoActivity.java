package com.example.nearbyrestaurants;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nearbyrestaurants.PlaceModels.ListImplementation;
import com.example.nearbyrestaurants.PlaceModels.PlaceDataList;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.nearbyrestaurants.AppConfiguration.OK;
import static com.example.nearbyrestaurants.AppConfiguration.STATUS;

public class InfoActivity extends AppCompatActivity {
    private ImageView menuButton;
    public PlaceDataList restaurants;
    String iPosition = "hello";
    TextView restName, restAdd, restPhone;
    ImageView restPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        restaurants = ListImplementation.sharedInstance();
        Intent mIntent = getIntent();
        int positionNumb = mIntent.getIntExtra("itemPosition", 0);

        restName = (TextView) findViewById(R.id.rest_name);
        iPosition = restaurants.getPlaces().get(positionNumb).getName();
        restName.setText(iPosition);

        restAdd = (TextView) findViewById(R.id.rest_address);
        iPosition = restaurants.getPlaces().get(positionNumb).getAddress();
        restAdd.setText(iPosition);

        menuButton = (ImageView) findViewById(R.id.title);

        initInfoActivity();
        loadDetails();
    }

    private void initInfoActivity() {
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InfoActivity.this, MainMenu.class);
                startActivity(i);
            }
        });
    }

    private void loadDetails() {
        Intent mIntent = getIntent();
        int positionNumb = mIntent.getIntExtra("itemPosition", 0);

        //https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJ5V-QXKiwj4ARv5e-BSB9fiA&fields=formatted_phone_number,photo,address_component&key=AIzaSyCEpLRjoupchPtJoXt9Wd50OXWRtkQ4Fgk
        StringBuilder restaurantUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        restaurantUrl.append("placeid=" + restaurants.getPlaces().get(positionNumb).getID());
        restaurantUrl.append("&fields=formatted_phone_number,photo,address_component");
        restaurantUrl.append("&key=AIzaSyCEpLRjoupchPtJoXt9Wd50OXWRtkQ4Fgk");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, restaurantUrl.toString(), (String) null,

                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject result) {
                        jsonParse(result);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }

    private void jsonParse(JSONObject result) {
        String phoneNumber;
        String photoRef;

        try {
            JSONObject jObj = result.getJSONObject("result");
            if (result.getString(STATUS).equalsIgnoreCase(OK)) {
                if (!jObj.isNull("formatted_phone_number")) {
                    phoneNumber = jObj.getString("formatted_phone_number");
                    restPhone = (TextView) findViewById(R.id.rest_phone);
                    restPhone.setText(phoneNumber);
                }

                JSONArray jPhotoArray = jObj.getJSONArray("photos");
                JSONObject jPhoto = jPhotoArray.getJSONObject(0);
                photoRef = jPhoto.getString("photo_reference");
                //https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&maxheight=100&photoreference=[ref]&key=AIzaSyCEpLRjoupchPtJoXt9Wd50OXWRtkQ4Fgk
                StringBuilder picture = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
                picture.append("maxwidth=1500&maxheight=1000");
                picture.append("&photoreference=" + photoRef);
                picture.append("&key=AIzaSyCEpLRjoupchPtJoXt9Wd50OXWRtkQ4Fgk");
                System.out.println(photoRef);
                restPhoto = (ImageView) findViewById(R.id.rest_photo);
                loadImageFromWeb(picture.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadImageFromWeb(String url) {
        Picasso.with(InfoActivity.this).load(url).resize(1150, 1150).centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(restPhoto, new com.squareup.picasso.Callback() {

            @Override
            public void onSuccess() {
                //do smth when picture is loaded successfully
            }

            @Override
            public void onError() {
                //do smth when there is picture loading error
            }
        });
    }
}