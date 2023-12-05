package com.example.gcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserSearchActivity extends AppCompatActivity {

    User newUser;
    List<Event> eventList;
    List<Club> clubsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        Intent i = getIntent();
        newUser = (User) i.getSerializableExtra("USER");

        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("clubs");

        BottomNavigationView nav = findViewById(R.id.navUser);
        nav.setSelectedItemId(R.id.nav_user_search);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_user_search) {
                return true;
            } else if (item.getItemId() == R.id.nav_user_home) {
                Intent userSearch = new Intent(UserSearchActivity.this, UserHomeActivity.class);
                userSearch.putExtra("USER", newUser);
                startActivity(userSearch);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_user_settings) {
                Intent userSearch = new Intent(UserSearchActivity.this, UserSettingsActivity.class);
                userSearch.putExtra("USER", newUser);
                startActivity(userSearch);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            }
            return false;
        });
        String[] Levels = new String[]{"Clubs","Events"};
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Levels);
        Spinner level = findViewById(R.id.spinnerListFilter);
        level.setAdapter(levelAdapter);
        EditText searchEv = findViewById(R.id.editTextSearch);
        ListView clubEvList = findViewById(R.id.listSearchResult);
        clubsList = new ArrayList<>();
        eventList = new ArrayList<>();


        level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (position==0) {
                            clubsList.clear();
                            for (DataSnapshot clubs : snapshot.getChildren()) {
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(newUser.getUsername()).child("comments");
                                String clubName = clubs.child("clubname").getValue().toString();
                                int clubRating = 0;
                                if (clubs.child("ratings").exists()) {
                                    for (DataSnapshot ratings : clubs.child("ratings").getChildren()) {
                                        if (ratings.child("rating").exists()) {
                                            int rating = Math.toIntExact((Long) ratings.child("rating").getValue());
                                            clubRating += rating;
                                        }
                                    }
                                    if (clubs.child("ratings").child("rating").exists()) {
                                        clubRating = Math.toIntExact((clubRating / (clubs.child("ratings").getChildrenCount())));
                                    }
                                }
                                Club newClub = new Club(clubName, clubRating);
                                newClub.setID(clubs.getKey());
                                clubsList.add(newClub);
                            }
                            ClubList eventAdaptor = new ClubList(UserSearchActivity.this, clubsList, newUser.getUsername());
                            clubEvList.setAdapter(eventAdaptor);
                        } else if (position==1) {
                            eventList.clear();
                            for (DataSnapshot clubs : snapshot.getChildren()) {
                                for(DataSnapshot events : clubs.child("events").getChildren()){
                                    List<User> users = new ArrayList<>();

                                    for (DataSnapshot nestedSnapshot : events.child("users").getChildren()) {
                                        users.add(nestedSnapshot.getValue(User.class));
                                    }
                                    if (events.hasChild("eventname") && events.hasChild("starttime") && events.hasChild("location") && events.hasChild("pace") && events.hasChild("level") && events.hasChild("eventtype")) {
                                        User[] usersArr = users.toArray(new User[0]);
                                        String newevname = events.child("eventname").getValue().toString();
                                        String starttime = events.child("starttime").getValue().toString();
                                        String loc = events.child("location").getValue().toString();
                                        Float newpace = Float.parseFloat(events.child("pace").getValue().toString());
                                        Integer level = Integer.parseInt(events.child("level").getValue().toString());
                                        eventType evtype = events.child("eventtype").getValue(eventType.class);
                                        Event newEvent = new Event(newevname,
                                                evtype,
                                                usersArr,
                                                starttime,
                                                loc,
                                                newpace,
                                                level,
                                                events.getKey());

                                        eventList.add(newEvent);

                                    }
                                }
                            }
                            UserEventList eventAdapter = new UserEventList(UserSearchActivity.this,eventList, newUser);
                            clubEvList.setAdapter(eventAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (level.getSelectedItemPosition()==1) {

                    String searchText = s.toString().toLowerCase();
                    List<Event> filteredEvents = new ArrayList<>();
                    for (Event event : eventList) {
                        if (event.getName().toLowerCase().contains(searchText) || event.getType().getName().toLowerCase().contains(searchText)) {
                            filteredEvents.add(event);
                        }
                    }
                    UserEventList eventAdapter = new UserEventList(UserSearchActivity.this, filteredEvents, newUser);
                    clubEvList.setAdapter(eventAdapter);
                }
                else if(level.getSelectedItemPosition()==0){
                    String searchText = s.toString().toLowerCase();
                    List<Club> filteredClubs = new ArrayList<>();
                    for (Club club : clubsList) {
                        if (club.getName().toLowerCase().contains(searchText)) {
                            filteredClubs.add(club);
                        }
                    }
                    ClubList eventAdapter = new ClubList(UserSearchActivity.this, filteredClubs, newUser.getUsername());
                    clubEvList.setAdapter(eventAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}