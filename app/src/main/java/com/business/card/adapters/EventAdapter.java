package com.business.card.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.business.card.R;
import com.business.card.objects.Event;

import java.util.List;

public class EventAdapter extends BaseAdapter {

    private Context context;
    private List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    public Event getItem(int position) {
        return events.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the Event for the specified row
        final Event event = getItem(position);
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // inflate the corresponding row layout
            rowView = inflater.inflate(R.layout.event_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.name = (TextView) rowView.findViewById(R.id.name);
            viewHolder.date = (TextView) rowView.findViewById(R.id.date);
            viewHolder.location = (TextView) rowView.findViewById(R.id.location);
            viewHolder.passcode = (TextView) rowView.findViewById(R.id.passcode);

            rowView.setTag(viewHolder);
        }

        // populate the UI elements with the correct information
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.name.setText(event.getName());
        viewHolder.date.setText(event.getDate());
        viewHolder.location.setText(event.getLocation());
        viewHolder.passcode.setText(event.getPasscode());

        return rowView;
    }

    // this class is a holder for UI elements, in order to efficiently reuse them
    static class ViewHolder {
        private TextView name;
        private TextView date;
        private TextView location;
        private TextView passcode;
    }
}
