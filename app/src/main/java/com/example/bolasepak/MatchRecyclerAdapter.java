package com.example.bolasepak;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MatchRecyclerAdapter extends RecyclerView.Adapter<MatchRecyclerAdapter.ViewHolder> {
    private static final Integer MATCH_UPCOMING = 1;
    private static final Integer MATCH_PAST = 2;
    private List<Match> matchList;
    public Context context;


    public MatchRecyclerAdapter(List<Match> matchList){
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewHolder viewHolder;

        if (viewType == MATCH_PAST){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_past_match, parent, false);
            viewHolder =  new ViewHolder(view, MATCH_PAST);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_upcoming_match, parent, false);
            viewHolder = new ViewHolder(view, MATCH_UPCOMING);
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MatchRecyclerAdapter.ViewHolder holder, int position) {
        holder.bindMatch(this.matchList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.matchList.size();
    }

    @Override
    public int getItemViewType(int position){
        if (matchList.get(position).isPast()){
            return MATCH_PAST;
        }else {
            return MATCH_UPCOMING;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView matchDate, matchHighlighted, homeTeamName, awayTeamName;
        public ImageView homeBadge, awayBadge;
        public Button matchDetailsBtn;

        public ViewHolder(@NonNull final View itemView, int type) {
            super(itemView);

            matchDate = itemView.findViewById(R.id.MatchDate);
            homeTeamName = itemView.findViewById(R.id.HomeTeamName);
            awayTeamName = itemView.findViewById(R.id.AwayTeamName);
            homeBadge = (ImageView) itemView.findViewById(R.id.HomeBadge);
            awayBadge = (ImageView) itemView.findViewById(R.id.AwayBadge);
            matchDetailsBtn = (Button) itemView.findViewById(R.id.MatchDetailsBtn);

            if (type == MATCH_UPCOMING) {
                matchHighlighted = itemView.findViewById(R.id.MatchHighlighted);
            }
            else if (type == MATCH_PAST) {
                matchHighlighted = itemView.findViewById(R.id.MatchHighlighted);
            }
        }


        public void bindMatch(final Match match) {
            matchDate.setText(match.getDate());
            homeTeamName.setText(match.getHomeTeam());
            awayTeamName.setText(match.getAwayTeam());
            // load images with Glide
            Glide.with(context).load(match.getHomeBadgeURL()).into(homeBadge);
            Glide.with(context).load(match.getAwayBadgeURL()).into(awayBadge);

            if (match.isPast()) {
                matchHighlighted.setText(String.format("%s : %s", match.getHomeScore(), match.getAwayScore()));
            } else if (match.isCancelled()) {
                matchHighlighted.setText("Cancelled");
                matchHighlighted.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                matchDetailsBtn.setVisibility(View.GONE);
            } else {
                matchHighlighted.setText(match.getTime());
            }

            matchDetailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (match.isPast()){
                        intent = new Intent(v.getContext(), PastMatchDetailsActivity.class);
                    } else {
                        intent = new Intent(v.getContext(), UpcomingMatchDetailsActivity.class);
                    }

                    intent.putExtra("MATCH_DATE", matchDate.getText().toString());
                    intent.putExtra("MATCH_HIGHLIGHTED", matchHighlighted.getText().toString());
                    intent.putExtra("MATCH_HOME_TEAM_NAME", homeTeamName.getText().toString());
                    intent.putExtra("MATCH_AWAY_TEAM_NAME", awayTeamName.getText().toString());
                    intent.putExtra("MATCH_HOME_BADGE_URL", match.getHomeBadgeURL());
                    intent.putExtra("MATCH_AWAY_BADGE_URL", match.getAwayBadgeURL());
                    intent.putExtra("MATCH_CITY", match.getCity());
                    intent.putExtra("MATCH_ID", match.getId());
                    intent.putExtra("MATCH_LOCATION", match.getLocation());

                    v.getContext().startActivity(intent);
                }
            });

            // set onClickListener for Home and Away team badge
            homeBadge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), TeamDetailsActivity.class);

                    intent.putExtra("TEAM_NAME", homeTeamName.getText().toString());
                    intent.putExtra("TEAM_BADGE_URL", match.getHomeBadgeURL());

                    v.getContext().startActivity(intent);
                }
            });

            awayBadge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), TeamDetailsActivity.class);

                    intent.putExtra("TEAM_NAME", awayTeamName.getText().toString());
                    intent.putExtra("TEAM_BADGE_URL", match.getAwayBadgeURL());

                    v.getContext().startActivity(intent);;
                }
            });
        }
    }
}
