package com.example.gcc;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ClubList extends ArrayAdapter<Club> {

    private Activity context;

    List<Club> clubs;
    String UID;

    public ClubList(Activity context, List<Club> clubs, String UID) {
        super(context, R.layout.layout_user_club_search, clubs);
        this.context = context;
        this.clubs = clubs;
        this.UID=UID;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View clubListItem = inflater.inflate(R.layout.layout_user_club_search, null, true);

        TextView clubName = (TextView) clubListItem.findViewById(R.id.textViewClubName);
        TextView clubRating = (TextView) clubListItem.findViewById(R.id.textViewClubRating);

        Club newClub = clubs.get(position);

        clubName.setText(newClub.getName());
        clubRating.setText(newClub.getRating().toString());

        Button addRating = (Button) clubListItem.findViewById(R.id.addRatingBtn);

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRating(newClub);
            }
        });
        return clubListItem;

    }

    private void addRating(Club club){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext()); // Use the context variable
        LayoutInflater inflater = LayoutInflater.from(context); // Obtain LayoutInflater from the context

        final View dialogView = inflater.inflate(R.layout.dialog_add_rating, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        Spinner ratingLevel = dialogView.findViewById(R.id.spinnerRating);
        EditText addComment = dialogView.findViewById(R.id.editTextComment);


        Integer[] Levels = new Integer[]{1, 2, 3, 4, 5};
        ArrayAdapter<Integer> levelAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, Levels);
        ratingLevel.setAdapter(levelAdapter);

        Button confirmComment = dialogView.findViewById(R.id.submitRatingBtn);
        DatabaseReference clubRef = FirebaseDatabase.getInstance().getReference("clubs").child(club.getID()).child("ratings").child(UID);
        confirmComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clubRef.child("comment").setValue(addComment.getText().toString());
                clubRef.child("rating").setValue(ratingLevel.getSelectedItemPosition() + 1);
                alertDialog.dismiss();
            }
        });
    }
}
