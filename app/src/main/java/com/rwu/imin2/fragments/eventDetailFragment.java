package com.rwu.imin2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rwu.imin2.viewModel.MainActivity;
import com.rwu.imin2.R;
import com.rwu.imin2.model.Event;
import com.rwu.imin2.model.FirebaseHandler;
import com.rwu.imin2.model.Question;
import com.rwu.imin2.model.QuestionList;

import java.util.ArrayList;

public class eventDetailFragment extends Fragment {

    private FirebaseHandler data;
    private TextView TVeventTitle;
    private String eventId;
    private TextView MLTeventDesc;
    private TextView eventDate;
    private TextView eventTime;
    private TextView invitedUsers;
    private TextView eventQuestions;
    private ConstraintLayout CLeventDetailLayout;
    private TextView TVeventDetailCreatorDisplayName;
    private LinearLayout LLeventQuestionsLayout;

    private Event clickedEvent;
    private boolean isInvitedUser;

    private Button deleteButton;
    private Button editButton;
    private Button answerButton;
    private Button showAnswersButton;


    public eventDetailFragment() {
        // Required empty public constructor
        eventDetailFragment fragment = this;
        Bundle args = new Bundle();
        fragment.setArguments(args);

    }

    public static eventDetailFragment newInstance(String param1, String param2) {
        eventDetailFragment fragment = new eventDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new FirebaseHandler(getContext());
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            isInvitedUser = getArguments().getBoolean("invited");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eventdetail, container, false);

        TVeventTitle = view.findViewById(R.id.TVeventDetailEventTitle);
        MLTeventDesc = view.findViewById(R.id.MLTeventDetailEventDesc);
        eventDate = view.findViewById(R.id.TVeventDetailEventDate);
        eventTime = view.findViewById(R.id.TVeventDetailEventTime);
        invitedUsers = view.findViewById(R.id.TVeventDetailsInvitedUserNames);
        eventQuestions = view.findViewById(R.id.TVeventDetailaskedQuestions);
        CLeventDetailLayout = view.findViewById(R.id.CLeventDetailLayout);
        TVeventDetailCreatorDisplayName = view.findViewById(R.id.TVeventDetailCreatorDisplayName);
        LLeventQuestionsLayout = view.findViewById(R.id.linearLayout);

        // initialize the clicked Event
        clickedEvent = data.getEvent(eventId);
        clickedEvent.setQuestionList(new QuestionList<Question>(data.getQuestionsFromEvent(eventId, eventQuestions)));

        // set Viewtexts based on event attributes
        TVeventTitle.setText(clickedEvent.getTitle());
        TVeventDetailCreatorDisplayName.setText("Von: " + clickedEvent.getCreatorDisplayName());
        MLTeventDesc.setText(clickedEvent.getDescription());
        eventDate.setText(clickedEvent.getEventDate());
        eventTime.setText(clickedEvent.getEventTime());

        // extract String of all invited Users and insert into textview
        String allUsers = "";
        if (clickedEvent.getInvitedUsers() != null) {
            for (String names : clickedEvent.getInvitedUsers()) {
                allUsers += names += ", ";
            }
        }
        invitedUsers.setText(allUsers);

        editButton = view.findViewById(R.id.BTNeventdetailEditButton);
        deleteButton = view.findViewById(R.id.BTNeventdetailDeleteButton);
        answerButton = view.findViewById(R.id.BTNeventDetailAnswerButton);
        showAnswersButton = view.findViewById(R.id.BTNeventDetailShowAnswers);

