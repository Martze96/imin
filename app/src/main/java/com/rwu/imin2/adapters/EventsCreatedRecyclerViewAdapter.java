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

public class EventsCreatedRecyclerViewAdapter extends RecyclerView.Adapter<EventsCreatedRecyclerViewAdapter.ViewHolder> {

    private String eventIdent;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView eventTitle;
        private final TextView eventDate;
        private final TextView eventDesc;
        private final TextView eventId;
        private String eventIdent;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            eventTitle = (TextView) view.findViewById(R.id.TVmyEventmyEventsTitleRow);
            eventDate = (TextView) view.findViewById(R.id.TVmyEventsmyEventsDateRow);
            eventDesc = (TextView) view.findViewById(R.id.TVmyEventsmyEventsDescRow);
            eventId = (TextView) view.findViewById(R.id.TVmyEventsmyEventId);
            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {
            Bundle newBundle = new Bundle();
            eventIdent = eventId.getText().toString();
            newBundle.putString("eventId", this.eventIdent);
            newBundle.putBoolean("invited", false);
            Navigation.findNavController(v).navigate(R.id.action_secondFragment_to_eventDetailFragment, newBundle);
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param inputEventList ArrayList<Event> containing the data to populate views to be used
     *                       by RecyclerView.
     */
    public EventsCreatedRecyclerViewAdapter(ArrayList<Event> inputEventList) {
        MainActivity.EventList = inputEventList;
    }


    @Override
    public EventsCreatedRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rv_myevents_myevents_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(EventsCreatedRecyclerViewAdapter.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Event e = MainActivity.EventList.get(position);
        TextView textView = holder.eventTitle;
        textView.setText(e.getTitle());
        TextView textView1 = holder.eventDate;
        textView1.setText(e.getEventDate());
        TextView textView2 = holder.eventDesc;
        // set max height for good visibility
        textView2.setMaxHeight(100);
        textView2.setText(e.getDescription());
        TextView textView3 = holder.eventId;
        textView3.setText(e.getEventId());
        textView3.setVisibility(View.GONE);
        this.eventIdent = e.getEventId();

    }

    @Override
    public int getItemCount() {
        return MainActivity.EventList.size();
    }
}
