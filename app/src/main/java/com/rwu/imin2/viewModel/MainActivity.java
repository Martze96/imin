package com.rwu.imin2.viewModel;


import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;


import android.content.Intent;
import android.os.Bundle;

import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rwu.imin2.R;
import com.rwu.imin2.model.Answer;
import com.rwu.imin2.model.Event;
import com.rwu.imin2.model.FirebaseHandler;
import com.rwu.imin2.model.Question;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private NavHostFragment navHostFragment;

    private TextView TVtopUsername;

    /**
     * The following Static List were created to get Data out of Firebase functions. Otherwise every Firebase Function
     * has to be specific to a View and even if, the asynchronous request/query of Data from Firebase could destroy Fragments.
     * (I tried several approaches (CountDownLatch, creating Threads that wait for each other, etc... ) to
     * wait for the Firebase Data Retrieval and nothing worked.).
     */
    // STATIC LIST FOR MY CREATED EVENTS
    public static List<Event> EventList;
    // STATIC LIST FOR EVENTS USER GOT INVITED TO
    public static List<Event> InvitationList;
    // STATIC LIST FOR QUESTIONS WITHIN A EVENT
    public static List<Question> QuestionList;
    // STATIC LIST FOR ANSWERS OF SPECIFIC EVENT
    public static List<Answer> AnswerList;

    private FirebaseHandler data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TVtopUsername = findViewById(R.id.TVtopUsername);

        //Initialize Statics
        EventList = new ArrayList<Event>();
        InvitationList = new ArrayList<>();
        QuestionList = new ArrayList<Question>();
        AnswerList = new ArrayList<Answer>();

        //Setup Navigation with BottomNavigation menu
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //Initialize FirebaseHandler
        data = new FirebaseHandler(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if User is already logged in, sets the topBar TextView to E-Mail of currently logged in User
        if (data.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            TVtopUsername.setText(data.getCurrentUser().getEmail());
        }
    }
}