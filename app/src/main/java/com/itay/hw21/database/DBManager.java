package com.itay.hw21.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itay.hw21.models.OnScoresResponse;
import com.itay.hw21.models.Score;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    // root reference for the database
    private final DatabaseReference root;
    // singleton
    private static final DBManager shared = new DBManager();

    public static DBManager getInstance() {
        return shared;
    }

    // private constructor
    private DBManager() {
        root = FirebaseDatabase.getInstance("https://car-game-eb178-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    }


    /**
     * addNewScore
     * Adds a new score to the firebase database 'scores'
     * @param score score to be added
     * @param onSuccessListener listener for success
     * @param onFailureListener listener for failure
     */
    public void addNewScore(Score score, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        root.child("scores").push().setValue(score)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * fetchScores
     * retrieves all scores from the database 'scores'
     * @param listener scores response
     */
    public void fetchScores(OnScoresResponse listener) {
        root.child("scores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Score> scores = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child == null)
                        continue;
                    Score score = child.getValue(Score.class);
                    scores.add(score);
                }
                listener.consumeScores(scores);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.print(error.getDetails());
            }
        });
    }


}
