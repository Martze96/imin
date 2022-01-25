package com.rwu.imin2.nestedFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.rwu.imin2.R;
import com.rwu.imin2.fragments.myEventsFragment;

import java.util.ArrayList;
import java.util.List;

public class personAdderFragment extends Fragment {

    private Button addButton;
    private LinearLayout personAdderLayout;
    private FrameLayout personAdderFrameLayout;
    private ScrollView personAdderScrollView;
    private int personCounter;
    private List<String> invitedUsers;

    /**
     * Constructors
     */
    public personAdderFragment() {
        // Required empty public constructor
    }

    public personAdderFragment(Bundle bundle) {
        this.setArguments(bundle);
        invitedUsers = new ArrayList<String>(getArguments().getStringArrayList("invitedUsers"));
    }

    public static personAdderFragment newInstance(String param1, String param2) {
        personAdderFragment fragment = new personAdderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nested_fragment_personadder, container, false);

        personAdderLayout = (LinearLayout) view.findViewById(R.id.LLpersonAdder);
        personAdderFrameLayout = (FrameLayout) view.findViewById(R.id.frameLayoutPersonAdder);
        addButton = view.findViewById(R.id.BTNaddPerson);

        // Handle LayoutHeight, so that the Layout expands downwards based on childcount
        setListViewHeightBasedOnChildren(personAdderScrollView);
        // Iterator
        personCounter = 0;

        // If there are already invitedUsers in the Event (f.ex. from arguments or editing an Event)
        if(invitedUsers != null) {
            // Create the View and show them
            for(String user : invitedUsers) {
                EditText editText = new EditText(getActivity());
                editText.setText(user);
                editText.setLayoutParams(new
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                personCounter++;
                personAdderLayout.addView(editText);
                setListViewHeightBasedOnChildren(personAdderScrollView);
                scrollDown(getParentFragment().getView().findViewById(R.id.SVcreateEvent));
            }
        }

        // onClick addInvitedUser
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limit 7 atm
                if(personCounter < 7) {
                    // Create Edittext to input one more invited User
                    EditText editText = new EditText(getActivity());
                    editText.setHint("Benutzername");
                    editText.setLayoutParams(new
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    personCounter++;
                    personAdderLayout.addView(editText);
                    setListViewHeightBasedOnChildren(personAdderScrollView);
                    scrollDown(getParentFragment().getView().findViewById(R.id.SVpersonAdder));
                    editText.requestFocus();
                } else {
                    return;
                }

            }
        });

        return view;
    }

    /**
     * Handle LayoutHeight, so that the Layout Height expands downwards based on childcount
     * @param scrollView
     */
    public  void setListViewHeightBasedOnChildren(ScrollView scrollView) {
        if(scrollView != null) {
            ViewGroup.LayoutParams params = scrollView.getLayoutParams();
            params.height = personCounter*120;
            System.out.println(params.height);
            personAdderLayout.setLayoutParams(params);

        }

    }

    /**
     * Scroll down for a better view when creating an Input Field
     * @param sv
     */
    public void scrollDown(ScrollView sv) {
        sv.fullScroll(View.FOCUS_DOWN);
    }

}