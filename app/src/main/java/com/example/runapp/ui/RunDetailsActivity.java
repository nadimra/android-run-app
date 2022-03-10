package com.example.runapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runapp.R;
import com.example.runapp.db.entity.Run;
import com.example.runapp.other.ChallengeHelper;
import com.example.runapp.other.Constants;
import com.example.runapp.other.TrackingUtility;
import com.example.runapp.ui.adapters.RunPhotosAdapter;
import com.example.runapp.viewmodels.RunDetailsActivityViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

/**
 * Class that displays the details of a run when the user clicks the option
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class RunDetailsActivity extends AppCompatActivity {
    private RunDetailsActivityViewModel viewModel;
    private TextView dateView;
    private TextView clockView;
    private TextView timeView;
    private TextView speedView;
    private TextView distanceView;
    private TextView caloriesView;
    private TextView descriptionView;
    private TextView ratingView;
    private TextView weatherView;
    private ImageView mapView;
    private Button challengePostBtn;
    private Button deleteRunBtn;
    private Button shareBtn;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_details);

        // Assign viewmodel
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(RunDetailsActivityViewModel.class);

        // Get online database reference
        rootNode = FirebaseDatabase.getInstance("https://runapp-d9c02-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = rootNode.getReference("Challenges");

        // Assign run
        int runId = 0;
        if(getIntent().hasExtra("run_id")){
            runId = getIntent().getExtras().getInt("run_id");
            if(viewModel.getCurrentRunId() != runId){
                viewModel.setCurrentRunId(runId);
                viewModel.setRun(runId);
            }
        }
        Run run = viewModel.getRun();
        Log.d("g53mdp",run.get_id()+"");

        // Assign UI elements
        dateView = findViewById(R.id.itemDatePlaceholder);
        clockView = findViewById(R.id.itemClockPlaceholder);
        timeView = findViewById(R.id.itemTimePlaceholder);
        speedView = findViewById(R.id.itemSpeedPlaceholder);
        distanceView = findViewById(R.id.itemDistancePlaceholder);
        caloriesView = findViewById(R.id.itemCaloriesPlaceholder);
        descriptionView = findViewById(R.id.itemDescriptionPlaceholder);
        ratingView = findViewById(R.id.itemRatingPlaceholder);
        weatherView = findViewById(R.id.itemWeatherPlaceholder);
        mapView = findViewById(R.id.itemPathPlaceholder);
        challengePostBtn = findViewById(R.id.postChallenge);
        deleteRunBtn = findViewById(R.id.deleteRun);
        shareBtn = findViewById(R.id.shareButton);

        // Set UI elements
        Timestamp stamp = new Timestamp(run.getTimestamp());
        Date date = new Date(stamp.getTime());
        Time time = new Time(stamp.getTime());
        String dataString = date.toString();
        String timeString = time.toString();
        String durationString = TrackingUtility.millisFormatted(run.getTimeInMillis(),false);
        dateView.setText(dataString);
        clockView.setText(timeString);
        timeView.setText(durationString);
        speedView.setText(run.getAvgSpeedInKMH()+"KMH");
        distanceView.setText(run.getDistanceInMetres()+"m");
        caloriesView.setText(run.getCaloriesBurned()+"");
        descriptionView.setText(run.getDescription()+"");
        ratingView.setText(run.getRating()+"");
        weatherView.setText(run.getWeather()+"");
        mapView.setImageBitmap(run.getImg());

        // Get the run photos from a query
        try {
            viewModel.getRunPhotos(run.get_id());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Set the photos for the run
        RecyclerView recyclerView = findViewById(R.id.photoListRecycler);
        final RunPhotosAdapter adapter = new RunPhotosAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        try {
            viewModel.getRunPhotos(run.get_id()).observe(this, images ->
                    adapter.setData(images)
            );
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Add a new challenge to the online database
        challengePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                String username = sharedPreferences.getString(Constants.KEY_NAME,"");
                ChallengeHelper post = new ChallengeHelper(username, run.getDistanceInMetres(),run.getTimeInMillis());
                reference.push().setValue(post);
                Toast.makeText(getApplicationContext(),"Posted Challenge",Toast.LENGTH_SHORT).show();
            }
        });

        // Delete a run from the database
        deleteRunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteRun(run);
                finish();
                Toast.makeText(getApplicationContext(),"Run Deleted",Toast.LENGTH_SHORT).show();
            }
        });

        // Share run information to social media
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareSub = "RunApp: Today's Run ("+ LocalDateTime.now()+")";
                String shareBody = "I ran "+run.getDistanceInMetres()+"m in "+run.getAvgSpeedInKMH()+"!";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(myIntent,"Share using"));

            }
        });
    }
}