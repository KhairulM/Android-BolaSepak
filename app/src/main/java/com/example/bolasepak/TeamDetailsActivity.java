package com.example.bolasepak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class TeamDetailsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String teamName, teamBadgeURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        // set views
        Intent intent = getIntent();
        teamName = intent.getStringExtra("TEAM_NAME");
        teamBadgeURL = intent.getStringExtra("TEAM_BADGE_URL");

        ((TextView) findViewById(R.id.TeamName)).setText(teamName);
        Glide.with(this).load(teamBadgeURL).into((ImageView) findViewById(R.id.TeamBadge));

        // set list selector onClick
        final Button pmButton = (Button) findViewById(R.id.PMSelector);
        final Button upButton = (Button) findViewById(R.id.UPSelector);

        pmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(R.drawable.rounded_active_left_selector);
                ((Button) v).setTextColor(getResources().getColor(R.color.fontFocused));

                upButton.setBackgroundResource(R.drawable.rounded_inactive_right_selector);
                upButton.setTextColor(getResources().getColor(R.color.fontUnfocused));
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(R.drawable.rounded_active_right_selector);
                ((Button) v).setTextColor(getResources().getColor(R.color.fontFocused));

                pmButton.setBackgroundResource(R.drawable.rounded_inactive_left_selector);
                pmButton.setTextColor(getResources().getColor(R.color.fontUnfocused));
            }
        });

        // set recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.MatchContainer);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<Match> matchList = new ArrayList<>();

        mAdapter = new MatchRecyclerAdapter(matchList);

    }
}
