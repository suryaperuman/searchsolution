package com.example.gcc;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class EventList extends ArrayAdapter<Event> {

    private Activity context;
    List<Event> events;
    public EventList(Activity context, List<Event> events) {
        super(context, R.layout.layout_event_list, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_event_list, null, true);

        TextView textViewEventName = (TextView) listViewItem.findViewById(R.id.textViewLayoutEventListName);
        TextView textViewEventType = (TextView) listViewItem.findViewById(R.id.textViewLayoutEventListType);
        TextView textViewStartTime = (TextView) listViewItem.findViewById(R.id.textViewLayoutEventListStartTime);
        TextView textViewEventLocation = (TextView) listViewItem.findViewById(R.id.textViewLayoutEventListLocation);
        TextView textViewPace = (TextView) listViewItem.findViewById(R.id.textViewLayoutEventListPace);
        TextView textViewLevel = (TextView) listViewItem.findViewById(R.id.textViewLayoutEventListLevel);

        Button membersSelButton = (Button) listViewItem.findViewById(R.id.clubOwnerListSelectMembers);

        Event newEvent = events.get(position);
        textViewEventName.setText(newEvent.getName());
        textViewEventType.setText(newEvent.getType().getName());
        textViewStartTime.setText(newEvent.getStartTime());
        textViewEventLocation.setText(String.format(newEvent.getLocation()));
        textViewPace.setText(String.format(newEvent.getPace().toString()));
        textViewLevel.setText(String.format(newEvent.getLevel().toString()));




        membersSelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMembersList(newEvent);
            }
        });

        return listViewItem;
    }

    private void showMembersList(Event event){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext()); // Use the context variable
        LayoutInflater inflater = LayoutInflater.from(context); // Obtain LayoutInflater from the context

        final View dialogView = inflater.inflate(R.layout.activity_club_owner_events_participants, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        List<String> Usernames = new ArrayList<>();
        ListView listView = dialogView.findViewById(R.id.listClubOwnerEventMembersView);

        for (User newUser : event.getUsers()){
            Usernames.add(newUser.getUsername());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, Usernames);

        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("clubs").child(ClubOwnerActivityEvents.getUUID()).child("events").child(Usernames.get(position));
                ref.removeValue();
                return false;
            }
        });
    }
}
