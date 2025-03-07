package com.example.driver4u;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomBaseAdapter extends BaseAdapter {

    private final List<String> trip_id;
    private final List<String> sources;
    private final List<String> destinations;
    private final List<String> trip_type;
    private final List<String> trip_time;
    private final List<String> pickup;
    LayoutInflater inflater;

    public CustomBaseAdapter(Context context, List<String> trip_id, List<String> sources, List<String> destinations, List<String> trip_type, List<String> trip_time, List<String> pickup) {
        this.trip_id = trip_id;
        this.sources = sources;
        this.destinations = destinations;
        this.trip_type = trip_type;
        this.trip_time = trip_time;
        this.pickup = pickup;
        inflater = (LayoutInflater.from(context));
    }


    @Override
    public int getCount() {
        return trip_id.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_custom_list_view, null);
        TextView tripId = convertView.findViewById(R.id.trip_id);
        TextView source = convertView.findViewById(R.id.source);
        TextView destination = convertView.findViewById(R.id.destination);
        TextView tripType = convertView.findViewById(R.id.trip_type);
        TextView tripTime = convertView.findViewById(R.id.trip_time);
        TextView pickupTime = convertView.findViewById(R.id.pickup_time);


        tripId.setText(trip_id.get(position));
        source.setText(sources.get(position));
        destination.setText(destinations.get(position));
        tripType.setText(trip_type.get(position));
        tripTime.setText(trip_time.get(position));
        pickupTime.setText(pickup.get(position));

        return convertView;
    }
}