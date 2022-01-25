package com.rwu.imin2.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.rwu.imin2.viewModel.MainActivity;
import com.rwu.imin2.R;
import com.rwu.imin2.model.Question;
import com.rwu.imin2.model.QuestionList;

import java.util.ArrayList;
import java.util.UUID;

public class questionFragment extends Fragment {

    private Button addOption;
    private int optionsCounter;
    private LinearLayout LLaddedOptions;

    private EditText questionTitle;
    private RadioGroup questionType;
    private RadioButton selectedQuestionType;
    private Bundle newBundle;

    private QuestionList<Question> questionList;


    public questionFragment() {
        // Empty Constructor
    }

    public static questionFragment newInstance(String param1, String param2) {
        questionFragment fragment = new questionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Build Event is already given f.ex. Update an Event to give it back with new/updated questionList
        if (getArguments() != null) {
            newBundle = new Bundle();
            newBundle.putString("eventTitle", getArguments().getString("eventTitle"));
            newBundle.putString("eventDesc", getArguments().getString("eventDesc"));
            newBundle.putString("eventDate", getArguments().getString("eventDate"));
            newBundle.putString("eventTimeHour", getArguments().getString("eventTimeHour"));
            newBundle.putString("eventTimeMinute", getArguments().getString("eventTimeMinute"));
            newBundle.putSerializable("invitedUsers", getArguments().getSerializable("invitedUsers"));
            newBundle.putString("eventId", getArguments().getString("eventId"));

            // Create Questionlist or fill questionList with Question of Arguments
            if ((QuestionList<Question>) getArguments().getSerializable("questionList") == null) {
                questionList = new QuestionList<Question>();
            } else {
                questionList = new QuestionList<Question>();
                questionList.addAll((QuestionList) getArguments().getSerializable("questionList"));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_question, container, false);

        LLaddedOptions = (LinearLayout) view.findViewById(R.id.LLaddedOptions);
        addOption = view.findViewById(R.id.BTNcreateQuestionAddOption);
        // onClick add an Option to the Question
        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for now, limit for 6 different options to choose in a Question
                // Create and add View to Layout
                if (optionsCounter < 6) {
                    EditText editText = new EditText(getActivity());
                    editText.setHint("Option");
                    editText.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    LLaddedOptions.addView(editText);
                    editText.requestFocus();
                    optionsCounter++;
                } else {
                    return;
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // intialize variables for input content
        questionTitle = view.findViewById(R.id.ETcreateQuestionTitle);
        questionType = view.findViewById(R.id.RGcreateQuestionTypeOption);
        ArrayList<String> options = new ArrayList<String>();
        LLaddedOptions = view.findViewById(R.id.LLaddedOptions);
        // initialize navcontroller
        final NavController navController = Navigation.findNavController(view);

        // Event Listener zum hinzufügen der Frage und Navigation zurück zum create Event Fragment
        Button createQuestionButton = view.findViewById(R.id.BTNcreateQuestionCreate);
        // onClick create the Question
        createQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get questiontype input
                int selectedQuestionTypeId = questionType.getCheckedRadioButtonId();
                selectedQuestionType = view.findViewById(selectedQuestionTypeId);
                String questionTypeStringChecker;

                // save string of checked questiontype
                if (selectedQuestionType == null) {
                    questionTypeStringChecker = "";
                } else {
                    questionTypeStringChecker = selectedQuestionType.getText().toString();
                }
                // initialize questiontype enum and define by questiontypestring
                com.rwu.imin2.enums.questionType questionTypeEnum;
                if (questionTypeStringChecker.equals("Einzelne Auswahl der Optionen")) {
                    questionTypeEnum = com.rwu.imin2.enums.questionType.SINGLE;
                } else if (questionTypeStringChecker.equals("Mehrfachauswahl aus den Optionen")) {
                    questionTypeEnum = com.rwu.imin2.enums.questionType.MULTI;
                } else {
                    questionTypeEnum = com.rwu.imin2.enums.questionType.NOT_SELECTED;
                }


                //insert created Options into a List, set Error if empty
                int count = LLaddedOptions.getChildCount();
                View v1 = null;
                for (int i = 0; i < count; i++) {
                    v1 = LLaddedOptions.getChildAt(i);
                    options.add(((EditText) v1).getText().toString());
                }
                if (TextUtils.isEmpty(questionTitle.getText().toString())) {
                    questionTitle.setError("Frage darf nicht leer sein");
                    questionTitle.requestFocus();
                    return;
                }
                // Create Question Object and add it to the fragment variable, and static List
                Question question = new Question("QUESTION_" + UUID.randomUUID().toString(), questionTitle.getText().toString(), questionTypeEnum, options);
                questionList.add(question);
                MainActivity.QuestionList.add(question);
                // append to Bundle for transfer
                newBundle.putSerializable("questionList", questionList);
                // set boolean for createEventFragment if navigating from createQuestion to set the focus back on the questions of createEventFragment
                newBundle.putBoolean("fromQuestionCreation", true);

                // navigate with the created Bundle to createEventFragment
                navController.navigate(R.id.action_questionFragment_to_thirdFragment, newBundle);
            }
        });
    }
}