        // hide/show options if the user is invited to the event
        if (isInvitedUser) {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            answerButton.setVisibility(View.VISIBLE);
            //set correct constraints, because if you make a view gone, than the constraint is also gone
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(CLeventDetailLayout);
            constraintSet.connect(LLeventQuestionsLayout.getId(), ConstraintSet.BOTTOM, answerButton.getId(), ConstraintSet.TOP, 24);
            constraintSet.applyTo(CLeventDetailLayout);
            showAnswersButton.setVisibility(View.GONE);
        }
        // onClick show Answers to the Event
        showAnswersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build Bundle to transfer data to eventAnswersFragment
                Bundle newBundle = new Bundle();
                newBundle.putString("eventId", eventId);
                newBundle.putString("eventCreatorId", clickedEvent.getCreatorUserId());
                newBundle.putString("eventCreatorName", clickedEvent.getCreatorDisplayName());
                newBundle.putString("eventTitle", clickedEvent.getTitle());
                newBundle.putString("eventDesc", clickedEvent.getDescription());
                newBundle.putString("eventDate", clickedEvent.getEventDate());
                newBundle.putString("eventTime", clickedEvent.getEventTime());
                newBundle.putString("eventTimeHour", clickedEvent.getEventTimeHour());
                newBundle.putString("eventTimeMinute", clickedEvent.getEventTimeMinute());
                newBundle.putStringArrayList("invitedUsers", (ArrayList<String>) clickedEvent.getInvitedUsers());
                newBundle.putSerializable("questionList", clickedEvent.getQuestionList());
                // navigate to eventAnwersFragment
                final NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_eventDetailFragment_to_eventAnswersFragment, newBundle);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build Bundle to transfer data to createEventFragment
                Bundle newBundle = new Bundle();
                newBundle.putString("eventId", eventId);
                newBundle.putString("eventCreatorId", clickedEvent.getCreatorUserId());
                newBundle.putString("eventCreatorName", clickedEvent.getCreatorDisplayName());
                newBundle.putString("eventTitle", clickedEvent.getTitle());
                newBundle.putString("eventDesc", clickedEvent.getDescription());
                newBundle.putString("eventDate", clickedEvent.getEventDate());
                newBundle.putString("eventTime", clickedEvent.getEventTime());
                newBundle.putString("eventTimeHour", clickedEvent.getEventTimeHour());
                newBundle.putString("eventTimeMinute", clickedEvent.getEventTimeMinute());
                newBundle.putStringArrayList("invitedUsers", (ArrayList<String>) clickedEvent.getInvitedUsers());
                newBundle.putSerializable("questionList", clickedEvent.getQuestionList());
                // navigate to createEventFragment
                final NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_eventDetailFragment_to_thirdFragment, newBundle);
            }
        });
        // onClick delete Event
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deletes Event from Firebase and navigates back to myEventsFragment
                data.deleteEvent(clickedEvent.getEventId(), getContext(), v);
            }
        });
        // onClick answer Event
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the Event has no questions
                if (MainActivity.QuestionList.size() == 0) {
                    answerButton.setEnabled(false);
                    answerButton.setText("Keine Fragen offen");
                    return;
                }
                // build bundle for data transfer
                Bundle newBundle = new Bundle();
                newBundle.putString("eventId", eventId);
                newBundle.putString("eventCreatorId", clickedEvent.getCreatorUserId());
                newBundle.putString("eventCreatorName", clickedEvent.getCreatorDisplayName());
                newBundle.putString("eventTitle", clickedEvent.getTitle());
                newBundle.putString("eventDesc", clickedEvent.getDescription());
                newBundle.putString("eventDate", clickedEvent.getEventDate());
                newBundle.putString("eventTime", clickedEvent.getEventTime());
                newBundle.putString("eventTimeHour", clickedEvent.getEventTimeHour());
                newBundle.putString("eventTimeMinute", clickedEvent.getEventTimeMinute());
                newBundle.putStringArrayList("invitedUsers", (ArrayList<String>) clickedEvent.getInvitedUsers());
                newBundle.putSerializable("questionList", clickedEvent.getQuestionList());
                // navigate to answerFragment
                final NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_eventDetailFragment_to_answerFragment, newBundle);
            }
        });
        return view;
    }

}
