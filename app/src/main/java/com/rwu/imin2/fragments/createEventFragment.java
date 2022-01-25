package com.rwu.imin2.fragments;

import static com.rwu.imin2.viewModel.MainActivity.EventList;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rwu.imin2.viewModel.MainActivity;
import com.rwu.imin2.adapters.EventQuestionRecyclerViewAdapter;
import com.rwu.imin2.enums.questionType;
import com.rwu.imin2.model.Event;
import com.rwu.imin2.model.FirebaseHandler;
import com.rwu.imin2.nestedFragments.personAdderFragment;
import com.rwu.imin2.R;
import com.rwu.imin2.model.Question;
import com.rwu.imin2.model.QuestionList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link createEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class createEventFragment extends Fragment {

    // Event to eventually update
    private Event editingEvent;
    // Eventtitle and Description Input
    private EditText eventTitle;
    private EditText eventDesc;
    // datepicker attributes
    private Button datePickerButton;
    private DatePickerDialog datePickerDialog;
    private TextView dateText;
    private int year, month, day;
    // timepicker
    private TimePicker timepicker;
    // premade choosable Questions
    private Question initialQuestion_presence;
    private Question initialQuestion_covid;
    private Switch initialQuestionCovidSwitch;
    private Switch InitialQuestionPresenceSwitch;
    // local QuestionList attribute
    private QuestionList<Question> questionList;
    // questionlist View Attributes
    private Button createQuestion;
    public RecyclerView RVquestionList;
    private EventQuestionRecyclerViewAdapter myRVadapter;
    // invited Users View Attributes
    private TextView TVinvitedUsers;
    // Nested Fragment to handle invited Users
    private Fragment personAdder;
    // Create Event Button
    private Button createEventButton;

    private FirebaseHandler data;


    public createEventFragment(Bundle bundle) {
        createEventFragment fragment = new createEventFragment();
        Bundle args = new Bundle();
    }

    public createEventFragment() {
        // Required empty public constructor
    }

    public static createEventFragment newInstance(String param1, String param2) {
        createEventFragment fragment = new createEventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize FirebaseHandler
        data = new FirebaseHandler(getContext());

        if (getArguments() != null) {
            // Get Questions from Arguments
            questionList = new QuestionList<Question>();
            questionList.addAll((QuestionList) getArguments().getSerializable("questionList"));
        } else {
            // Initialize new if no Arguments
            questionList = new QuestionList<Question>();
        }

        // initialize premade Questions
        initialQuestion_covid = new Question("INITIAL_COVID" + UUID.randomUUID(), "Ihre 3G Information",
                questionType.MULTI, new ArrayList<String>(Arrays.asList("Getestet", "Geimpft", "Genesen")));
        initialQuestion_presence = new Question("INITIAL_PRESENCE" + UUID.randomUUID(), "Werden Sie teilnehmen?",
                questionType.SINGLE, new ArrayList<String>(Arrays.asList("Ich werde teilnehmen", "ich werde nicht teilnehmen")));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createevent, container, false);

        // intitialize FirebaseHandler
        data = new FirebaseHandler(this.getContext());

        eventTitle = view.findViewById(R.id.PTeventTitle);

        eventDesc = view.findViewById(R.id.MLPTeventDescription);
        timepicker = view.findViewById(R.id.timePicker);
        timepicker.setIs24HourView(true);
        dateText = (TextView) view.findViewById(R.id.TVchosenDate);

        // If Updating Event or navigating from QuestionFragment fill up EditingEvent Attribute to work with
        if (getArguments() != null) {
            eventTitle.setText(getArguments().getString("eventTitle"));
            eventDesc.setText(getArguments().getString("eventDesc"));
            timepicker.setHour(Integer.parseInt(getArguments().getString("eventTimeHour")));
            timepicker.setMinute(Integer.parseInt(getArguments().getString("eventTimeMinute")));
            createQuestion = (Button) view.findViewById(R.id.BTNcreateQuestion);
            dateText.setText(getArguments().getString("eventDate"));
            // If working on existing Event with ID, work with the ID the Event had to update it
            if (getArguments().getString("eventId") != null) {
                // initialize Event with existing attributes from arguments
                editingEvent = new Event(data.getCurrentUser().getUserID(), getArguments().getString("eventId"),
                        getArguments().getString("eventTitle"), getArguments().getString("eventDesc"), getArguments().getString("eventDate"),
                        getArguments().getString("eventTime"), getArguments().getStringArrayList("invitedUsers"), new QuestionList<Question>(MainActivity.QuestionList));
                questionList = editingEvent.getQuestionList();
                editingEvent.setCreatorDisplayName(getArguments().getString("eventCreatorName"));
                editingEvent.setCreatorUserId(getArguments().getString("eventCreatorId"));
            }

        } else {
            dateText.setText("Kein Datum ausgewählt");
        }


        // create DatePicker
        datePickerButton = view.findViewById(R.id.BTNdatePicker);
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        // handle consistent DateString data (leading zeros etc.)
                        String monthString = "";
                        String dayString;
                        if (month < 10) {
                            monthString = "0" + (month + 1);
                        } else {
                            monthString = String.valueOf(month + 1);
                        }
                        if (day < 10) {
                            dayString = "0" + day;
                        } else {
                            dayString = String.valueOf(day);
                        }
                        dateText.setText(dayString + "." + monthString + "." + year);
                    }
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());


        //Build Questionlist Recyclerview
        //Adapter und View erzeugen
        myRVadapter = new EventQuestionRecyclerViewAdapter(questionList);
        RVquestionList = (RecyclerView) view.findViewById(R.id.RVquestionList);
        RVquestionList.setLayoutManager(new LinearLayoutManager(getContext()));
        RVquestionList.setAdapter(myRVadapter);
        // WrapContent expands upwards, so i wrote this Layout HeightHandler
        setListViewHeightBasedOnChildren(RVquestionList);

        //Switch Logic for premade Questions
        initialQuestionCovidSwitch = (Switch) view.findViewById(R.id.switch3GQuestion);
        initialQuestionCovidSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If switch on
                if (isChecked) {
                    // and already there
                    if (questionList.containsQuestionTitle(initialQuestion_covid)) {
                        return;
                        // and not there
                    } else {
                        // and list empty
                        if (questionList.size() == 0) {
                            questionList.add(initialQuestion_covid);
                        }
                        // and not empty
                        else {
                            questionList.add(1,initialQuestion_covid);
                        }
                    }
                    // If switch off
                } else {
                    // and already there
                    if (questionList.containsQuestionTitle(initialQuestion_covid)) {
                        questionList.removeQuestionByTitle(initialQuestion_covid);
                    }

                }
                // Notify and set height based on content
                myRVadapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(RVquestionList);
            }
        });
        InitialQuestionPresenceSwitch = (Switch) view.findViewById(R.id.switchPresence);
        InitialQuestionPresenceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Switch on
                if (isChecked) {
                    // and already there
                    if (questionList.containsQuestionTitle(initialQuestion_presence)) {
                        return;
                        // and not there
                    } else {
                        questionList.add(0,initialQuestion_presence);
                    }
                    // Switch Off
                } else {
                    // and already there
                    if (questionList.containsQuestionTitle(initialQuestion_presence)) {
                        questionList.removeQuestionByTitle(initialQuestion_presence);
                    }
                }
                // Notify and set height based on content
                myRVadapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(RVquestionList);
            }
        });

        // initialize Label to request Focus on after coming from QuestionFragment
        TVinvitedUsers = view.findViewById(R.id.TVLabelSendTo);

        // onClick create Event Handler
        createEventButton = (Button) view.findViewById(R.id.createEventButton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Insert PersonAdder Fragment for invited Users (Method to make it shorter here)
        insertNestedFragment();
        // initialize Navcontroller
        final NavController navController = Navigation.findNavController(view);

        createQuestion = (Button) view.findViewById(R.id.BTNcreateQuestion);
        // onClick create Question, create Bundle and navigate to questionFragment with the bundle
        createQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (editingEvent == null) {
                    bundle.putSerializable("questionList", questionList);
                } else {
                    bundle.putSerializable("questionList", editingEvent.getQuestionList());
                    bundle.putString("eventId", getArguments().getString("eventId"));
                }
                bundle.putString("eventTitle", eventTitle.getText().toString());
                bundle.putString("eventDesc", eventDesc.getText().toString());
                bundle.putString("eventDate", dateText.getText().toString());
                bundle.putString("eventTimeHour", String.valueOf(timepicker.getHour()));
                bundle.putString("eventTimeMinute", String.valueOf(timepicker.getMinute()));
                bundle.putSerializable("invitedUsers", getAllInputtedUsers());
                navController.navigate(R.id.action_thirdFragment_to_questionFragment, bundle);
            }
        });

        // Handle Switch Logic of premade Questions if coming from questionFragment to check them if containing
        if (questionList.containsQuestionTitle(initialQuestion_covid)) {
            initialQuestionCovidSwitch.setChecked(true);
        }
        if (questionList.containsQuestionTitle(initialQuestion_presence)) {
            InitialQuestionPresenceSwitch.setChecked(true);
        }
    }


    /**
     * Creates the nested Fragment personAdder which handles the input for invited Users
     */
    private void insertNestedFragment() {
        // If updating Event, show invited users, else create new Fragment
        if (editingEvent != null) {
            Bundle newBundle = new Bundle();

            if (editingEvent.getInvitedUsers() != null) {
                newBundle.putStringArrayList("invitedUsers", (ArrayList<String>) editingEvent.getInvitedUsers());
                personAdder = new personAdderFragment(newBundle);
            } else {
                newBundle.putStringArrayList("invitedUsers", new ArrayList<String>());
                personAdder = new personAdderFragment(newBundle);
            }

        } else {
            personAdder = new personAdderFragment();
        }
        // Transaction
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutPersonAdder, personAdder).commit();
    }

    /**
     * Simple Method to show the DatepickerDialog onClick
     */
    private void showDatePicker() {
        datePickerDialog.show();
    }

    /**
     * (unused atm) get all QuestionTitles from a List
     *
     * @param list
     * @return Sting ArrayList with the Questiontitles
     */
    private ArrayList<String> getAllQuestionTitles(List<Question> list) {
        ArrayList<String> questionTitles = new ArrayList<String>();

        for (Question q : list) {
            questionTitles.add(q.getTitle());
        }
        return questionTitles;
    }

    /**
     * Sets the Height of a RecyclerViewer based on the childcounts. expands downwards, instead of wrap content which expands upwards
     *
     * @param rv
     */
    public void setListViewHeightBasedOnChildren(RecyclerView rv) {
        ViewGroup.LayoutParams params = rv.getLayoutParams();
        params.height = questionList.size() * 185;
        RVquestionList.setLayoutParams(params);
        RVquestionList.scrollToPosition(RVquestionList.getChildCount() - 1);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            // Set Focus down to Questionlist if coming form questionFragment (used TVinvitedUsers because better View)
            if (getArguments().getBoolean("fromQuestionCreation")) {
                TVinvitedUsers.requestFocus(TextView.FOCUS_DOWN);
            }
        }
    }

    /**
     * creates or updates an Event and sends it to Firebase
     */
    private void createEvent() {
        // If working on a new Event
        if (editingEvent == null) {
            // Create new Event with the attributes of existing and inputted attributes
            Event event = new Event();
            event.setEventId(event.getEventId());
            // Eventtitle is necessary to input, so handle Error if input empty
            if (TextUtils.isEmpty(eventTitle.getText().toString())) {
                eventTitle.setError("Veranstaltungstitel darf nicht leer sein!");
                eventTitle.requestFocus();
            } else {
                event.setTitle(eventTitle.getText().toString());
                event.setDescription(eventDesc.getText().toString());
                event.setEventDate(dateText.getText().toString());
                String timeMinuteString = "";
                // set a zero so minute can be f. ex. "19:07" instead of "19:7")
                if (timepicker.getMinute() < 10) {
                    timeMinuteString = "0" + String.valueOf(timepicker.getMinute());
                } else {
                    timeMinuteString = String.valueOf(timepicker.getMinute());
                }
                event.setEventTime(String.valueOf(timepicker.getHour()) + ":" + timeMinuteString);
                event.setEventTimeHour(String.valueOf(timepicker.getHour()));
                event.setEventTimeMinute(timeMinuteString);
                event.setInvitedUsers(getAllInputtedUsers());
                // get questions to the Event
                event.setQuestionList(questionList);
                // add to Firebase
                data.addEvent(event);
                // add to static List
                EventList.add(event);
                // navigate to myEventsFragment
                Navigation.findNavController(getView()).navigate(R.id.action_thirdFragment_to_secondFragment);
            }
        }
        // If working on a existing Event
        else {
            editingEvent.setEventId(getArguments().getString("eventId"));
            editingEvent.setTitle(eventTitle.getText().toString());
            editingEvent.setDescription(eventDesc.getText().toString());
            editingEvent.setEventDate(dateText.getText().toString());
            String timeMinuteString = "";
            // set a zero so minute can be f. ex. "19:07" instead of "19:7")
            if (timepicker.getMinute() < 10) {
                timeMinuteString = "0" + String.valueOf(timepicker.getMinute());
            } else {
                timeMinuteString = String.valueOf(timepicker.getMinute());
            }
            editingEvent.setEventTime(String.valueOf(timepicker.getHour()) + ":" + timeMinuteString);
            editingEvent.setEventTimeHour(String.valueOf(timepicker.getHour()));
            editingEvent.setEventTimeMinute(String.valueOf(timeMinuteString));
            editingEvent.setInvitedUsers(getAllInputtedUsers());
            editingEvent.setCreatorDisplayName(data.getCurrentUser().getDisplayName());
            editingEvent.setCreatorUserId(data.getCurrentUser().getUserID());
            // get questions to the Event
            editingEvent.setQuestionList(questionList);
            // add to Firebase
            data.addEvent(editingEvent);
            // add to static List
            EventList.add(editingEvent);
            // navigate to myEventsFragment
            Navigation.findNavController(getView()).navigate(R.id.action_thirdFragment_to_secondFragment);
        }

    }

    /**
     * gets all inputted Usernames from Nested Fragment personAdderLayout
     * @return List of username Strings
     */
    private ArrayList<String> getAllInputtedUsers() {
        // get ParentLayout
        LinearLayout pAdder = (LinearLayout) personAdder.getView().findViewById(R.id.LLpersonAdder);
        // create List to fill with usernames
        ArrayList<String> invitedUserList = new ArrayList<String>();
        // loop over child edittexts
        for (int i = 0; i < pAdder.getChildCount(); i++) {
            if (pAdder.getChildCount() == 0) {
                Toast.makeText(getContext(), "Keine Personen eingetragen.", Toast.LENGTH_SHORT).show();
            } else {
                View nextChild = pAdder.getChildAt(i);
                invitedUserList.add(((TextView) nextChild).getText().toString());
            }
        }
        return invitedUserList;
    }

}