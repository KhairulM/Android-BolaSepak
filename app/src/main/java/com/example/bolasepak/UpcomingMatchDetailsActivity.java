package com.example.bolasepak;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.List;

public class UpcomingMatchDetailsActivity extends FragmentActivity implements OnMapReadyCallback, AsyncGeocoderResponse {
    private final String TAG = "UpcomingMatchDetails";
    private String matchDate, matchHighlighted, homeTeamName, awayTeamName, homeBadgeURL, awayBadgeURL, matchLocation, matchCity, matchId;
    private List<Address> addressList;
    private Forecast[] forecasts;
    public GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_match_details);

        // google map integration
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.MapView);
        mapFragment.getMapAsync(this);

        // set header details from intent extras
        Intent intent = getIntent();
        matchDate = intent.getStringExtra("MATCH_DATE");
        matchLocation = intent.getStringExtra("MATCH_LOCATION");
        matchCity = intent.getStringExtra("MATCH_CITY");
        matchHighlighted = intent.getStringExtra("MATCH_HIGHLIGHTED");
        homeTeamName = intent.getStringExtra("MATCH_HOME_TEAM_NAME");
        awayTeamName = intent.getStringExtra("MATCH_AWAY_TEAM_NAME");
        homeBadgeURL = intent.getStringExtra("MATCH_HOME_BADGE_URL");
        awayBadgeURL = intent.getStringExtra("MATCH_AWAY_BADGE_URL");
        matchId = intent.getStringExtra("MATCH_ID");

        ((TextView) findViewById(R.id.MatchDate)).setText(String.format("%s ,%s", matchCity, matchDate));
        ((TextView) findViewById(R.id.MatchHighlighted)).setText(matchHighlighted);
        ((TextView) findViewById(R.id.HomeTeamName)).setText(homeTeamName);
        ((TextView) findViewById(R.id.AwayTeamName)).setText(awayTeamName);

        Glide.with(this).load(homeBadgeURL).into((ImageView) findViewById(R.id.HomeBadge));
        Glide.with(this).load(awayBadgeURL).into((ImageView) findViewById(R.id.AwayBadge));

        // set onClickListener for Home and Away team badge
        ((ImageView) findViewById(R.id.HomeBadge)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TeamDetailsActivity.class);

                intent.putExtra("TEAM_NAME", homeTeamName);
                intent.putExtra("TEAM_BADGE_URL", homeBadgeURL);

                startActivity(intent);
            }
        });

        ((ImageView) findViewById(R.id.AwayBadge)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TeamDetailsActivity.class);

                intent.putExtra("TEAM_NAME", awayTeamName);
                intent.putExtra("TEAM_BADGE_URL", awayBadgeURL);

                startActivity(intent);
            }
        });

        // request weather forecast by city
        String baseForecastURL = "https://api.openweathermap.org/data/2.5/forecast";
        String locParam = "q";
        String apiParam = "appid";

        Uri forecastURI = Uri.parse(baseForecastURL).buildUpon()
                .appendQueryParameter(locParam, matchCity)
                .appendQueryParameter(apiParam, getResources().getString(R.string.weather_key))
                .build();

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.start();
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET,
                forecastURI.toString(),
                null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray arr = response.getJSONArray("list");
                            JSONObject json = arr.getJSONObject(0);
                            arr = json.getJSONArray("weather");
                            json = arr.getJSONObject(0);
                            String weather = json.getString("main");

                            onWeatherResponse(weather);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.getStackTrace();
                    }
                });
        queue.add(jor);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // default latitude and longitude if city not found
        LatLng loc = new LatLng(-6.417642, 106.860671);
        map = googleMap;

        AsyncGeocoderTask agt = new AsyncGeocoderTask(this);
        agt.delegate = this;
        agt.execute(matchLocation);

        map.addMarker(new MarkerOptions().position(loc).title("City can't be found"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13));
    }

    @Override
    public void onGeocoderFinished(List<Address> addressList) {
        this.addressList = addressList;

        if (addressList.size() != 0){
            Address addr = addressList.get(0);
            LatLng loc = new LatLng(addr.getLatitude(), addr.getLongitude());

            map.clear();
            map.addMarker(new MarkerOptions().position(loc).title(matchLocation));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13));
        }
    }

    public void onWeatherResponse(String weather){
        //weather di sini udh = Clouds, Sunny, dll

        switch (weather){
            case "Clouds":
                ((ImageView) findViewById(R.id.WeatherImage)).setImageDrawable(getResources().getDrawable(R.drawable.clouds));
                break;
            case "Rain":
                ((ImageView) findViewById(R.id.WeatherImage)).setImageDrawable(getResources().getDrawable(R.drawable.rain));
                break;
            case "Clear":
                ((ImageView) findViewById(R.id.WeatherImage)).setImageDrawable(getResources().getDrawable(R.drawable.clear));
                break;
        }

        ((TextView) findViewById(R.id.WeatherDescription)).setText(weather);

        //Handle frontend nya. ganti image, sama set text, tergantung value weather
        //Log.d(TAG, "onWeatherResponse: "+weather);
    }
}
