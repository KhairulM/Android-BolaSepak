package com.example.bolasepak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PastMatchDetailsActivity extends AppCompatActivity {
    private RecyclerView homeRecyclerView, awayRecyclerView;
    private RecyclerView.Adapter homeAdapter, awayAdapter;
    private RecyclerView.LayoutManager homeLayoutManager, awayLayoutManager;
    private String matchDate,
            matchHighlighted,
            homeTeamName,
            awayTeamName,
            homeBadgeURL,
            awayBadgeURL,
            matchId,
            matchCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_match_details);

        // set header details from intent extras
        Intent intent = getIntent();
        matchDate = intent.getStringExtra("MATCH_DATE");
        matchCity = intent.getStringExtra("MATCH_CITY");
        matchHighlighted = intent.getStringExtra("MATCH_HIGHLIGHTED");
        homeTeamName = intent.getStringExtra("MATCH_HOME_TEAM_NAME");
        awayTeamName = intent.getStringExtra("MATCH_AWAY_TEAM_NAME");
        homeBadgeURL = intent.getStringExtra("MATCH_HOME_BADGE_URL");
        awayBadgeURL = intent.getStringExtra("MATCH_AWAY_BADGE_URL");
        matchId = intent.getStringExtra("MATCH_ID");

        ((TextView) findViewById(R.id.MatchDate)).setText(String.format("%s, %s", matchCity, matchDate));
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

        // set recycler view
        homeRecyclerView = (RecyclerView) findViewById(R.id.HomeGoalsContainer);
        awayRecyclerView = (RecyclerView) findViewById(R.id.AwayGoalsContainer);

        homeRecyclerView.setHasFixedSize(true);
        awayRecyclerView.setHasFixedSize(true);

        homeLayoutManager = new LinearLayoutManager(this);
        awayLayoutManager = new LinearLayoutManager(this);

        homeRecyclerView.setLayoutManager(homeLayoutManager);
        awayRecyclerView.setLayoutManager(awayLayoutManager);

        List<String> homeGoalList = new ArrayList<>();
        List<String> awayGoalList = new ArrayList<>();

        homeGoalList.add("test");
        homeGoalList.add("test1");
        awayGoalList.add("test2");
        awayGoalList.add("test3");


        homeAdapter = new GoalsRecyclerAdapter(homeGoalList, true);
        awayAdapter = new GoalsRecyclerAdapter(awayGoalList, false);

        homeRecyclerView.setAdapter(homeAdapter);
        awayRecyclerView.setAdapter(awayAdapter);



    }
}
