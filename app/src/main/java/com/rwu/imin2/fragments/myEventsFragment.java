package com.rwu.imin2.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.rwu.imin2.viewModel.MainActivity;
import com.rwu.imin2.R;
import com.rwu.imin2.adapters.EventsCreatedRecyclerViewAdapter;
import com.rwu.imin2.adapters.EventsInvitedRecyclerViewAdapter;
import com.rwu.imin2.model.Event;
import com.rwu.imin2.model.FirebaseHandler;


import java.util.ArrayList;

public class myEventsFragment extends Fragment {

    //Recyclerview for Created Events
    private RecyclerView RVmyEvents;
    private EventsCreatedRecyclerViewAdapter mMyRVadapter;
    private ArrayList<Event> eventList;

    //Recyclerview for Invited Events
    private RecyclerView RVinvitedEvents;
    private EventsInvitedRecyclerViewAdapter mInvitedRVadapter;

    private FirebaseHandler data;


    public myEventsFragment() {
        // Required empty public constructor
    }

    public static myEventsFragment newInstance(String param1, String param2) {
        myEventsFragment fragment = new myEventsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Handler
        data = new FirebaseHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myevents, container, false);

        // Build Recyclerview for Created Events
        mMyRVadapter = new EventsCreatedRecyclerViewAdapter((ArrayList<Event>) MainActivity.EventList);
        RVmyEvents = (RecyclerView) view.findViewById(R.id.RVmyEventsmyEventsList);
        RVmyEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        RVmyEvents.setAdapter(mMyRVadapter);
        // Fill created Events Recyclerview with Data
        data.getMyCreatedEvents(data.getCurrentUser(), RVmyEvents);

        // Build Recyclerview for Invitation Events
        mInvitedRVadapter = new EventsInvitedRecyclerViewAdapter((ArrayList<Event>) MainActivity.InvitationList);
        RVinvitedEvents = (RecyclerView) view.findViewById(R.id.RVmyEventsInvitedEventsList);
        RVinvitedEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        RVinvitedEvents.setAdapter(mInvitedRVadapter);
        // Fill invited Events Recyclerview with Data
        data.getMyInvitationEvents(data.getCurrentUser(), RVinvitedEvents);

        return view;
    }

}