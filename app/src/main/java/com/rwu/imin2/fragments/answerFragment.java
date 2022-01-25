package com.rwu.imin2.fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rwu.imin2.viewModel.MainActivity;
import com.rwu.imin2.R;
import com.rwu.imin2.enums.questionType;
import com.rwu.imin2.model.Answer;
import com.rwu.imin2.model.Event;
import com.rwu.imin2.model.FirebaseHandler;
import com.rwu.imin2.model.Question;
import com.rwu.imin2.model.QuestionList;

import java.util.ArrayList;
import java.util.Arrays;

public class answerFragment extends Fragment {

    private TextView TVquestionTitle;
    private EditText MLTadditionalInfo;

    private Event mEvent;
    private QuestionList mQuestionList;
    private Question activeQuestion;
    private LinearLayout LLanswerOptions;
    private Button BTNanswerButton;
    private Answer currentAnswer;
    private int questionIndex;

    private LinearLayout LLanswerOverview;
    private Button BTNanswerFullSubmitButton;
    private ConstraintLayout CLanswerFragmentLayout;
    private TextView TVanswerOverViewTopTitle;
    private ScrollView SVanswerOverviewLayout;

    private FirebaseHandler data;


    public answerFragment() {
        // Required empty public constructor
    }

    public static answerFragment newInstance(String param1, String param2) {
        answerFragment fragment = new answerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        data = new FirebaseHandler(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_answer, container, false);

        TVquestionTitle = view.findViewById(R.id.TVanswerTopTitle);
        MLTadditionalInfo = view.findViewById(R.id.MLTanswerAdditionalInfo);
        LLanswerOptions = view.findViewById(R.id.LLanswerOptions);
        BTNanswerFullSubmitButton = view.findViewById(R.id.BTNanswerFullSubmit);
        LLanswerOverview = view.findViewById(R.id.LLanswerOverVIew);
        CLanswerFragmentLayout = view.findViewById(R.id.CLanswerFragmentLayout);
        TVanswerOverViewTopTitle = view.findViewById(R.id.TVanswerOverViewTopTitle);
        SVanswerOverviewLayout = view.findViewById(R.id.SVanswerOverViewLayout);

        // Get Arguments and build the Event which was clicked
        if (getArguments() != null) {
            mEvent = new Event(data.getCurrentUser().getUserID(), getArguments().getString("eventId"),
                    getArguments().getString("eventTitle"), getArguments().getString("eventDesc"), getArguments().getString("eventDate"),
                    getArguments().getString("eventTime"), getArguments().getStringArrayList("invitedUsers"), new QuestionList<Question>(MainActivity.QuestionList) /*(QuestionList<Question>) getArguments().getSerializable("questionList") */);
            System.out.println(Arrays.asList(mEvent.getQuestionList()));
            mQuestionList = new QuestionList<Question>(MainActivity.QuestionList);
            //Setting Creator back to Creator bc Event makes Creator to the User that answers it
            mEvent.setEventId(getArguments().getString("eventId"));
            mEvent.setCreatorUserId(getArguments().getString("eventCreatorId"));
            mEvent.setCreatorDisplayName(getArguments().getString("eventCreatorName"));
        }
        // Index of Questionlist of the Event
        questionIndex = 0;
        askQuestion(questionIndex);

        BTNanswerButton = view.findViewById(R.id.BTNanswerSubmitAnswer);
        BTNanswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!submitAnswer(questionIndex)) {
                    return;
                }
                questionIndex++;
                if (questionIndex >= mEvent.getQuestionList().size()) {
                    /**
                     * IMPORTANT! ADD ALL ANSWERS TO FIREBASE AFTER OVERVIEW, BECAUSE DESTROYS THE ORDER OF THE FIREBASEHANDLER-FUNCTIONS (ITS ASYNCHRONOUS)
                     *
                     * BEFORE THAT, WE NEED A OVERVIEW OF ALL QUESTION WITH OUR ANSWERS AND A BUTTON TO SEND IT OUT
                     * CAUTION! EXISTING ANSWERS ARE DELETED ONE BY ONE FROM THE MOMENT YOU ANSWERED A QUESTION THE SECOND TIME!
                     * THEY WILL BE REPLACED ONLY IF YOU SUBMIT THEM FULLY...
                     */
                    showOverview();
                } else {
                    askQuestion(questionIndex);
                }
            }
        });

        BTNanswerFullSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.addAllAnswersToQuestions(mEvent);
                Toast.makeText(v.getContext(), "Veranstaltung erfolgreich beantwortet", Toast.LENGTH_SHORT).show();
                final NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_answerFragment_to_secondFragment);
            }
        });

        return view;
    }

    /**
     * Submits the Answer LOCALLY to the Event and deletes the answer in Firebase if already existing
     *
     * @param index - index which Question should be submitted
     * @return false if questiontype Single was left empty, else true
     */
    private boolean submitAnswer(int index) {

        if (activeQuestion.getType() == questionType.SINGLE) {
            //Get Strings (answer) out of Layout
            RadioGroup rg = (RadioGroup) LLanswerOptions.getChildAt(0);
            RadioButton rb = getView().findViewById(rg.getCheckedRadioButtonId());
            if (rb == null) {
                MLTadditionalInfo.setError("Bitte eine Option auswählen");
                MLTadditionalInfo.requestFocus();
                return false;
            } else {
                // add to the Event
                currentAnswer.getAnswerList().add(rb.getText().toString());
                currentAnswer.setAdditionalInfo(MLTadditionalInfo.getText().toString());
                activeQuestion.setAnswer(currentAnswer);
                mEvent.getQuestionList().set(index, activeQuestion);
            }
        }

        if (activeQuestion.getType() == questionType.MULTI) {
            for (int i = 0; i < LLanswerOptions.getChildCount(); i++) {
                Switch sw = (Switch) LLanswerOptions.getChildAt(i);
                if (sw.isChecked()) {
                    currentAnswer.getAnswerList().add(sw.getText().toString());
                }
            }
            currentAnswer.setAdditionalInfo(MLTadditionalInfo.getText().toString());
            activeQuestion.setAnswer(currentAnswer);
            mEvent.getQuestionList().set(index, activeQuestion);
        }

        data.deleteAnswer(mEvent, activeQuestion);

        // reset view to make it ready for next question
        LLanswerOptions.removeAllViewsInLayout();
        MLTadditionalInfo.setText("");
        return true;
    }

    /**
     * sets up a question on views to answer it
     *
     * @param index - index of Question in questionlist of the event
     */
    private void askQuestion(int index) {
        // get current question
        activeQuestion = (Question) mQuestionList.get(index);
        // if there already is an answer, use it
        if (activeQuestion.getAnswer() != null) {
            currentAnswer = activeQuestion.getAnswer();
            // create a new answer
        } else {
            currentAnswer = new Answer(data.getCurrentUser().getUserID(), data.getCurrentUser().getDisplayName(), new ArrayList<String>(), "", null);
        }

        // set the title to questiontitle
        TVquestionTitle.setText(mEvent.getQuestionList().get(index).getTitle());

        // set Layout params
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(20, 20, 20, 20);
        // build switches if multi-optional question
        if (activeQuestion.getType() == questionType.MULTI) {
            for (int i = 0; i < activeQuestion.getOptionList().size(); i++) {
                Switch newSwitch = new Switch(getContext());
                newSwitch.setText(activeQuestion.getOptionList().get(i));
                newSwitch.setLayoutParams(layoutParams);
                LLanswerOptions.addView(newSwitch);
            }


        }
        // build radiobuttons if single-option question
        if (activeQuestion.getType() == questionType.SINGLE) {
            RadioGroup newRG = new RadioGroup(getContext());
            newRG.setLayoutParams(layoutParams);
            LLanswerOptions.addView(newRG);

            for (int i = 0; i < activeQuestion.getOptionList().size(); i++) {
                RadioButton rb = new RadioButton(getContext());
                rb.setText(activeQuestion.getOptionList().get(i));
                rb.setLayoutParams(layoutParams);
                newRG.addView(rb);
            }
        }
    }

    /**
     * shows the overview of all Questions and the answers that were submitted
     */
    private void showOverview() {
        // Make Overview Views visible, and answers Views gone
        for (int i = 0; i < CLanswerFragmentLayout.getChildCount(); i++) {
            View child = CLanswerFragmentLayout.getChildAt(i);

            if (child.getId() == LLanswerOverview.getId() || child.getId() == BTNanswerFullSubmitButton.getId() || child.getId() == TVanswerOverViewTopTitle.getId() || child.getId() == SVanswerOverviewLayout.getId()) {
                child.setVisibility(View.VISIBLE);
            } else {
                child.setVisibility(View.GONE);
            }
        }

        //Build answerOverView Content
        for (int i = 0; i < mQuestionList.size(); i++) {
            Question q = (Question) mQuestionList.get(i);
            TextView questionTitle = new TextView(getContext());
            String answerText = "";
            TextView answers = new TextView(getContext());
            TextView additionalInfo = new TextView(getContext());

            // Create LayoutParams
            LinearLayout.LayoutParams layoutParamsTitle = new
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTitle.setMargins(0, 10, 0, 10);
            LinearLayout.LayoutParams layoutParamsBottom = new
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsBottom.setMargins(0, 5, 0, 20);

            //set Questiontitle
            questionTitle.setText(q.getTitle());

            // Add answers to answerText and Last one without Comma
            for (int j = 0; j < q.getAnswer().getAnswerList().size(); j++) {
                if (j == q.getAnswer().getAnswerList().size() - 1) {
                    answerText += q.getAnswer().getAnswerList().get(j);
                } else {
                    answerText += q.getAnswer().getAnswerList().get(j) + ", ";
                }

            }
            answers.setText(answerText);

            // Handle if additional information is empty, set Text
            if (!additionalInfo.equals("")) {
                additionalInfo.setText("Zusätzliche Info: " + q.getAnswer().getAdditionalInfo());
            } else {
                additionalInfo.setText("");
            }

            // add LayoutParams
            questionTitle.setLayoutParams(layoutParamsTitle);
            answers.setLayoutParams(new
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            additionalInfo.setLayoutParams(layoutParamsBottom);

            questionTitle.setTextSize(18);
            answers.setTextSize(16);
            answers.setTypeface(null, Typeface.BOLD);

            additionalInfo.setTypeface(null, Typeface.ITALIC);

            // Add to Layout
            LLanswerOverview.addView(questionTitle);
            LLanswerOverview.addView(answers);
            LLanswerOverview.addView(additionalInfo);

        }


    }

}