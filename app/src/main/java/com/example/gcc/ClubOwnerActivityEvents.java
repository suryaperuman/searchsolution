package com.example.gcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClubOwnerActivityEvents extends AppCompatActivity {

    ClubOwner newClubOwner;
    ListView listViewEvents;
    DatabaseReference dbEvents;
    static String UUID;

    public boolean validateEventName(String name) {
        return !TextUtils.isEmpty(name);
    }

    public boolean validatePace(float pace, float min, float max) {
        return pace >= min && pace <= max;
    }

    public boolean validateLevel(int level, int max) {
        return level >= 0 && level <= max;
    }

    public boolean validateDateFormat(String date) {
        Pattern pattern = Pattern.compile("^(0[1-9]|[1-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-\\d{2}$");
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    public boolean validateDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Date date = null;
        long unixTime = 0;

        long currentUnixTime = System.currentTimeMillis() / 1000;
        currentUnixTime = currentUnixTime - (currentUnixTime % 86400); // Remove the seconds from the current time

        try {
            date = dateFormat.parse(dateString);
            unixTime = date.getTime() / 1000; // Convert milliseconds to seconds
        } catch (Exception ignored) {}

        return unixTime >= currentUnixTime;
    }

    public boolean validateLocation(String location) {
        return !TextUtils.isEmpty(location);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_owner_events);
        DatabaseReference keyGet = FirebaseDatabase.getInstance().getReference("clubs");
        List<Event> events = new ArrayList<>();
        listViewEvents = findViewById(R.id.listEventsView);
        keyGet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot keysnapshot : snapshot.getChildren()) {
                    if ((keysnapshot.child("username").getValue().toString()).equals(newClubOwner.getUsername())) {
                        UUID = keysnapshot.getKey().toString();

                        DatabaseReference chngeSettings = keyGet.child(UUID);
                        chngeSettings.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!(snapshot.child("clubname").exists()
                                        && (snapshot.child("clubdesc").exists())
                                        && (snapshot.child("clubnumber").exists())
                                        && (snapshot.child("clubemail").exists())
                                        && (snapshot.child("clubsocial").exists()))) {
                                    Intent clubOwnerSettings = new Intent(ClubOwnerActivityEvents.this, ClubOwnerActivitySettings.class);
                                    clubOwnerSettings.putExtra("USER", newClubOwner);
                                    clubOwnerSettings.putExtra("UUID", UUID);
                                    startActivity(clubOwnerSettings);
                                    finish();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        DatabaseReference evRef = keyGet.child(UUID).child("events");
                        evRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot newsnapshot) {
                                events.clear();
                                for (DataSnapshot postSnapshot : newsnapshot.getChildren()) {
                                    List<User> users = new ArrayList<>();

                                    for (DataSnapshot nestedSnapshot : postSnapshot.child("users").getChildren()) {
                                        users.add(nestedSnapshot.getValue(User.class));
                                    }
                                    if (postSnapshot.hasChild("eventname") && postSnapshot.hasChild("starttime") && postSnapshot.hasChild("location") && postSnapshot.hasChild("pace") && postSnapshot.hasChild("level") && postSnapshot.hasChild("eventtype")) {
                                        User[] usersArr = users.toArray(new User[0]);
                                        String newevname = postSnapshot.child("eventname").getValue().toString();
                                        String starttime = postSnapshot.child("starttime").getValue().toString();
                                        String loc = postSnapshot.child("location").getValue().toString();
                                        Float newpace = Float.parseFloat(postSnapshot.child("pace").getValue().toString());
                                        Integer level = Integer.parseInt(postSnapshot.child("level").getValue().toString());
                                        eventType evtype = postSnapshot.child("eventtype").getValue(eventType.class);
                                        Event newEvent = new Event(newevname,
                                                evtype,
                                                usersArr,
                                                starttime,
                                                loc,
                                                newpace,
                                                level,
                                                postSnapshot.getKey());
                                        events.add(newEvent);
                                    }
                                }
                                EventList eventAdaptor = new EventList(ClubOwnerActivityEvents.this, events);
                                listViewEvents.setAdapter(eventAdaptor);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        Intent i = getIntent();
        newClubOwner = (ClubOwner) i.getSerializableExtra("USER");

        BottomNavigationView nav = findViewById(R.id.navClubOwner);
        nav.setSelectedItemId(R.id.nav_club_owner_events);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_club_owner_events) {
                return true;
            } else if (item.getItemId() == R.id.nav_club_owner_settings) {
                Intent clubOwnerSettings = new Intent(ClubOwnerActivityEvents.this, ClubOwnerActivitySettings.class);
                clubOwnerSettings.putExtra("USER", newClubOwner);
                clubOwnerSettings.putExtra("UUID", UUID);
                startActivity(clubOwnerSettings);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            }
            return false;
        });

        listViewEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event newEvent = events.get(i);
                addEventDialog(false, newEvent);


                return true;

            }
        });


        Button addEvButton = findViewById(R.id.addEventBtn);

        addEvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventDialog(true, null);
            }
        });


    }

    private void addEventDialog(Boolean creating, Event updateEvent) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_event, null);
        dialogBuilder.setView(dialogView);

        Spinner evTypeSpinner = dialogView.findViewById(R.id.SpinnerClubevType);
        DatabaseReference evtype = FirebaseDatabase.getInstance().getReference("eventTypes");
        List<eventType> evTypeNames = new ArrayList<>();

        EventTypeAdapter typeAdapter = new EventTypeAdapter(this, android.R.layout.simple_spinner_dropdown_item, evTypeNames);
        evTypeSpinner.setAdapter(typeAdapter);

        evtype.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                evTypeNames.clear(); // Clear the list before populating it again
                for (DataSnapshot newsnapshot : snapshot.getChildren()) {
                    if (newsnapshot.child("status").getValue().toString().equals("true")) {
                        eventType event = newsnapshot.getValue(eventType.class);
                        evTypeNames.add(event);
                    }
                }
                typeAdapter.notifyDataSetChanged(); // Notify the adapter of changes in the data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });

        //this is the database reference youll be using setvalue to
        final Button addEvent = dialogView.findViewById(R.id.clubOwnerCreateEvent);
        EditText name = dialogView.findViewById(R.id.clubOwnerEditEventname);
        EditText startTime = dialogView.findViewById(R.id.clubOwnerEditDate);
        EditText location = dialogView.findViewById(R.id.clubOwnerEditLocation);
        EditText pace = dialogView.findViewById(R.id.clubOwnerEditPace);
        EditText level = dialogView.findViewById(R.id.clubOwnerEditLevel);
        EditText MaxMembers = dialogView.findViewById(R.id.clubOwnerEditNumMembers);
        TextView typeText = dialogView.findViewById(R.id.textViewClubAddEvType);
        Button deleteEvent = dialogView.findViewById(R.id.clubOwnerDeleteEvent);


        if (creating == false) {

            name.setText(updateEvent.getName());
            startTime.setText(updateEvent.getStartTime());
            location.setText(updateEvent.getLocation());
            pace.setText(updateEvent.getPace().toString());
            level.setText((updateEvent.getLevel().toString()));
            for (int i = 0; i < evTypeNames.size(); i++) {
                if (evTypeNames.get(i).equals(updateEvent.getType())) {
                    evTypeSpinner.setSelection(i);
                    break;
                }
            }
        } else if (creating) {
            deleteEvent.setVisibility(View.INVISIBLE);
        }

        typeText.setText("Type:");

        final AlertDialog b = dialogBuilder.create();
        b.show();

        evTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventType selectedEventType = (eventType) parent.getItemAtPosition(position);
                pace.setHint("Pace (Min: " + selectedEventType.getPaceMin() + " / Max: " + selectedEventType.getPaceMax() + ")");
                level.setHint("Level (0 - " + selectedEventType.getLevel() + ")");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                eventType selectedEventType = (eventType) parent.getItemAtPosition(0);
            }


        });

        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference evdel = FirebaseDatabase.getInstance().getReference("clubs").child(UUID).child("events").child(updateEvent.getID());
                evdel.removeValue();
                b.dismiss();

            }
        });


        addEvent.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                DatabaseReference evadder = FirebaseDatabase.getInstance().getReference("clubs").child(UUID).child("events");
                String eventID = "";
                if (creating == true) {
                    eventID = evadder.push().getKey();
                } else if (!creating) {
                    eventID = updateEvent.getID();
                }


                eventType selectedEventType = (eventType) evTypeSpinner.getSelectedItem();
                Log.d("RAMAN", selectedEventType.getName());

                String eventName = name.getText().toString();
                String locationString = location.getText().toString();
                String paceString = pace.getText().toString();
                String levelString = level.getText().toString();
                String timeString = startTime.getText().toString();
                float paceNum = 0;
                int levelNum = 0;
                int memNum = 0;

                float paceMax = selectedEventType.getPaceMax();
                float paceMin = selectedEventType.getPaceMin();
                int maxLevel = selectedEventType.getLevel();


                if (!validateEventName(eventName)) {
                    Toast.makeText(ClubOwnerActivityEvents.this, "Enter valid event name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validateDateFormat(timeString)) {
                    Toast.makeText(ClubOwnerActivityEvents.this, "Enter date in (DD-MM-YY) format", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (!validateDate(timeString)) {
                    Toast.makeText(ClubOwnerActivityEvents.this, "Date cannot be in past", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validateLocation(locationString)) {
                    Toast.makeText(ClubOwnerActivityEvents.this, "Enter valid location", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    paceNum = Float.parseFloat(paceString);
                } catch (Exception ignored) {
                    Toast.makeText(ClubOwnerActivityEvents.this, "Pace must be a decimal value", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!validatePace(paceNum, paceMin, paceMax)) {
                    Toast.makeText(ClubOwnerActivityEvents.this, String.format("Enter pace between %.2f - %.2f", paceMin, paceMax), Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    levelNum = Integer.parseInt(levelString);
                } catch (Exception ignored) {
                    Toast.makeText(ClubOwnerActivityEvents.this, "Level must be an integer", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    memNum = Integer.parseInt(MaxMembers.getText().toString());
                } catch (Exception ignored) {
                    Toast.makeText(ClubOwnerActivityEvents.this, "Max members must be an integer", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validateLevel(levelNum, maxLevel)) {
                    Toast.makeText(ClubOwnerActivityEvents.this, String.format("Enter level between 0-%d", maxLevel), Toast.LENGTH_SHORT).show();
                    return;
                }

                evadder.child(eventID).child("pace").setValue(paceString);
                evadder.child(eventID).child("level").setValue(levelNum);
                evadder.child(eventID).child("eventname").setValue(eventName);
                evadder.child(eventID).child("starttime").setValue(timeString);
                evadder.child(eventID).child("location").setValue(locationString);
                evadder.child(eventID).child("users").setValue(null);
                evadder.child(eventID).child("eventtype").setValue(selectedEventType);
                evadder.child(eventID).child("eventmaxmem").setValue(memNum);

                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                b.dismiss();
            }
        });
    }

    public static String getUUID() {
        return UUID;
    }
}