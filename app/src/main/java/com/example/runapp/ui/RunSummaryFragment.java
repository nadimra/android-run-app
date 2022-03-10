package com.example.runapp.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runapp.R;
import com.example.runapp.db.entity.Photo;
import com.example.runapp.db.entity.Run;
import com.example.runapp.db.entity.RunPhoto;
import com.example.runapp.other.TrackingUtility;
import com.example.runapp.ui.adapters.PhotoAdapter;
import com.example.runapp.viewmodels.TrackingActivityViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Summary fragment to be displayed once a user completes a run
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class RunSummaryFragment extends Fragment implements View.OnClickListener {
    private TrackingActivityViewModel viewModel;
    private PhotoAdapter adapter;
    private Bitmap img;
    private PieChart pieChart;
    private int ADD_IMAGE_CODE = 105;

    public RunSummaryFragment() {
        // Required empty public constructor
    }

    public static RunSummaryFragment newInstance(String param1, String param2) {
        RunSummaryFragment fragment = new RunSummaryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Assign viewmodel
        viewModel = new ViewModelProvider(requireActivity()).get(TrackingActivityViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_run_summary, container, false);

        if(this.getArguments()!=null){
            // Get run information
            Bundle bundle = this.getArguments();
            Long timestamp = bundle.getLong("timestamp");
            Long timeInMillis = bundle.getLong("timeInMillis");
            Long stillTimeInMillis = bundle.getLong("stillTimeInMillis");
            Float avgSpeedInKMH = bundle.getFloat("avgSpeedInKMH");
            int caloriesBurned = bundle.getInt("caloriesBurned");
            int distanceInMeters = bundle.getInt("distanceInMeters");

            Timestamp stamp = new Timestamp(timestamp);
            Date date = new Date(stamp.getTime());
            Time time = new Time(stamp.getTime());

            viewModel.setTimestamp(timestamp);
            viewModel.setTimeInMillis(timeInMillis);
            viewModel.setStillTimeInMillis(stillTimeInMillis);
            viewModel.setAvgSpeedInKMH(avgSpeedInKMH);
            viewModel.setCaloriesBurned(caloriesBurned);
            viewModel.setDistanceInMeters(distanceInMeters);
            viewModel.setDate(date);
            viewModel.setTime(time);
        }

        // Assign values to UI elements
        TextView datePlaceholder = view.findViewById(R.id.itemDatePlaceholder);
        TextView clockPlaceholder = view.findViewById(R.id.itemClockPlaceholder);
        TextView durationPlaceholder = view.findViewById(R.id.itemTimePlaceholder);
        TextView speedPlaceholder = view.findViewById(R.id.itemSpeedPlaceholder);
        TextView caloriesPlaceholder = view.findViewById(R.id.itemCaloriesPlaceholder);
        TextView distancePlaceholder = view.findViewById(R.id.itemDistancePlaceholder);
        ImageView imgPlaceholder = view.findViewById(R.id.imgPlaceholder);
        clockPlaceholder.setText(viewModel.getTime().toString());
        datePlaceholder.setText(viewModel.getDate().toString());
        durationPlaceholder.setText(TrackingUtility.millisFormatted(viewModel.getTimeInMillis(),false));
        speedPlaceholder.setText(viewModel.getAvgSpeedInKMH()+"");
        caloriesPlaceholder.setText(viewModel.getCaloriesBurned()+"");
        distancePlaceholder.setText(viewModel.getDistanceInMeters()+"");

        // Assigns the photos for the run
        GridView gridView = view.findViewById(R.id.gridView);
        adapter = new PhotoAdapter(getActivity().getApplicationContext(),viewModel.getPhotoList());
        gridView.setAdapter(adapter);

        // Displays map image of run
        if(viewModel.getByteArray()!=null){
            byte[] bytes = viewModel.getByteArray();
            img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgPlaceholder.setImageBitmap(img);
        }

        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        Button deleteButton = (Button) view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
        Button addImageButton = (Button) view.findViewById(R.id.addImage);
        addImageButton.setOnClickListener(this);
        pieChart = view.findViewById(R.id.pieChartStillTime);

        setupPieChartTimeStill();
        return view;
    }

    /**
     * Initialise pie chart that represents the proportion of time that the user is idle
     */
    private void setupPieChartTimeStill() {
        // Create pie chart data
        ArrayList<PieEntry> options = new ArrayList<>();
        options.add(new PieEntry(viewModel.getStillTimeInMillis(),"% Time spent idle"));
        long timeSpentMoving = viewModel.getTimeInMillis()-viewModel.getStillTimeInMillis();
        if(timeSpentMoving<0){
            timeSpentMoving=0;
        }
        options.add(new PieEntry(timeSpentMoving,"% Time spent moving"));
        PieDataSet pieDataSet = new PieDataSet(options,"");

        // Design the pie chart
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setDrawValues(false);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Time spent moving/idle");
        pieChart.animate();
    }

    /**
     * Handles onclick items
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                // Gather all user input
                EditText description = getView().findViewById(R.id.descriptionText);
                String descriptionText = description.getText().toString();
                RatingBar ratingBar = getView().findViewById(R.id.ratingBar);
                Float rating = ratingBar.getRating();
                Spinner mySpinner = getView().findViewById(R.id.weatherSpinner);
                String weather = mySpinner.getSelectedItem().toString();

                // Get map bitmap image
                if(viewModel.getByteArray()!=null){
                    byte[] bytes = viewModel.getByteArray();
                    img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                }

                // Create Run Object
                Run run = new Run(viewModel.getTimestamp(),viewModel.getDistanceInMeters(),img,
                        viewModel.getAvgSpeedInKMH(), viewModel.getTimeInMillis(), viewModel.getCaloriesBurned(),descriptionText,rating,weather);

                // Insert the run to database
                long runId = viewModel.insertRun(run);

                // Insert photos uploaded for specific run into run photo table
                ArrayList<Uri> listOfPhotos = viewModel.getPhotoList();
                for(Uri photo:listOfPhotos){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photo);
                        Photo pic = new Photo(bitmap);
                        long photoId = viewModel.insertPhoto(pic);
                        viewModel.insertRunPhoto(new RunPhoto((int)runId,(int)photoId));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(getActivity().getApplicationContext(),"Run Saved",Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                break;
            case R.id.deleteButton:
                // Deletes the run
                Toast.makeText(getActivity().getApplicationContext(),"Run Deleted",Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                break;
            case R.id.addImage:
                // Allow user to load images
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, ADD_IMAGE_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ADD_IMAGE_CODE) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    // code for multiple image selection
                    viewModel.addNewPhoto(imageUri);
                }
                adapter.notifyDataSetChanged();
            } else {
                Uri uri = data.getData();
                // single image selection
                viewModel.addNewPhoto(uri);
                adapter.notifyDataSetChanged();
            }
        }
    }


}