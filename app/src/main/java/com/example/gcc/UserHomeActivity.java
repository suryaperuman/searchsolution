package com.example.gcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UserHomeActivity extends AppCompatActivity {

    User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        Intent i = getIntent();
        newUser = (User) i.getSerializableExtra("USER");

        BottomNavigationView nav = findViewById(R.id.navUser);
        nav.setSelectedItemId(R.id.nav_user_home);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_user_home) {
                return true;
            } else if (item.getItemId() == R.id.nav_user_search) {
                Intent userSearch = new Intent(UserHomeActivity.this, UserSearchActivity.class);
                userSearch.putExtra("USER", newUser);
                startActivity(userSearch);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_user_settings) {
                Intent userSearch = new Intent(UserHomeActivity.this, UserSettingsActivity.class);
                userSearch.putExtra("USER", newUser);
                startActivity(userSearch);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            }
            return false;
        });

        ListView joinedEv = findViewById(R.id.listJoinedEventsView);
        DatabaseReference joinedEvDBREF = FirebaseDatabase.getInstance().getReference("users").child(newUser.getUsername()).child("joinedevents");
        List<Event> events = new ArrayList<>();
        joinedEvDBREF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                events.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()){

                    if (postSnapshot.hasChild("eventname") && postSnapshot.hasChild("starttime") && postSnapshot.hasChild("location") && postSnapshot.hasChild("pace") && postSnapshot.hasChild("level") && postSnapshot.hasChild("eventtype")) {
                        User[] usersArr = new User[0];
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
                joinedEventsList eventAdapter = new joinedEventsList(UserHomeActivity.this,events, newUser);
                joinedEv.setAdapter(eventAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}