package com.example.runapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.runapp.R;
import com.example.runapp.other.Constants;
import com.example.runapp.viewmodels.MainActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Fragment shown on first time setup
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class SetupFragment extends Fragment implements View.OnClickListener {

    private Animation topAnim;
    private Animation bottomAnim;
    private EditText usernameField;
    private EditText weightField;
    private SharedPreferences sharedPreferences;
    private MainActivityViewModel viewModel;

    public SetupFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SetupFragment newInstance(String param1, String param2) {
        SetupFragment fragment = new SetupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setup, container, false);

        // Assign viewmodel
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        ImageView logo = v.findViewById(R.id.logo);
        TextView logoText = v.findViewById(R.id.logoText);
        usernameField = v.findViewById(R.id.usernameField);
        weightField = v.findViewById(R.id.weightField);

        // Create small animation when the user enters the fragment
        topAnim = AnimationUtils.loadAnimation(requireActivity(), R.animator.slide_down);
        bottomAnim = AnimationUtils.loadAnimation(requireActivity(), R.animator.slide_up);

        logo.setAnimation(topAnim);
        logoText.setAnimation(topAnim);
        usernameField.setAnimation(bottomAnim);
        weightField.setAnimation(bottomAnim);

        Button continueButton = (Button) v.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);


        return v;
    }

    /**
     * Handles onclick buttons
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButton:
                // Validates fields before saving the information
                if(validateFields()){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.KEY_NAME, usernameField.getText().toString());
                    editor.putFloat(Constants.KEY_WEIGHT, Float.parseFloat(weightField.getText().toString()));
                    editor.putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false);
                    Toast.makeText(getActivity().getApplicationContext(),"Setup Successful",Toast.LENGTH_LONG).show();
                    editor.apply();
                    exitToHome();
                }
                break;
        }
    }

    /**
     * Send user to main UI
     */
    public void exitToHome(){
        androidx.appcompat.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        BottomNavigationView bottomNavigation=getActivity().findViewById(R.id.bottom_nav);
        bottomNavigation.setVisibility(View.VISIBLE);

        viewModel.setCurrentFragment(R.id.nav_runs);
        Fragment fragment = new RunFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
    }

    /**
     * Ensure user enters the correct information
     * @return
     */
    public boolean validateFields(){
        if(usernameField.getText().length()<3 || usernameField.getText().length()>15){
            Toast.makeText(getActivity().getApplicationContext(),"Enter a name between 3-15 characters",Toast.LENGTH_LONG).show();
            return false;
        }

        String numText = weightField.getText().toString();
        try {
            Float num = Float.parseFloat(numText);
            if(num<0){
                Toast.makeText(getActivity().getApplicationContext(),"Please enter a positive weight (in kg)",Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity().getApplicationContext(),"Please enter a positive weight (in kg)",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}