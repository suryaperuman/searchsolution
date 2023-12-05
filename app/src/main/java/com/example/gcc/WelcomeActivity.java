package com.example.gcc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;

public class WelcomeActivity extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Intent i = getIntent();
        String newUserRole = (String)i.getSerializableExtra("ROLE");

        Account newUser = null;

        if (newUserRole.equals("owner")){
            ClubOwner temp = (ClubOwner)i.getSerializableExtra("USER");
            newUser = temp;
        }
        else if (newUserRole.equals("user")){
            User temp = (User)i.getSerializableExtra("USER");
            newUser = temp;
        }
        else if (newUserRole.equals("admin")){
            User temp = (User)i.getSerializableExtra("USER");
            newUser = temp;
        }
        TextView userName = (TextView)findViewById(R.id.displayName);
        TextView userRole = (TextView)findViewById(R.id.displayRole);

        userName.setText(newUser.getUsername().toString());
        userRole.setText(newUser.getRole().toString());
    }
}