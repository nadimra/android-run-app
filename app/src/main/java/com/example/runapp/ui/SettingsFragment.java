package com.example.runapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.runapp.R;
import com.example.runapp.other.Constants;

/**
 * Allows user to update their current user profile
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    EditText usernameField;
    EditText weightField;
    SharedPreferences sharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        usernameField = (EditText) v.findViewById(R.id.usernameSettings);
        weightField = (EditText) v.findViewById(R.id.weightSettings);

        // Get current user details
        String username = sharedPreferences.getString(Constants.KEY_NAME,"Name");
        Float weight = sharedPreferences.getFloat(Constants.KEY_WEIGHT,50);

        // Sets the UI to the current user profile details
        usernameField.setText(username+"");
        weightField.setText(weight+"");

        Button continueButton = (Button) v.findViewById(R.id.settingsContinue);
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
            case R.id.settingsContinue:
                // Validate fields before updating preferences
                if (validateFields()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.KEY_NAME, usernameField.getText().toString());
                    editor.putFloat(Constants.KEY_WEIGHT, Float.parseFloat(weightField.getText().toString()));
                    Toast.makeText(getActivity().getApplicationContext(), "Settings Updated", Toast.LENGTH_LONG).show();
                    editor.apply();
                }
                break;
        }
    }

    /**
     * Makes sure that the user enters the correct details
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