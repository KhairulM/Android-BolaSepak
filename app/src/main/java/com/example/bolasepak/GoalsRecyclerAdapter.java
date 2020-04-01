package com.example.bolasepak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GoalsRecyclerAdapter extends RecyclerView.Adapter<GoalsRecyclerAdapter.ViewHolder> {
    public Context context;
    public Boolean isHomeGoal;
    private List<String> goalList;

    public GoalsRecyclerAdapter(List<String> goalList, Boolean isHomeGoal) {
        this.goalList = goalList;
        this.isHomeGoal = isHomeGoal;
    }

    @NonNull
    @Override
    public GoalsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewHolder viewHolder;
        View view;

        if (this.isHomeGoal){
            view = LayoutInflater.from(context).inflate(R.layout.layout_home_goal, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.layout_away_goal, parent, false);
        }

        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GoalsRecyclerAdapter.ViewHolder holder, int position) {
        holder.goalText.setText(goalList.get(position));
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView goalText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            goalText = (TextView) itemView.findViewById(R.id.GoalText);
        }
    }
}
