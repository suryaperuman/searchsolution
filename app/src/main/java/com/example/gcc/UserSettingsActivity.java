package com.example.gcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSettingsActivity extends AppCompatActivity {
    User newUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        Intent i = getIntent();
        newUser = (User) i.getSerializableExtra("USER");

        DatabaseReference userSettings = FirebaseDatabase.getInstance().getReference("users").child(newUser.getUsername());

        BottomNavigationView nav = findViewById(R.id.navUser);
        nav.setSelectedItemId(R.id.nav_user_settings);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_user_settings) {
                return true;
            } else if (item.getItemId() == R.id.nav_user_home) {
                Intent userSearch = new Intent(UserSettingsActivity.this, UserHomeActivity.class);
                userSearch.putExtra("USER", newUser);
                startActivity(userSearch);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_user_search) {
                Intent userSearch = new Intent(UserSettingsActivity.this, UserSearchActivity.class);
                userSearch.putExtra("USER", newUser);
                startActivity(userSearch);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            }
            return false;
        });

        Integer[] Levels = new Integer[]{1, 2, 3, 4, 5};
        ArrayAdapter<Integer> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Levels);
        Spinner level = findViewById(R.id.spinnerUserLevel);
        level.setAdapter(levelAdapter);

        EditText userPwd = findViewById(R.id.userPassword);
        EditText userPace = findViewById(R.id.userPace);
        EditText userAge = findViewById(R.id.userAge);

        userSettings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("password").exists()){
                    userPwd.setText(snapshot.child("password").getValue().toString());
                }
                if (snapshot.child("idealpace").exists()){
                    userPace.setText(snapshot.child("idealpace").getValue().toString());
                }
                if (snapshot.child("userAge").exists()){
                    userAge.setText(snapshot.child("userAge").getValue().toString());
                }
                if (snapshot.child("userLevel").exists()){
                    level.setSelection(Integer.valueOf(snapshot.child("userAge").getValue().toString())-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button chngeSettings = findViewById(R.id.changeUserSettingsBtn);

        chngeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSettings.child("password").setValue(userPwd.getText().toString());
                userSettings.child("idealpace").setValue(Float.parseFloat(userPace.getText().toString()));
                userSettings.child("userAge").setValue(Integer.valueOf(userAge.getText().toString()));
                userSettings.child("userLevel").setValue(Integer.valueOf(level.getSelectedItemPosition()+1));
            }
        });


    }
}