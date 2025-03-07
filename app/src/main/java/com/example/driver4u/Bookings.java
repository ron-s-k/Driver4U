package com.example.driver4u;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;  // Remove EdgeToEdge import

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Bookings extends AppCompatActivity {
    private static final String TAG = "Bookings";

    private ListView listView;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);  // Removed EdgeToEdge.enable(this);

        listView = findViewById(R.id.listView);
        firestore = FirebaseFirestore.getInstance();

        fetchTripDetails();
    }

    private void fetchTripDetails() {
        firestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> tripDetails = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get fields with checks
                            String source = document.getString("daddress");
                            String destination = document.getString("saddress");
                            String driver = document.getString("TripType");
                            String pickup = document.getString("pickup");
                            String trip_type = document.getString("Trip Type");
                            String trip_id = document.getString("trip_id");


                            // Log if any field is missing
                            if (pickup == null || destination == null || driver == null) {
                                Log.w(TAG, "Missing fields in document: " + document.getId());
                            }

                            String tripDetail = "Pickup: " + source + ", Destination: " + destination + ", Driver: " + driver + ", Date: " + pickup + ", Trip Type: " + trip_type + ", Trip ID: " + trip_id;
                            tripDetails.add(tripDetail);
                        }

                        // Set the adapter to the ListView only after the data is fetched
                        listView.setAdapter(new ArrayAdapter<>(Bookings.this, android.R.layout.simple_list_item_1, tripDetails));
                        Log.d(TAG, "Trip details fetched: " + tripDetails);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
}
