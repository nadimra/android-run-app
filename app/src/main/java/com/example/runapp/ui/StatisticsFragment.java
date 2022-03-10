package com.example.runapp.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.runapp.R;
import com.example.runapp.db.entity.Run;
import com.example.runapp.other.DayTuple;
import com.example.runapp.other.TrackingUtility;
import com.example.runapp.viewmodels.StatisticsFragmentViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Display useful statistics
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class StatisticsFragment extends Fragment {

    private StatisticsFragmentViewModel viewModel;

    private TextView dailyDistanceView;
    private TextView dailyTotalTimeView;
    private TextView dailyCaloriesView;
    private TextView dailyAvgSpeedView;

    private TextView weeklyDistanceView;
    private TextView weeklyTotalTimeView;
    private TextView weeklyCaloriesView;
    private TextView weeklyAvgSpeedView;

    private TextView allTimeDistanceView;
    private TextView allTimeTotalTimeView;
    private TextView allTimeCaloriesView;
    private TextView allTimeAvgSpeedView;

    private TextView lineChartSpeedPerRunTitle;
    private TextView lineChartDistancePerRunTitle;
    private TextView barChartDistanceDayTitle;

    private LineChart lineChartSpeedPerRun;
    private LineChart lineChartDistancePerRun;
    private BarChart barChartDistancePerDay;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);
        // Assign viewmodel
        //viewModel = new ViewModelProvider(this,
        //        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(StatisticsFragmentViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(StatisticsFragmentViewModel.class);

        // Daily statistics UI
        dailyTotalTimeView = v.findViewById(R.id.dailyTime);
        dailyDistanceView = v.findViewById(R.id.dailyDistance);
        dailyCaloriesView = v.findViewById(R.id.dailyCalories);
        dailyAvgSpeedView = v.findViewById(R.id.dailySpeed);

        // Weekly statistics UI
        weeklyTotalTimeView = v.findViewById(R.id.weeklyTime);
        weeklyDistanceView = v.findViewById(R.id.weeklyDistance);
        weeklyCaloriesView = v.findViewById(R.id.weeklyCalories);
        weeklyAvgSpeedView = v.findViewById(R.id.weeklySpeed);

        // All time statistics UI
        allTimeTotalTimeView = v.findViewById(R.id.allTimeTime);
        allTimeDistanceView = v.findViewById(R.id.allDistance);
        allTimeCaloriesView = v.findViewById(R.id.allCalories);
        allTimeAvgSpeedView = v.findViewById(R.id.allSpeed);

        // Charts UI
        lineChartSpeedPerRun = (LineChart) v.findViewById(R.id.lineChartSpeedPerRun);
        lineChartDistancePerRun = (LineChart) v.findViewById(R.id.lineChartDistancePerRun);
        barChartDistancePerDay = (BarChart) v.findViewById(R.id.barChartDistanceDay);
        barChartDistanceDayTitle = (TextView) v.findViewById(R.id.barChartDistanceDayTitle);
        lineChartDistancePerRunTitle = (TextView) v.findViewById(R.id.lineChartDistancePerRunTitle);
        lineChartSpeedPerRunTitle = (TextView) v.findViewById(R.id.lineChartSpeedPerRunTitle);

        // Initialise charts
        setupLineChartSpeedPerRun();
        setupLineChartDistancePerRun();
        setupBarChartDistancePerDay();

        subscribeToObservers();
        return v;
    }


    /**
     * Setup line chart design
     */
    private void setupLineChartSpeedPerRun(){
        lineChartSpeedPerRun.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChartSpeedPerRun.getXAxis().setDrawLabels(false);
        lineChartSpeedPerRun.getXAxis().setAxisLineColor(Color.BLACK);
        lineChartSpeedPerRun.getXAxis().setDrawGridLines(false);

        lineChartSpeedPerRun.getAxisLeft().setAxisLineColor(Color.BLACK);
        lineChartSpeedPerRun.getAxisLeft().setTextColor(Color.BLACK);
        lineChartSpeedPerRun.getAxisLeft().setDrawGridLines(false);

        lineChartSpeedPerRun.getAxisRight().setAxisLineColor(Color.BLACK);
        lineChartSpeedPerRun.getAxisRight().setTextColor(Color.BLACK);
        lineChartSpeedPerRun.getAxisRight().setDrawGridLines(false);

        lineChartSpeedPerRun.getDescription().setText("");
        lineChartSpeedPerRunTitle.setText("Avg Speed (in KMH) per Run sorted by date");

        lineChartSpeedPerRun.getDescription().setTextSize(18);
        lineChartSpeedPerRun.getLegend().setEnabled(false);
        lineChartSpeedPerRun.getAxisLeft().setAxisMinimum(0);
        lineChartSpeedPerRun.getAxisRight().setAxisMinimum(0);
    }

    /**
     * Setup line chart design
     */
    private void setupLineChartDistancePerRun(){
        lineChartDistancePerRun.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChartDistancePerRun.getXAxis().setDrawLabels(false);
        lineChartDistancePerRun.getXAxis().setAxisLineColor(Color.BLACK);
        lineChartDistancePerRun.getXAxis().setDrawGridLines(false);

        lineChartDistancePerRun.getAxisLeft().setAxisLineColor(Color.BLACK);
        lineChartDistancePerRun.getAxisLeft().setTextColor(Color.BLACK);
        lineChartDistancePerRun.getAxisLeft().setDrawGridLines(false);

        lineChartDistancePerRun.getAxisRight().setAxisLineColor(Color.BLACK);
        lineChartDistancePerRun.getAxisRight().setTextColor(Color.BLACK);
        lineChartDistancePerRun.getAxisRight().setDrawGridLines(false);

        lineChartDistancePerRun.getDescription().setText("");
        lineChartDistancePerRunTitle.setText("Distance (in m) per Run sorted by date");
        lineChartDistancePerRun.getDescription().setTextSize(18);
        lineChartDistancePerRun.getLegend().setEnabled(false);
        lineChartDistancePerRun.getAxisLeft().setAxisMinimum(0);
        lineChartDistancePerRun.getAxisRight().setAxisMinimum(0);
    }

    /**
     * Setup bar chart design
     */
    private void setupBarChartDistancePerDay(){
        barChartDistancePerDay.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChartDistancePerDay.getXAxis().setDrawLabels(true);
        barChartDistancePerDay.getXAxis().setAxisLineColor(Color.BLACK);
        barChartDistancePerDay.getXAxis().setDrawGridLines(false);

        barChartDistancePerDay.getAxisLeft().setAxisLineColor(Color.BLACK);
        barChartDistancePerDay.getAxisLeft().setTextColor(Color.BLACK);
        barChartDistancePerDay.getAxisLeft().setDrawGridLines(false);

        barChartDistancePerDay.getAxisRight().setAxisLineColor(Color.BLACK);
        barChartDistancePerDay.getAxisRight().setTextColor(Color.BLACK);
        barChartDistancePerDay.getAxisRight().setDrawGridLines(false);

        barChartDistancePerDay.getDescription().setText("");
        barChartDistanceDayTitle.setText("Distance (in m) per Run sorted by date");

        barChartDistancePerDay.getDescription().setTextSize(18);
        barChartDistancePerDay.getLegend().setEnabled(false);
        barChartDistancePerDay.getAxisLeft().setAxisMinimum(0);
        barChartDistancePerDay.getAxisRight().setAxisMinimum(0);

    }

    /**
     * Update charts based on database information
     */
    private void subscribeToObservers() {
        // Daily stats
        viewModel.getDailyTotalTime().observe(requireActivity(), time -> {
            dailyTotalTimeView.setText(TrackingUtility.millisFormatted(time,false));
        });
        viewModel.getDailyDistance().observe(requireActivity(), distance -> {
            dailyDistanceView.setText(distance+"m");
        });
        viewModel.getDailyCalories().observe(requireActivity(), calories -> {
            dailyCaloriesView.setText(calories+" calories");
        });
        viewModel.getDailyAvgSpeed().observe(requireActivity(), speed -> {
            dailyAvgSpeedView.setText(speed+"KMH");
        });

        // Weekly stats
        viewModel.getWeeklyTotalTime().observe(requireActivity(), time -> {
            weeklyTotalTimeView.setText(TrackingUtility.millisFormatted(time,false));
        });
        viewModel.getWeeklyDistance().observe(requireActivity(), distance -> {
            weeklyDistanceView.setText(distance+"m");
        });
        viewModel.getWeeklyCalories().observe(requireActivity(), calories -> {
            weeklyCaloriesView.setText(calories+" calories");
        });
        viewModel.getWeeklyAvgSpeed().observe(requireActivity(), speed -> {
            weeklyAvgSpeedView.setText(speed+"KMH");
        });

        // All time stats
        viewModel.getAllTimeTotalTime().observe(requireActivity(), time -> {
            allTimeTotalTimeView.setText(TrackingUtility.millisFormatted(time,false));
        });
        viewModel.getAllTimeDistance().observe(requireActivity(), distance -> {
            allTimeDistanceView.setText(distance+"m");
        });
        viewModel.getAllTimeCalories().observe(requireActivity(), calories -> {
            allTimeCaloriesView.setText(calories+" calories");
        });
        viewModel.getAllTimeAvgSpeed().observe(requireActivity(), speed -> {
            allTimeAvgSpeedView.setText(speed+"KMH");
        });


        viewModel.getRunsSortedByDate().observe(requireActivity(), runs ->  {
            // Add data to both line charts
            ArrayList<Entry> lineEntriesSpeed = new ArrayList<Entry>();
            ArrayList<Entry> lineEntriesDistance = new ArrayList<Entry>();

            Collections.reverse(runs);
            for(Run run:runs){
                lineEntriesSpeed.add(new Entry(lineEntriesSpeed.size(),run.getAvgSpeedInKMH()));
                lineEntriesDistance.add(new Entry(lineEntriesDistance.size(),run.getDistanceInMetres()));
            }

            LineDataSet set1 = new LineDataSet(lineEntriesSpeed,"Set 1");
            LineDataSet set2 = new LineDataSet(lineEntriesDistance,"Set 2");

            ArrayList<ILineDataSet> datasetsSpeed = new ArrayList<>();
            ArrayList<ILineDataSet> datasetsDistance = new ArrayList<>();

            datasetsSpeed.add(set1);
            datasetsDistance.add(set2);

            LineData dataSpeed = new LineData(datasetsSpeed);
            LineData dataDistance = new LineData(datasetsDistance);

            lineChartSpeedPerRun.setData(dataSpeed);
            lineChartSpeedPerRun.getLineData().setValueTextSize(14);
            lineChartDistancePerRun.setData(dataDistance);
            lineChartDistancePerRun.getLineData().setValueTextSize(14);
        });

        viewModel.getTotalDistancePerDay().observe(requireActivity(), days -> {
            // Add data to distance per day bar chart
            // Initialise hashmap with 0m distance for each day
            Map<String, Integer> dayMap = new HashMap<>();
            dayMap.put("Sun",0);
            dayMap.put("Mon",0);
            dayMap.put("Tue",0);
            dayMap.put("Wed",0);
            dayMap.put("Thu",0);
            dayMap.put("Fri",0);
            dayMap.put("Sat",0);

            // Assign distance for each day
            for(DayTuple day : days){
                switch (day.day){
                    case 0:
                        dayMap.put("Sun",day.dist);
                        break;
                    case 1:
                        dayMap.put("Mon",day.dist);
                        break;
                    case 2:
                        dayMap.put("Tue",day.dist);
                        break;
                    case 3:
                        dayMap.put("Wed",day.dist);
                        break;
                    case 4:
                        dayMap.put("Thu",day.dist);
                        break;
                    case 5:
                        dayMap.put("Fri",day.dist);
                        break;
                    case 6:
                        dayMap.put("Sat",day.dist);
                        break;
                }
            }
            // Put data into bar chart
            ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
            barEntries.add(new BarEntry(0,dayMap.get("Sun")));
            barEntries.add(new BarEntry(1,dayMap.get("Mon")));
            barEntries.add(new BarEntry(2,dayMap.get("Tue")));
            barEntries.add(new BarEntry(3,dayMap.get("Wed")));
            barEntries.add(new BarEntry(4,dayMap.get("Thu")));
            barEntries.add(new BarEntry(5,dayMap.get("Fri")));
            barEntries.add(new BarEntry(6,dayMap.get("Sat")));
            BarDataSet thisWeek = new BarDataSet(barEntries,"Set 1");
            ArrayList<IBarDataSet> datasets = new ArrayList<>();
            datasets.add(thisWeek);

            BarData barDay = new BarData(datasets);
            barChartDistancePerDay.setData(barDay);
            barChartDistancePerDay.getBarData().setValueTextSize(14);

            final String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            // Set the value formatter
            XAxis xAxis = barChartDistancePerDay.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(weekdays));
        });

    }
}