package com.example.bolasepak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.BroadcastReceiver;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private String cursorToString(Cursor cursor){
        String cursorString = "";
        if (cursor.moveToFirst() ){
            String[] columnNames = cursor.getColumnNames();
            for (String name: columnNames)
                cursorString += String.format("%s ][ ", name);
            cursorString += "\n";
            do {
                for (String name: columnNames) {
                    cursorString += String.format("%s ][ ",
                            cursor.getString(cursor.getColumnIndex(name)));
                }
                cursorString += "\n";
            } while (cursor.moveToNext());
        }
        return cursorString;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String countedStep = intent.getStringExtra("Counted_Step");

            TextView textView = (TextView) findViewById(R.id.StepCount);
            textView.setText(countedStep+" Steps");
        }
    };

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "bolasepak";
            String description = "Aplikasi informasi sepak bola";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ch_bolasepak", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void notification(String title, String desc){
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ch_bolasepak")
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(title)
                .setContentText(desc)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ==================================================
        //NOTIFICATION
        notification("Bolasepak Notification","This notification is from bolasepak");
        // ==================================================


        // ==================================================
        //DATABASE
        Log.d("db init","initialize the database");
        dbHelper = new DatabaseHelper(this); //inisialisasi database
        db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * from teams",null);
        Log.d("TEST SCHEMA", Arrays.toString(c.getColumnNames()));
        //Syntax ngakses : https://developer.android.com/reference/android/database/Cursor
        //Buat insert, dll pake `db.execSQL(String sql)
        //full class docs : https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase
        // ==================================================



        // ==================================================
        //STEP COUNTER
        Log.d("StepCounterService","Starting Step Counter Service");
        Intent serviceIntent = new Intent(this, StepCounterService.class);
        startService(serviceIntent);
        registerReceiver(broadcastReceiver, new IntentFilter(StepCounterService.BROADCAST_ACTION));
        Log.d("StepCounterService run:",Boolean.toString(isMyServiceRunning(StepCounterService.class)));
        // ==================================================



        // set on click listener for fab
        FloatingActionButton fab = findViewById(R.id.FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(view.getContext(), SearchTeamActivity.class);
                startActivity(intent);
            }
        });

        // update date
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy");
        String strDate = sdf.format(date);
        TextView dateTime = (TextView) findViewById(R.id.DateTime);
        dateTime.setText(strDate);

        // set onClickListener for list selector buttons
        Button myTeamBtn = (Button) findViewById(R.id.PMSelector);
        myTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // change button UI
                view.setBackgroundResource(R.drawable.rounded_active_left_selector);
                ((Button) view).setTextColor(getResources().getColor(R.color.fontFocused));

                Button allTeamBtn = (Button) findViewById(R.id.UPSelector);
                allTeamBtn.setBackgroundResource(R.drawable.rounded_inactive_right_selector);
                allTeamBtn.setTextColor(getResources().getColor(R.color.fontUnfocused));

                // tembak API
            }
        });

        Button allTeamBtn = (Button) findViewById(R.id.UPSelector);
        /*allTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // change button UI
                view.setBackgroundResource(R.drawable.rounded_active_right_selector);
                ((Button) view).setTextColor(getResources().getColor(R.color.fontFocused));

                Button myTeamBtn = (Button) findViewById(R.id.PMSelector);
                myTeamBtn.setBackgroundResource(R.drawable.rounded_inactive_left_selector);
                myTeamBtn.setTextColor(getResources().getColor(R.color.fontUnfocused));

                //tembak API
                final ArrayList<Team> allTeam = new ArrayList<Team>();
                String URL = "http://134.209.97.218:5050/api/v1/json/1/search_all_teams.php?l=English%20Premier%20League";
                RequestQueue reqTeam = Volley.newRequestQueue(getBaseContext());
                reqTeam.start();
                JsonObjectRequest objReq = new JsonObjectRequest(
                    Request.Method.GET,
                    URL,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArrTeam = response.getJSONArray("teams");
                                for (int i=0;i<jsonArrTeam.length();i++) {
                                    JSONObject jsonObjTeam = jsonArrTeam.getJSONObject(i);
                                    String jsonName = jsonObjTeam.getString("strTeam");
                                    String jsonId = jsonObjTeam.getString("idTeam");
                                    String jsonBadge = jsonObjTeam.getString("strTeamBadge");
                                    Team temp = new Team();
                                    temp.setName(jsonName);
                                    temp.setId(jsonId);
                                    temp.setBadgeURL(jsonBadge);
                                    allTeam.add(temp);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("REST Response",error.toString());
                        }
                    }
                );
                reqTeam.add(objReq);
            }
        });*/

        // update step count


        // prepare RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.MatchContainer);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // tembak API sportsdb
        final List<Match> matchList = new ArrayList<>();
        String URLNext15 = "http://134.209.97.218:5050/api/v1/json/1/eventsnextleague.php?id=4328";
        String URLPast15 = "http://134.209.97.218:5050/api/v1/json/1/eventspastleague.php?id=4328";
        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        queue.start();

        JsonObjectRequest reqNext = new JsonObjectRequest(
                Request.Method.GET,
                URLNext15,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("events");
                            for(int i=0;i<arr.length();i++) {
                                JSONObject temp = arr.getJSONObject(i);
                                if(!temp.isNull("strAwayTeam")&&!temp.isNull("strHomeTeam")&&!temp.isNull("dateEvent")&&!temp.isNull("strTime")&&!temp.isNull("idEvent")) {
                                    Match myMatch1 = new Match();
                                    myMatch1.setAwayBadgeURL("http://134.209.97.218:5050/images/media/team/badge/7qhg311579111301.png");
                                    myMatch1.setAwayTeam(temp.getString("strAwayTeam"));
                                    myMatch1.setPast(false);
                                    myMatch1.setCancelled(false);
                                    myMatch1.setHomeBadgeURL("http://134.209.97.218:5050/images/media/team/badge/a1af2i1557005128.png");
                                    myMatch1.setHomeTeam(temp.getString("strHomeTeam"));
                                    myMatch1.setDate(temp.getString("dateEvent"));
                                    myMatch1.setTime(temp.getString("strTime").substring(0,5));
                                    myMatch1.setId(temp.getString("idEvent"));
                                    myMatch1.setCity("Jakarta");
                                    myMatch1.setLocation("Gelora Bung Karno");
                                    matchList.add(myMatch1);
                                }
                            }
                            mAdapter = new MatchRecyclerAdapter(matchList);
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REST Response",error.toString());
                    }
                }
        );
        JsonObjectRequest reqPast = new JsonObjectRequest(
                Request.Method.GET,
                URLPast15,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("events");
                            for(int i=0;i<arr.length();i++){
                                JSONObject temp = arr.getJSONObject(i);
                                if(!temp.isNull("intAwayScore")&&!temp.isNull("intHomeScore")){
                                    Match myMatch = new Match();
                                    myMatch.setAwayBadgeURL("http://134.209.97.218:5050/images/media/team/badge/7qhg311579111301.png");
                                    myMatch.setAwayScore(temp.getString("intAwayScore"));
                                    myMatch.setAwayTeam(temp.getString("strAwayTeam"));
                                    myMatch.setPast(true);
                                    myMatch.setHomeBadgeURL("http://134.209.97.218:5050/images/media/team/badge/a1af2i1557005128.png");
                                    myMatch.setHomeScore(temp.getString("intHomeScore"));
                                    myMatch.setHomeTeam(temp.getString("strHomeTeam"));
                                    myMatch.setDate(temp.getString("dateEvent"));
                                    myMatch.setId(temp.getString("idEvent"));
                                    myMatch.setCity("Jakarta");
                                    myMatch.setLocation("Gelora Bung Karno");
                                    matchList.add(myMatch);
                                }
                            }
                            mAdapter = new MatchRecyclerAdapter(matchList);
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REST Response",error.toString());
                    }
                }
        );
        queue.add(reqNext);
        queue.add(reqPast);
        
        mAdapter = new MatchRecyclerAdapter(matchList);
        mRecyclerView.setAdapter(mAdapter);
    }
}
