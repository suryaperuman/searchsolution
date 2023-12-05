package com.example.gcc;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class EventTypeList extends ArrayAdapter<eventType> {

    private Activity context;
    List<eventType> eventTypes;
    public EventTypeList(Activity context, List<eventType> eventTypes) {
        super(context, R.layout.layout_event_type_list, eventTypes);
        this.context = context;
        this.eventTypes = eventTypes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_event_type_list, null, true);

        TextView textViewEventName = (TextView) listViewItem.findViewById(R.id.textViewEventName);
        TextView textViewEventDesc = (TextView) listViewItem.findViewById(R.id.textViewEventDesc);
        TextView textViewEventMinMax = (TextView) listViewItem.findViewById(R.id.textViewEventMinMaxPace);
        TextView textViewEventLevel = (TextView) listViewItem.findViewById(R.id.textViewEventLevel);
        TextView textViewEventAge = (TextView) listViewItem.findViewById(R.id.textViewEventAge);

        eventType newEventType = eventTypes.get(position);
        textViewEventName.setText("test");
        textViewEventName.setText(newEventType.getName());
        textViewEventDesc.setText(newEventType.getDescription());

        textViewEventMinMax.setText(String.format("Pace: %s - %s", newEventType.getPaceMin().toString(), newEventType.getPaceMax().toString()));
        textViewEventLevel.setText(new StringBuilder().append("Level: ").append(newEventType.getLevel()).toString());
        textViewEventAge.setText(new StringBuilder().append("Age: ").append(newEventType.getAge()).toString());

        return listViewItem;
    }
}
