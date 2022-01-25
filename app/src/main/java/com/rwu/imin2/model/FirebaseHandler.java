package com.rwu.imin2.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rwu.imin2.viewModel.MainActivity;
import com.rwu.imin2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FirebaseHandler {

    public FirebaseAuth mAuth;
    public DatabaseReference mDatabaseRootRef;
    private Context mContext;
    private myUser currentUser;
    UserProfileChangeRequest userProfileChangeRequest;
    public DatabaseReference events;
    public Query myEventsQuery;
    public Query myInvitationsQuery;
    public DatabaseReference users;
    public DatabaseReference questions;


    /**
     * FirebaseHandler Constructor
     *
     * @param context - context important for showing f. ex. Toasts as Feedback of Actions
     */
    public FirebaseHandler(Context context) {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabaseRootRef = FirebaseDatabase.getInstance("https://imin-b3bf1-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        this.mContext = context;
        //Create myUser of Firebase user Data
        if (mAuth.getCurrentUser() != null) {
            this.currentUser = new myUser(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getUid());
        }
        this.events = mDatabaseRootRef.child("events");
        this.questions = mDatabaseRootRef.child("events").child("questions");
        this.users = mDatabaseRootRef.child("users");

    }

    /**
     * @param user registers the User into Firebase Auth and DB with Displayname and shows a Toast if success or not.
     */
    public void registerUser(myUser user) {
        Context context = this.mContext;
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // set Displayname
                    userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(user.getDisplayName()).build();
                    mAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
                    //insert Userinfo into Database
                    if (mAuth.getCurrentUser() == null) {
                        System.out.println("NOT DONE CREATING USER!");
                    }
                    mDatabaseRootRef.child("users").child(mAuth.getCurrentUser().getUid()).setValue(user);
                    Toast.makeText(context, "Benutzer erfolgreich registriert", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Fehler bei der Registrierung: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * @param user logs in the user into Firebase Auth with email and password and shows a Toast if success or not.
     *             Also navigates to MainActivity after
     */
    public void loginUser(myUser user) {
        Context context = this.mContext;
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Benutzer erfolgreich eingeloggt", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, MainActivity.class));
                } else {
                    Toast.makeText(context, "Fehler beim einloggen: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * @param event adds a Event and its questionList (one after one) to Firebase DB. shows Toast if success.
     */
    public void addEvent(Event event) {
        Context context = this.mContext;
        // Make normal List<Question> for Firebase
        List<Question> qList = new ArrayList<Question>(event.getQuestionList().getContent());
        // Add Event to Firebase
        mDatabaseRootRef.child("events").child(event.getEventId()).setValue(event);
        // counter to get questions with index
        int counter = 0;
        //loop through standard list<question> and set questions, doesnt work with questionlist bc firebase :)
        for (Object q : qList) {
            mDatabaseRootRef.child("events").child(event.getEventId()).child("questions").child(qList.get(counter).getId()).setValue((Question) q);
            counter++;
        }
        Toast.makeText(context, "Event:  " + event.getTitle() + "  wurde erfolgreich hinzugefügt und User werden eingeladen", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets all Answers of the local Event to the same Event on Firebase (similiar to update Event with Answers)
     *
     * @param event - updated Event with answers
     */
    public synchronized void addAllAnswersToQuestions(Event event) {
        /*
        Tried the CountDownLatch-Method to wait for the completion of the Firebase Operation, seems not to work
         */
        CountDownLatch done = new CountDownLatch(1);
        for (int i = 0; i < event.getQuestionList().size(); i++) {
            mDatabaseRootRef.child("events").child(event.getEventId()).child("questions").child(event.getQuestionList().get(i).getId())
                    .child("answers").child(event.getQuestionList().get(i).getAnswer().getAnswerId()).setValue(event.getQuestionList().get(i).getAnswer());
        }
        done.countDown();
        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the Events created by @param user to the static Eventlist in Mainactivity and optionally updates a recyclerview
     *
     * @param user
     * @param rv
     */
    public void getMyCreatedEvents(myUser user, RecyclerView rv) {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                MainActivity.EventList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Event event = snap.getValue(Event.class);
                        //Eventlist is connected to adapter of eventsCreated
                        MainActivity.EventList.add(event);
                    }
                }
                // Leave rv input null if you dont want a recycleview to update
                if (rv != null) {
                    rv.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("EVENTLIST ERROR: " + error.getMessage());
            }
        };
        // Create Query for specific dataset
        this.myEventsQuery = FirebaseDatabase.getInstance("https://imin-b3bf1-default-rtdb.europe-west1.firebasedatabase.app").getReference("events")
                .orderByChild("creatorUserId")
                .equalTo(mAuth.getCurrentUser().getUid());
        // Add Valuelistener to Query
        myEventsQuery.addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Adds the Events to which @param user is invited to, to the static Invitationlist in Mainactivity and optionally updates a recyclerview
     *
     * @param user
     * @param rv
     */
    public void getMyInvitationEvents(myUser user, RecyclerView rv) {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                MainActivity.InvitationList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        // Get Event Object
                        Event event = snap.getValue(Event.class);
                        // set creatorDisplayname to the Event
                        event.setCreatorDisplayName(snap.child("creatorDisplayName").getValue().toString());
                        // get invitedUsers
                        List<String> invitedUsers = event.getInvitedUsers();
                        // check if invitedusers is null
                        if (invitedUsers != null) {
                            // if user is invited, add the Event to the static List
                            if (invitedUsers.contains(user.getDisplayName())) {
                                MainActivity.InvitationList.add(event);
                            }
                        } else {
                            invitedUsers = new ArrayList<>();
                        }
                    }
                }
                // Leave rv input null if you dont want a recycleview to update
                if (rv != null) {
                    rv.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("INVITATIONLIST ERROR: " + error.getMessage());
            }
        };
        // Create Query
        this.myInvitationsQuery = FirebaseDatabase.getInstance("https://imin-b3bf1-default-rtdb.europe-west1.firebasedatabase.app").getReference("events")
                .orderByChild("invitedUsers");
        // Add Valuelistener to Query
        myInvitationsQuery.addListenerForSingleValueEvent(valueEventListener);

    }

    /**
     * Returns a event in static EventList/InvitationList
     *
     * @param eventId
     * @return
     */
    public Event getEvent(String eventId) {
        boolean found = false;

        for (Event e : MainActivity.EventList) {

            if (e.getEventId().equals(eventId)) {

                e.setQuestionList(new QuestionList<Question>(getQuestionsFromEvent(e.getEventId(), null)));

                found = true;
                return e;
            } else {
                System.out.println("NO EVENT FOUND");
            }
        }

        if (!found) {
            for (Event e : MainActivity.InvitationList) {
                if (e.getEventId().equals(eventId)) {
                    found = true;
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * @param eventId the event id of the questions you want
     * @param newText optional, can be nulled. If you want the titles of the Questions set on a textview, insert textview
     * @return updates the static Questionlist, returns it and (optionally) updates the textview with the titles of the question.
     */
    public List<Question> getQuestionsFromEvent(String eventId, TextView newText) {
        DatabaseReference quezt = FirebaseDatabase.getInstance("https://imin-b3bf1-default-rtdb.europe-west1.firebasedatabase.app").getReference("events").child(eventId).child("questions");
        //Create Query
        Query qQuery = FirebaseDatabase.getInstance("https://imin-b3bf1-default-rtdb.europe-west1.firebasedatabase.app").getReference("events").orderByChild("eventId").equalTo(eventId);
        // Add and define Valuelistener
        qQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MainActivity.QuestionList.clear();
                //Null handler for Textview input (optional Textview)
                if (newText != null) {
                    newText.setText("");
                }
                // iterate over snapshot
                for (DataSnapshot dz : snapshot.getChildren()) {
                    // set snapshot to questions
                    DataSnapshot dy = dz.child("questions");
                    //iterate over questions snapshot
                    for (DataSnapshot snap : dy.getChildren()) {
                        // get Question Object
                        Question q = snap.getValue(Question.class);
                        // set Textview textcontent of input
                        if (newText != null) {
                            newText.setText(newText.getText().toString() + q.getTitle() + ", ");
                        }
                        // add the Question to the static Questionlist
                        MainActivity.QuestionList.add(snap.getValue(Question.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return MainActivity.QuestionList;
    }

    /**
     * Handles Deletion of an Event
     *
     * @param eventId Event which should be deleted
     * @param context If you want to make a Toast, otherwise leave null
     * @param v       View, if you want to navigate to myEvents afterwards
     */
    public void deleteEvent(String eventId, Context context, View v) {

        DatabaseReference ref = FirebaseDatabase.getInstance("https://imin-b3bf1-default-rtdb.europe-west1.firebasedatabase.app").getReference("events");
        Query query = FirebaseDatabase.getInstance("https://imin-b3bf1-default-rtdb.europe-west1.firebasedatabase.app").getReference("events").orderByChild("eventId").equalTo(eventId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            //boolean to know if something was deleted
            boolean wasDeleted = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // iterate over events
                for (DataSnapshot child : snapshot.getChildren()) {
                    // get Event Object
                    Event e = child.getValue(Event.class);
                    // remove Event if contains given id
                    if (e.getEventId().equals(eventId)) {
                        //remove from firebase
                        ref.child(eventId).removeValue();
                        //remove locally
                        MainActivity.EventList.remove(e);
                        // make Toast if context was given
                        if (context != null) {
                            Toast.makeText(context, "Event wurde gelöscht!", Toast.LENGTH_SHORT).show();
                        }
                        // set boolean true
                        wasDeleted = true;
                        //navigate to my event if view was given
                        if (v != null) {
                            Navigation.findNavController(v).navigate(R.id.action_eventDetailFragment_to_secondFragment);
                        }

                    }
                }
                // Make Toast if no event with that id existed
                if (!wasDeleted && context != null) {
                    Toast.makeText(context, "Kein Event gefunden oder etwas ist schiefgelaufen :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Returns a list of Answers of a Question from a Event
     *
     * @param event
     * @param question
     * @return the static List of Answers
     */
    public ArrayList<Answer> getAnswersToQuestion(Event event, Question question) {

        // Reference to Answers
        DatabaseReference qRef = mDatabaseRootRef.child("events").child(event.getEventId()).child("questions").child(question.getId()).child("answers");
        Query qQuery = FirebaseDatabase.getInstance("https://imin-b3bf1-default-rtdb.europe-west1.firebasedatabase.app").getReference("events").child(event.getEventId()).child("questions").child(question.getId()).child("answers").orderByChild("answerId");

        qRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // iterate through answers
                for (DataSnapshot snap1 : snapshot.getChildren()) {
                    // null handler
                    if (snap1 == null) {
                        continue;
                    }
                    // get object
                    Answer a = snap1.getValue(Answer.class);
                    // add to static Answerlist
                    MainActivity.AnswerList.add(a);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return (ArrayList<Answer>) MainActivity.AnswerList;
    }

    /**
     * @param event
     * @param linearLayout - can be null, this is to update a Layout and fill it with the Answers, bc Firebase Queries seem to be handled asnychronous in Java
     * @return
     */
    public void getAnswersToEvent(Event event, LinearLayout linearLayout, int index) {

        List<Answer> answerList = new ArrayList<Answer>(MainActivity.AnswerList);

        // get Reference
        DatabaseReference aRef = mDatabaseRootRef.child("events").child(event.getEventId()).child("questions").child(event.getQuestionList().get(index).getId()).child("answers");
        // Loop Answers in Question and make Views for the answers
        aRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // iterate over answers
                for (DataSnapshot snap1 : snapshot.getChildren()) {
                    if (snap1 == null) {
                        continue;
                    }
                    //get Answer Object
                    Answer a = snap1.getValue(Answer.class);
                    /**
                     * The reason i define the frontend changes here, is that firebase queries are asynchronous.
                     * I tried with several approaches (countdownLatch, singleThread...) to wait for this function to be complete
                     * before android builds the views, without success. though it adds the answers to the static list and you can leave
                     * the linear layout and the index null if you dont need it
                     */
                    if (a != null && linearLayout != null) {
                        // create Views
                        TextView answerName = new TextView(linearLayout.getContext());
                        TextView answers = new TextView(linearLayout.getContext());
                        TextView additionalInfo = new TextView(linearLayout.getContext());

                        // Set content
                        answerName.setText(a.getUserDisplayName()+":");
                        answerName.setTextColor(ContextCompat.getColor(answerName.getContext(), R.color.primaryColor));
                        answerName.setTypeface(null, Typeface.BOLD);
                        String answerItem = "";
                        if (a.getAnswerList() == null) {
                            answers.setText("Keine Antwort abgegeben.");
                        } else {
                            for (int i = 0; i < a.getAnswerList().size(); i++) {
                                if (a.getAnswerList().size() == 1) {
                                    answerItem = a.getAnswerList().get(0);
                                } else if (i == a.getAnswerList().size() - 1) {
                                    answerItem += a.getAnswerList().get(i);
                                } else {
                                    answerItem += a.getAnswerList().get(i) + ", ";
                                }
                            }
                            answers.setText(answerItem);
                        }
                        if(!a.getAdditionalInfo().equals("")) {
                            additionalInfo.setText("Zusätzliche Information: " + a.getAdditionalInfo());
                            additionalInfo.setTypeface(null, Typeface.ITALIC);
                        } else {
                            additionalInfo.setText(a.getAdditionalInfo());
                        }


                        //Set Layout Params
                        answerName.setLayoutParams(new
                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        answers.setLayoutParams(new
                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        additionalInfo.setLayoutParams(new
                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));

                        // set margin if additional info is not empty
                        if(!additionalInfo.getText().toString().equals("")) {
                            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) additionalInfo.getLayoutParams();
                            lp.setMargins(0,5,0,20);
                            additionalInfo.setLayoutParams(lp);
                        }
                        // Add to Layout
                        linearLayout.addView(answerName);
                        linearLayout.addView(answers);
                        linearLayout.addView(additionalInfo);
                    }
                    // Add answer to static List
                    MainActivity.AnswerList.add(a);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        MainActivity.AnswerList = answerList;
    }

    /**
     * Deletes the Answers from Firebase
     *
     * @param event
     * @param question
     */
    public synchronized void deleteAnswer(Event event, Question question) {
        // Get Reference
        DatabaseReference qRef = mDatabaseRootRef.child("events").child(event.getEventId())
                .child("questions").child(question.getId()).child("answers");
        // Create Query
        Query qQuery = FirebaseDatabase.getInstance("https://imin-b3bf1-default-rtdb.europe-west1.firebasedatabase.app").getReference("events").child(event.getEventId())
                .child("questions").child(question.getId()).child("answers").orderByChild("answerId");
        // Add Value Listener
        qQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Iterate over answer snapshot
                for (DataSnapshot snap : snapshot.getChildren()) {
                    // get Answer Object
                    Answer a = snap.getValue(Answer.class);
                    // remove Answer if the Answer is from the current User
                    if (a.getUserDisplayName().equals(mAuth.getCurrentUser().getDisplayName())) {
                        Log.d("REMOVED ANSWER ID: ", a.getAnswerId().toString());
                        qRef.child(a.getAnswerId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public myUser getCurrentUser() {
        return currentUser;
    }

    public void signOutCurrentUser() {
        mAuth.signOut();
    }
}
