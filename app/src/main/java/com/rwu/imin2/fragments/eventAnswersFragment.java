package com.rwu.imin2.fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rwu.imin2.viewModel.MainActivity;
import com.rwu.imin2.R;
import com.rwu.imin2.model.Event;
import com.rwu.imin2.model.FirebaseHandler;
import com.rwu.imin2.model.Question;
import com.rwu.imin2.model.QuestionList;

public class eventAnswersFragment extends Fragment {

    private FirebaseHandler data;
    private Event mEvent;
    private LinearLayout LLeventAnswersLayout;


    public eventAnswersFragment() {
        // Required empty public constructor
    }

    public static eventAnswersFragment newInstance(String param1, String param2) {
        eventAnswersFragment fragment = new eventAnswersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize FirebaseHandler
        data = new FirebaseHandler(getContext());
        // set mEvent as a Object from the clicked Event
        if (getArguments() != null) {
            mEvent = new Event(data.getCurrentUser().getUserID(), getArguments().getString("eventId"),
                    getArguments().getString("eventTitle"), getArguments().getString("eventDesc"), getArguments().getString("eventDate"),
                    getArguments().getString("eventTime"), getArguments().getStringArrayList("invitedUsers"), new QuestionList<Question>(MainActivity.QuestionList));
            mEvent.setEventId(getArguments().getString("eventId"));
            mEvent.setCreatorDisplayName(getArguments().getString("eventCreatorName"));
            mEvent.setCreatorUserId(getArguments().getString("eventCreatorId"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_answers, container, false);

        // Setting LayoutParams
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16,16,16,16);

        LLeventAnswersLayout = view.findViewById(R.id.LLeventAnswersLayout);
        // build view for the answers and handle if no questions inside the event
        if (mEvent.getQuestionList().size() == 0) {
            TextView tv = new TextView(getContext());
            tv.setText("Bisher keine Antworten zu dieser Veranstaltung");
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            LLeventAnswersLayout.addView(tv);
        } else {
            for (int i = 0; i < mEvent.getQuestionList().size(); i++) {
                LinearLayout ll = new LinearLayout(getContext());
                ll.setLayoutParams(layoutParams);
                ll.setOrientation(LinearLayout.VERTICAL);

                Question q = mEvent.getQuestionList().get(i);
                TextView questionTitle = new TextView(getContext());
                questionTitle.setText(q.getTitle());
                questionTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                questionTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                questionTitle.setTypeface(null, Typeface.BOLD);
                // Set Margins bottom and top for all except first questionTitle
                if(i > 0) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) questionTitle.getLayoutParams();
                    lp.setMargins(lp.getMarginStart(),20,lp.getMarginEnd(),20);
                    questionTitle.setLayoutParams(lp);
                }
                ll.addView(questionTitle);
                LLeventAnswersLayout.addView(ll);
                data.getAnswersToEvent(mEvent, ll, i);
            }
        }

        return view;
    }
}