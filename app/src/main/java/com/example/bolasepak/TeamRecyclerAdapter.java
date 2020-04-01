package com.example.bolasepak;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TeamRecyclerAdapter extends RecyclerView.Adapter<TeamRecyclerAdapter.ViewHolder> {
    private List<Team> teamList;
    public Context context;

    public TeamRecyclerAdapter(List<Team> teamList) {
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public TeamRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_team_search_result, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull TeamRecyclerAdapter.ViewHolder holder, int position) {
        final Team team = teamList.get(position);
        holder.teamName.setText(team.getName());
        Glide.with(context).load(team.getBadgeURL()).into(holder.teamBadge);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TeamDetailsActivity.class);
                intent.putExtra("TEAM_NAME", team.getName());
                intent.putExtra("TEAM_BADGE_URL", team.getBadgeURL());
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.teamList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ImageView teamBadge;
        public TextView teamName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            teamBadge = itemView.findViewById(R.id.TeamBadge);
            teamName = itemView.findViewById(R.id.TeamName);
        }
    }
}
