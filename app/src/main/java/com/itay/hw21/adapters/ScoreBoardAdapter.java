package com.itay.hw21.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itay.hw21.R;
import com.itay.hw21.Utils;
import com.itay.hw21.models.OnItemClickListener;
import com.itay.hw21.models.Score;

import java.util.List;

public class ScoreBoardAdapter extends RecyclerView.Adapter<ScoreBoardAdapter.ViewHolder> {

    /**
     *
     * @param score the score list for the RecyclerView
     * @param listener on item click listener
     */
    public ScoreBoardAdapter(List<Score> score, OnItemClickListener listener) {
        this.scores = score;
        this.listener = listener;
    }

    private List<Score> scores;
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the row layout for the recycler-view
        View score_item = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item,parent,false);
       return new ViewHolder(score_item);
    }


    // gets called for each row
    // updates the UI according to the row position
    // with the corresponding score from the score list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Score cur = scores.get(position);
        holder.placeTv.setText("" + (position+1));
        holder.scoreTv.setText(Utils.getTimeString(cur.getTimeLasted()));
        holder.itemView.setOnClickListener(v -> {
            listener.Clicked(cur);
        });
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    // ViewHolder design pattern
    // connects the controls from the xmls
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeTv,scoreTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placeTv = itemView.findViewById(R.id.score_item_place);
            scoreTv = itemView.findViewById(R.id.score_item_score);
        }
    }
}
