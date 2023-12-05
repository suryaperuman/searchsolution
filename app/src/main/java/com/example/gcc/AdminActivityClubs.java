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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class AdminActivityClubs extends AppCompatActivity {
    DatabaseReference dbEventTypes;

    ListView listViewEventTypes;

    interface eventTypeCB {
        void canAddEventType(boolean isAllowed);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_clubs);

        BottomNavigationView nav = findViewById(R.id.nav);
        nav.setSelectedItemId(R.id.nav_clubs);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_clubs) {
                return true;
            } else if (item.getItemId() == R.id.nav_users) {
                startActivity(new Intent(getApplicationContext(), AdminActivityUsers.class));
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

        dbEventTypes = FirebaseDatabase.getInstance().getReference("eventTypes");
        listViewEventTypes = findViewById(R.id.listClubOwnerEventMembersView);
        List<eventType> eventTypes = new ArrayList<>();
        dbEventTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventTypes.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    eventType newEventType = new eventType(postSnapshot.getKey().toString(),postSnapshot.child("description").getValue().toString(),Integer.valueOf(String.valueOf(postSnapshot.child("level").getValue())),Float.parseFloat(postSnapshot.child("paceMin").getValue().toString()) ,Float.parseFloat(postSnapshot.child("paceMax").getValue().toString()),Integer.valueOf(postSnapshot.child("age").getValue().toString()), Boolean.valueOf(postSnapshot.child("status").getValue().toString()));
                    eventTypes.add(newEventType);
                    Log.d("TAG",eventTypes.toString());

                }
                EventTypeList eventTypeAdaptor = new EventTypeList(AdminActivityClubs.this, eventTypes);
                listViewEventTypes.setAdapter(eventTypeAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Spinner level = findViewById(R.id.spinnerLevel);
        Integer[] Items = new Integer[]{1, 2, 3, 4, 5};

        ArrayAdapter<Integer> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Items);
        level.setAdapter(levelAdapter);
        Button addEventTypeBtn = findViewById(R.id.addEventTypeBtn);

        addEventTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText eventTypeNameText = findViewById(R.id.editTextEventTypeName);
                EditText eventTypeDescText = findViewById(R.id.editTextEventTypeDesc);
                EditText eventTypeMinPaceText = findViewById(R.id.editTextMinPace);
                EditText eventTypeMaxPaceText = findViewById(R.id.editTextPaceMax);
                EditText eventTypeAge = findViewById(R.id.editTextAge);

                Integer levelInt = level.getSelectedItemPosition() + 1;
                String eventTypeName = eventTypeNameText.getText().toString();
                String eventTypeDesc = eventTypeDescText.getText().toString();
                Float eventTypeMinPace = 0.0f;
                Float eventTypeMaxPace = 0.0f;
                Integer ageInt = 0;
                //make this apart of the helper func maybe?
                if ((eventTypeMinPaceText.getText().toString().matches("^\\d+(\\.\\d+)?")) && (eventTypeMaxPaceText.getText().toString().matches("^\\d+(\\.\\d+)?"))) {
                    eventTypeMinPace = Float.parseFloat(eventTypeMinPaceText.getText().toString());
                    eventTypeMaxPace = Float.parseFloat(eventTypeMaxPaceText.getText().toString());
                } else {
                    Toast.makeText(AdminActivityClubs.this, "Invalid pace values", Toast.LENGTH_SHORT).show();
                }
                if (eventTypeAge.getText().toString().matches("^\\d+(\\.\\d+)?")) {
                    ageInt = Integer.parseInt(eventTypeAge.getText().toString());
                }
                canAddEventType(new eventTypeCB() {
                    @Override
                    public void canAddEventType(boolean isAllowed) {
                        Toast.makeText(AdminActivityClubs.this, "Added event type", Toast.LENGTH_SHORT).show();
                    }
                }, eventTypeName, eventTypeDesc, eventTypeMinPace, eventTypeMaxPace, levelInt, ageInt);
            }
        });
        listViewEventTypes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                eventType newEventType = eventTypes.get(i);
                showUpdateDeleteDialog(newEventType.getName(),newEventType.getDescription(),newEventType.getPaceMin(),newEventType.getPaceMax(),newEventType.getAge(),newEventType.getLevel(),newEventType.getStatus());
                return true;
            }
        });
    }

    private void canAddEventType(eventTypeCB canAddType, String Name, String Desc, Float minPace, Float maxPace, Integer level, Integer age) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference ref = db.getReference("eventTypes");
        DatabaseReference name = ref.child(Name);

        name.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();

                    if (snapshot.exists()) {
                        Toast.makeText(AdminActivityClubs.this, "Event type already exists", Toast.LENGTH_SHORT).show();
                        canAddType.canAddEventType(false);

                    } else {
                        Log.d("TAG", "The Document doesn't exist.");
                        canAddType.canAddEventType(true);
                        eventType newEventType = new eventType(Name, Desc, level, minPace, maxPace, age, true);
                        ref.child(Name).setValue(newEventType);
                    }
                } else {
                    Log.d("TAG", task.getException().getMessage()); //Never ignore potential errors!
                    canAddType.canAddEventType(false);
                }
            }
        });
    }

    private void showUpdateDeleteDialog(final String eventName,String eventDesc,float minPace,float maxPace,int Age,int Level, Boolean status) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_update_event_type, null);
        dialogBuilder.setView(dialogView);

        String[] Roles = new String[]{"Available","Hidden"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Roles);
        Spinner eventStatus = dialogView.findViewById(R.id.spinnerAdminChangeStatus);
        eventStatus.setAdapter(roleAdapter);

        Integer[] Items = new Integer[]{1, 2, 3, 4, 5};
        ArrayAdapter<Integer> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Items);
        Spinner level = dialogView.findViewById(R.id.spinnerAdminChangeEventLevel);
        level.setAdapter(levelAdapter);

        final Button buttonUpdateAdminEventTypeBtn = (Button) dialogView.findViewById(R.id.buttonUpdateAdminEventType);
        final EditText eventNameText = (EditText) dialogView.findViewById(R.id.editTextAdminChangeEventName);
        final EditText eventDescText = (EditText) dialogView.findViewById(R.id.editTextAdminChangeDesc);
        final EditText eventMinPaceText = (EditText) dialogView.findViewById(R.id.editTextAdminChangeMinPace);
        final EditText eventMaxPaceText = (EditText) dialogView.findViewById(R.id.editTextAdminChangeMaxPace);
        final EditText eventAgeText = (EditText) dialogView.findViewById(R.id.editTextAdminChangeAge);
        final Button buttonDeleteAdminEventTypeBtn = (Button) dialogView.findViewById(R.id.buttonDeleteAdminEventType);

        eventNameText.setText(eventName);
        eventDescText.setText(eventDesc);
        eventMinPaceText.setText(String.valueOf(minPace));
        eventMaxPaceText.setText(String.valueOf(maxPace));
        eventAgeText.setText(String.valueOf(Age));
        level.setSelection((Level-1));

        String oldName = eventName;
        if (status.equals(true)){
            eventStatus.setSelection(0);
        } else {
            eventStatus.setSelection(1);
        }

        dialogBuilder.setTitle(eventName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdateAdminEventTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String neweventName = "";
                String neweventDesc = "";
                float newminPace = 0;
                float newmaxPace = 0;
                Integer newAge = null;
                Integer newLevel = null;
                Boolean newStatus = null;
                try {
                    neweventName = eventNameText.getText().toString().trim();
                    neweventDesc = eventDescText.getText().toString();
                    if (Float.parseFloat(eventMinPaceText.getText().toString())<(Float.parseFloat(eventMaxPaceText.getText().toString()))) {
                        newminPace = Float.parseFloat(eventMinPaceText.getText().toString());
                        newmaxPace = Float.parseFloat(eventMaxPaceText.getText().toString());
                    }else {
                        newmaxPace = Float.parseFloat(eventMinPaceText.getText().toString());
                        newminPace = Float.parseFloat(eventMaxPaceText.getText().toString());
                    }
                    newAge = Integer.parseInt(eventAgeText.getText().toString());
                    newLevel = level.getSelectedItemPosition()+1;
                    if (eventStatus.getSelectedItemPosition()==0){
                        newStatus = true;
                    }
                    else {
                        newStatus = false;
                    }

                }
                catch (Exception e) {
                    Log.d("TAG", "BAD");
                }
                if (!(TextUtils.isEmpty(neweventName) && TextUtils.isEmpty(neweventDesc) && Float.isNaN(newminPace) && Float.isNaN(newmaxPace) && newAge==null && newLevel==null)) {
                    updateEventType(neweventName,neweventDesc, newminPace, newmaxPace,newAge,newLevel,newStatus, oldName);
                    b.dismiss();
                }
            }
        });
        buttonDeleteAdminEventTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = oldName;
                deleteEventType(name);
                b.dismiss();
            }
        });


    }
    private void updateEventType(String eventName,String eventDesc,float minPace,float maxPace,int Age,int Level, Boolean status, String oldName){
        deleteEventType(oldName);
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference ("eventTypes").child(eventName);

        eventType newType = new eventType(eventDesc,Level,minPace,maxPace,Age,status);
        dR.setValue(newType);
        Toast.makeText(getApplicationContext(), "eventType Updated", Toast.LENGTH_LONG).show();
    }

    private void deleteEventType(String eventName){
        Log.d("TAG",eventName);
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference ("eventTypes").child(eventName);
        dR.removeValue();
    }


}
