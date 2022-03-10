package com.example.runapp.ui.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.runapp.R;
import com.example.runapp.db.entity.Run;
import com.example.runapp.other.TrackingUtility;
import com.example.runapp.ui.RunDetailsActivity;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Run adapter to display run data in the run fragment
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class RunAdaptor extends RecyclerView.Adapter<RunAdaptor.RunViewHolder> {

    private List<Run> data;
    private Activity hostActivity;
    private Context context;
    private LayoutInflater layoutInflater;

    public RunAdaptor(Activity activity) {
        this.data = new ArrayList<>();
        this.hostActivity = activity;
        this.context = activity.getApplicationContext();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RunViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.run_item, parent, false);
        return new RunViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RunViewHolder holder, int position) {
        holder.bind(data.get(position));

        // Loads specific run details into a new activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Run run = data.get(position);
                Intent intent = new Intent(hostActivity, RunDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.putExtra("run_id", run.get_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Add new data to the dataset
     * @param newData
     */
    public void setData(List<Run> newData) {
        if (data != null) {
            data.clear();
            data.addAll(newData);
            notifyDataSetChanged();
        } else {
            data = newData;
        }
    }

    /**
     * Represents the view holder for the recycler view
     */
    class RunViewHolder extends RecyclerView.ViewHolder {

        TextView dateView;
        TextView clockView;
        TextView timeView;
        TextView speedView;
        TextView distanceView;
        TextView caloriesView;

        RunViewHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.itemDatePlaceholder);
            clockView = itemView.findViewById(R.id.itemClockPlaceholder);
            timeView = itemView.findViewById(R.id.itemTimePlaceholder);
            speedView = itemView.findViewById(R.id.itemSpeedPlaceholder);
            distanceView = itemView.findViewById(R.id.itemDistancePlaceholder);
            caloriesView = itemView.findViewById(R.id.itemCaloriesPlaceholder);
        }

        /**
         * Binds the variables to the correct ui elements
         * @param run
         */
        void bind(final Run run) {
            if (run != null) {
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

            }
        }

    }
}
