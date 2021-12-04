package com.itay.hw21;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScoreBoardAdapter extends RecyclerView.Adapter<ScoreBoardAdapter.ViewHolder> {

    public ScoreBoardAdapter(List<Score> score) {
        this.scores = score;
    }

    private List<Score> scores;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View score_item = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item,parent,false);
       return new ViewHolder(score_item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Score cur = scores.get(position);
        holder.placeTv.setText("" + position);
        holder.scoreTv.setText(String.valueOf(cur.getScore()));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
         TextView placeTv,scoreTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placeTv = itemView.findViewById(R.id.score_item_place);
            scoreTv = itemView.findViewById(R.id.score_item_score);
        }
    }
}
