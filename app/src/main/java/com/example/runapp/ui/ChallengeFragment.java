package com.example.runapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.runapp.R;
import com.example.runapp.other.ChallengeHelper;
import com.example.runapp.other.NetworkChangeReceiver;
import com.example.runapp.other.TrackingUtility;
import com.example.runapp.services.TrackingService;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Fragment which loads all challenges posted by other users
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class ChallengeFragment extends Fragment {
    private FirebaseRecyclerOptions<ChallengeHelper> options;
    private FirebaseRecyclerAdapter<ChallengeHelper,MyChallengeViewHolder> adapter;
    private RecyclerView recyclerView;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private NetworkChangeReceiver networkReceiver = new NetworkChangeReceiver();

    public ChallengeFragment() {
        // Required empty public constructor
    }

    public static ChallengeFragment newInstance(String param1, String param2) {
        ChallengeFragment fragment = new ChallengeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_challenge, container, false);

        // Loads the database and saves all the challenges posted
        rootNode = FirebaseDatabase.getInstance("https://runapp-d9c02-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = rootNode.getReference("Challenges");

        // Loads last ten challenges in the database
        RecyclerView recyclerView= v.findViewById(R.id.challengeRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        options = new FirebaseRecyclerOptions.Builder<ChallengeHelper>().setQuery(reference.limitToLast(10),ChallengeHelper.class).build();
        adapter = new FirebaseRecyclerAdapter<ChallengeHelper, MyChallengeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyChallengeViewHolder holder, int position, @NonNull ChallengeHelper model) {
                // Creates a challenge and updates the text on the view
                Timestamp stamp = new Timestamp(System.currentTimeMillis());
                Date date = new Date(stamp.getTime());
                String challengeText = model.getUsername() + " has just ran "+ model.getDistance() + "m in "+
                        TrackingUtility.millisFormatted(model.getTimeInMillis(),false)+"! (Posted: "+date+")";
                holder.postText.setText(challengeText);
            }

            @NonNull
            @Override
            public MyChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.challenge_item,parent,false);
                return new MyChallengeViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

        // Listens network changes
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkReceiver, filter);

        return v;
    }

    /**
     * Unregisters to the network receiver when the fragment is destroyed
     */
    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(networkReceiver);
        super.onDestroy();
    }

}