package com.example.gcc;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class joinedEventsList extends ArrayAdapter<Event> {

    private Activity context;
    List<Event> events;

    User user;

    joinedEventsList(Activity context, List<Event> events, User user){
        super(context, R.layout.layout_user_event_search, events);
        this.context = context;
        this.events = events;
        this.user = user;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View clubListItem = inflater.inflate(R.layout.layout_user_joined_event, null, true);

        TextView EvName = (TextView) clubListItem.findViewById(R.id.textViewLayoutEventListNameJoined);
        TextView EvType = (TextView) clubListItem.findViewById(R.id.textViewLayoutEventListTypeJoined);
        TextView EvDate = (TextView) clubListItem.findViewById(R.id.textViewLayoutEventListStartDateJoined);
        TextView EvLoc = (TextView) clubListItem.findViewById(R.id.textViewLayoutEventListLocationJoined);
        TextView EvPace = (TextView) clubListItem.findViewById(R.id.textViewLayoutEventListPaceJoined);
        TextView EvLevel = (TextView) clubListItem.findViewById(R.id.textViewLayoutEventListLevelJoined);
        Button leaveEv = (Button) clubListItem.findViewById(R.id.leaveEvent);

        Event newEv = events.get(position);

        EvName.setText(newEv.getName());
        EvType.setText(newEv.getType().getName());
        EvDate.setText(newEv.getStartTime());
        EvLoc.setText(newEv.getLocation());
        EvPace.setText(newEv.getPace().toString());
        EvLevel.setText(newEv.getLevel().toString());

        leaveEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveEvent(newEv);
            }
        });

        return  clubListItem;


    }

    private void leaveEvent(Event evLeave){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUsername()).child("joinedevents").child(evLeave.getID());
        DatabaseReference dbClub = FirebaseDatabase.getInstance().getReference("clubs");
        dbRef.removeValue();
        dbClub.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot newSnap : snapshot.getChildren()){
                    if (newSnap.child("events").hasChildren()) {
                        for (DataSnapshot events : newSnap.child("events").getChildren()) {
                            if (events.getKey().toString().equals(evLeave.getID())){
                                dbClub.child(newSnap.getKey()).child("events").child("users").child(user.getUsername()).removeValue();
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
