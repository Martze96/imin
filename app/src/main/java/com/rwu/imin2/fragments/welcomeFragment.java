package com.rwu.imin2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rwu.imin2.viewModel.LoginActivity;
import com.rwu.imin2.R;
import com.rwu.imin2.model.FirebaseHandler;


public class welcomeFragment extends Fragment {

    private TextView TVwelcomeText;
    private Button BTNlogout;
    private FirebaseHandler data;

    public welcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new FirebaseHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        TVwelcomeText = view.findViewById(R.id.TVwelcomeText);
        BTNlogout = view.findViewById(R.id.BTNlogout);

        // set Welcometext with the Name of the currently logged in User
        if (data.getCurrentUser() != null) {
            TVwelcomeText.setText("Willkommen " + data.getCurrentUser().getDisplayName() + "!");
        }

        // onClick logout user and navigate to LoginActivity
        BTNlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.signOutCurrentUser();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
        return view;
    }
}