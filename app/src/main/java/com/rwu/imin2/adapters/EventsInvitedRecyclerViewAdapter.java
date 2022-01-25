package com.rwu.imin2.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.rwu.imin2.viewModel.MainActivity;
import com.rwu.imin2.R;
import com.rwu.imin2.model.Event;

import java.util.ArrayList;


public class EventsInvitedRecyclerViewAdapter extends RecyclerView.Adapter<EventsInvitedRecyclerViewAdapter.ViewHolder>  {
    private String eventIdent;



    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView eventTitle;
        private final TextView eventDate;
        private final TextView eventDesc;
        private final TextView eventId;
        private final TextView eventCreatorName;
        private  String eventIdent;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            eventId = (TextView) view.findViewById(R.id.TVmyEventsInvitedEventsEventId);
            eventTitle = (TextView) view.findViewById(R.id.TVmyEventInvitedEventsTitle);
            eventDate = (TextView) view.findViewById(R.id.TVmyEventsInvitedEventsDate);
            eventDesc = (TextView)  view.findViewById(R.id.TVmyEventsInvitedEventsDesc);
            eventCreatorName = (TextView) view.findViewById(R.id.TVEventInvitedEventCreatorDisplayName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Bundle newBundle = new Bundle();
            eventIdent = eventId.getText().toString();
            newBundle.putString("eventId", this.eventIdent);
            newBundle.putBoolean("invited", true);
            Navigation.findNavController(v).navigate(R.id.action_secondFragment_to_eventDetailFragment, newBundle);
        }

    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param inputEventList ArrayList<Event> containing the data to populate views to be used
     * by RecyclerView.
     */
    public EventsInvitedRecyclerViewAdapter(ArrayList<Event> inputEventList) {
        MainActivity.InvitationList = inputEventList;
    }


    @Override
    public EventsInvitedRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rv_myevents_invitedevents_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(EventsInvitedRecyclerViewAdapter.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Event e = MainActivity.InvitationList.get(position);

            TextView textView = holder.eventTitle;
            textView.setText(e.getTitle());
            TextView textView1 = holder.eventDate;
            textView1.setText(e.getEventDate());
            TextView textView2 = holder.eventDesc;
            textView2.setText(e.getDescription());
            // set max height for good visibility
            textView2.setMaxHeight(100);
            TextView textview3 = holder.eventId;
            textview3.setText(e.getEventId());
            textview3.setVisibility(View.GONE);
            this.eventIdent = e.getEventId();
            TextView textView4 = holder.eventCreatorName;
            textView4.setText("Von: " + e.getCreatorDisplayName());



    }

    @Override
    public int getItemCount() {
        return MainActivity.InvitationList.size();
    }
}
