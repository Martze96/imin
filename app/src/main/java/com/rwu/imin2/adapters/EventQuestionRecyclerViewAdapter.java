package com.rwu.imin2.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rwu.imin2.R;
import com.rwu.imin2.model.Question;
import com.rwu.imin2.model.QuestionList;

import java.util.ArrayList;

public class EventQuestionRecyclerViewAdapter extends RecyclerView.Adapter<EventQuestionRecyclerViewAdapter.ViewHolder> {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final Button deleteButton;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.TVquestionTitle);
            deleteButton = (Button) view.findViewById(R.id.BTNdeleteQuestionButton);

        }
    }

    private QuestionList<Question> questionList;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param InputquestionList QuestionList<Question> containing the data to populate views to be used
     *                          by RecyclerView.
     */
    public EventQuestionRecyclerViewAdapter(QuestionList<Question> InputquestionList) {
        questionList = InputquestionList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventQuestionRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rv_questionlist_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(EventQuestionRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Question q = questionList.get(position);

        TextView textView = holder.textView;
        textView.setText(q.getTitle());
        Button button = holder.deleteButton;
        // premade Question are handled through switches, so make deletebutton gone if premade question
        if(q.getTitle().equals("Ihre 3G Information") || q.getTitle().equals("Werden Sie teilnehmen?")) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No need to delete in Firebase because it overwrites whole questionlist when submitting
                questionList.remove(q);
                notifyDataSetChanged();
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return questionList.size();
    }
}
