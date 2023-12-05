package com.example.gcc;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class EventTypeAdapter extends ArrayAdapter<eventType> {
    private List<eventType> eventTypeList;

    public EventTypeAdapter(Context context, int resource, List<eventType> eventTypeList) {
        super(context, resource);
        this.eventTypeList = eventTypeList;
    }

    @Override
    public int getCount() {
        return eventTypeList.size();
    }

    @Nullable
    @Override
    public eventType getItem(int position) {
        return eventTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) convertView;

        if (textView == null) {
            textView = new TextView(getContext());
            textView.setPadding(16, 16, 16, 16);
        }

        eventType event = eventTypeList.get(position);
        if (event != null) {
            textView.setText(event.getName()); // Display the event name or relevant field
        }

        return textView;
    }
}
