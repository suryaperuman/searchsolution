package com.example.gcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gcc.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivityUsers extends AppCompatActivity {

    interface callBack {
        void canRegister(boolean isAllowed);
    }

    DatabaseReference dbUsers;

    ListView listViewAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        BottomNavigationView nav = findViewById(R.id.nav);
        nav.setSelectedItemId(R.id.nav_users);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_users) {
                return true;
            } else if (item.getItemId() == R.id.nav_clubs) {
                startActivity(new Intent(getApplicationContext(), AdminActivityClubs.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), AdminActivitySettings.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            }
            return false;
        });

        dbUsers = FirebaseDatabase.getInstance().getReference("users");

        listViewAccounts = findViewById(R.id.listUsersView);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.layout_account_list_header, listViewAccounts, false);
        listViewAccounts.addHeaderView(header, null, false);

        List<Account> Accounts = new ArrayList<>();
        Button addUserBtn = findViewById(R.id.addUserBtn);

        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Accounts.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User account = new User(postSnapshot.child("password").getValue().toString(),postSnapshot.child("role").getValue().toString(),postSnapshot.getKey().toString());
                    //Log.d("TAG",account.getUsername());
                    Accounts.add(account);

                }
                AccountList accountAdaptor = new AccountList(AdminActivityUsers.this, Accounts);

                listViewAccounts.setAdapter(accountAdaptor);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText usernameField = findViewById(R.id.editTextUsername);
                EditText passwordField = findViewById(R.id.editTextPassword);

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
                    Toast.makeText(AdminActivityUsers.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                registerUser(new callBack() {
                    @Override
                    public void canRegister(boolean isAllowed) {
                        if (isAllowed) {
                            Toast.makeText(AdminActivityUsers.this, "Registration Successful", Toast.LENGTH_LONG).show();
                            User newUser;
                            ClubOwner newClubOwner;
                            if (role.equals("owner")) {
                                newClubOwner = new ClubOwner(username, password, role);
                            } else if (role.equals("user")) {
                                newUser = new User(password, role, username);
                            }
                        } else {
                            Toast.makeText(AdminActivityUsers.this, "Registration Failed, try a new username", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, username, password, role);

                usernameField.setText("");
                passwordField.setText("");
            }
        });
        listViewAccounts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Account account = Accounts.get(i - 1);
                showUpdateDeleteDialog(account.getUsername(), account.getPassword(),account.getRole());
                return true;
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


    private void showUpdateDeleteDialog(final String userName,String userpwd,String userRole) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_update_account, null);
        dialogBuilder.setView(dialogView);

        String[] Roles = new String[]{"User","Owner"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Roles);
        Spinner role = dialogView.findViewById(R.id.spinnerUpdateRole);
        role.setAdapter(roleAdapter);

        final EditText editTextUsername = (EditText) dialogView.findViewById(R.id.editTextAdminChangeName);
        final EditText editTextUserpwd  = (EditText) dialogView.findViewById(R.id.editTextAdminChangeDesc);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateAdminEventType);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteAdminEventType);

        editTextUsername.setText(userName);

        String oldName = userName;
        editTextUserpwd.setText(userpwd);
        if (userRole.equals("user")){
            role.setSelection(0);
        } else {
            role.setSelection(1);
        }

        dialogBuilder.setTitle("User");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextUsername.getText().toString().trim();
                String pwd = (editTextUserpwd.getText().toString());
                String roleStr = role.getSelectedItem().toString();
                if (!(TextUtils.isEmpty(name) && TextUtils.isEmpty(pwd))) {
                    updateUsername(oldName,name, pwd, roleStr);
                    b.dismiss();
                }
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextUsername.getText().toString().trim();
                deleteUser(name);
                b.dismiss();
            }
        });


    }
    private void updateUsername(String oldName, String userName,String userPwd,String userRole){
        deleteUser(oldName);
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(userName);
        Account acc;
        if (userRole.equals("user")){
            acc = new User(userPwd,userRole);
        }
        else {
            acc = new ClubOwner(userPwd,userRole);
            DatabaseReference keyGet = FirebaseDatabase.getInstance().getReference("clubs");
            keyGet.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot keysnapshot : snapshot.getChildren()) {
                        if (keysnapshot.child("username").getValue().toString().equals(oldName)){
                            String UUID = keysnapshot.getKey().toString();
                            keyGet.child(UUID).child("username").setValue(userName);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        dR.setValue(acc);
        Toast.makeText(getApplicationContext(), "User Updated", Toast.LENGTH_LONG).show();
    }
    private void deleteUser(String name){
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference ("users").child(name);
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "User Deleted", Toast.LENGTH_LONG).show();
    }
}