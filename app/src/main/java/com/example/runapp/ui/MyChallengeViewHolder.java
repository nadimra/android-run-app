package com.example.runapp.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runapp.R;

/**
 * Binds the challenge item to the UI
 */
public class MyChallengeViewHolder extends RecyclerView.ViewHolder {
    TextView postText;
    public MyChallengeViewHolder(@NonNull View itemView) {
        super(itemView);
        postText = itemView.findViewById(R.id.challengeText);
    }
}
