package com.example.driver4u;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class Bookings extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ListView listView;
    private List<String> trip_id = new ArrayList<>();
    private List<String> sources = new ArrayList<>();
    private List<String> destinations = new ArrayList<>();
    private List<String> trip_type = new ArrayList<>();
    private List<String> trip_time = new ArrayList<>();
    private List<String> pickup = new ArrayList<>();
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        }


        firestore.collection("users").document(userEmail).collection("Trips Details").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                trip_id.clear();
                sources.clear();
                destinations.clear();
                trip_type.clear();
                trip_time.clear();
                pickup.clear();


                for (DocumentSnapshot snapshot : value) {
                    String tripId = snapshot.getString("trip_id");
                    String source = snapshot.getString("saddress");
                    String destination = snapshot.getString("daddress");
                    String tripType = snapshot.getString("Trip Type");
                    String tripTime = snapshot.getString("TripTime");
                    String pickupTime = snapshot.getString("pickup");

                    trip_id.add("Trip id : " + tripId + "\n");
                    sources.add("Source : " + source + "\n");
                    destinations.add("Destination : " + destination + "\n");
                    trip_type.add("Trip Type : " + tripType + "\n");
                    trip_time.add("Trip Time : " + tripTime + "\n");
                    pickup.add("Pickup Time : " + pickupTime + "\n");


                }


                CustomBaseAdapter adapter = new CustomBaseAdapter(getApplicationContext(), trip_id, sources, destinations, trip_type, trip_time, pickup);
                listView.setAdapter(adapter);


            }
        });
    }

}
