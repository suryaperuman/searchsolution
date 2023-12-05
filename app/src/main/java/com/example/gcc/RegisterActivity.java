package com.example.gcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.gcc.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    interface callBack {
        void canRegister(boolean isAllowed);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView signInLink = findViewById(R.id.signInLink);
        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText usernameField = findViewById(R.id.usernameRegister);
                EditText passwordField = findViewById(R.id.passwordRegister);

                RadioGroup radioGroup = findViewById(R.id.searchBarFilter);
                int checkedBtnId = radioGroup.getCheckedRadioButtonId();
                RadioButton checkedBtn = findViewById(checkedBtnId);
                String checkedRole = checkedBtn.getText().toString();

                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                String role = checkedRole.equals("Participant") ? "user" : "owner";

                Helper helper = new Helper();
                String msg = helper.validateFields(username, password);
                if (!msg.equals("Registration Successful")) {
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }


                registerUser(new callBack() {
                    @Override
                    public void canRegister(boolean isAllowed) {
                        if (isAllowed) {
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                            User newUser;
                            ClubOwner newClubOwner;
                            Intent welcomeActivity = new Intent(RegisterActivity.this, WelcomeActivity.class);
                            if (role.equals("owner")) {
                                Intent clubOwnerMenu = new Intent(RegisterActivity.this, ClubOwnerActivityEvents.class);
                                newClubOwner = new ClubOwner(username, password, role);
                                clubOwnerMenu.putExtra("USER", newClubOwner);
                                startActivity(clubOwnerMenu);
                                finish();
                            } else if (role.equals("user")) {
                                Intent userMenu = new Intent(RegisterActivity.this, UserHomeActivity.class);
                                newUser = new User(password, role, username);
                                userMenu.putExtra("USER", newUser);
                                startActivity(userMenu);
                                finish();
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed, try a new username", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, username, password, role);
            }
        });
    }


    private void registerUser(callBack canUserLogin, String username, String password, String role) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference ref = db.getReference("users");

        DatabaseReference dbRefEmail = ref.child(username);

        dbRefEmail.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();

                    if (snapshot.exists()) {
                        canUserLogin.canRegister(false);

                    } else {
                        Log.d("TAG", "The Document doesn't exist.");
                        canUserLogin.canRegister(true);
                        if (role.equals("owner")){
                            ref.child(username).setValue(new ClubOwner(password, role));
                            DatabaseReference keyGet = FirebaseDatabase.getInstance().getReference("clubs");
                            String uniqueID = keyGet.push().getKey();
                            keyGet.child(uniqueID).child("username").setValue(username);
                        } else if (role.equals("user")){
                            ref.child(username).setValue(new User(password, role));
                        }
                    }
                } else {
                    Log.d("TAG", task.getException().getMessage()); //Never ignore potential errors!
                    canUserLogin.canRegister(false);
                }
            }
        });
    }

}