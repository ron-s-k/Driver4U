package com.example.driver4u;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OutstationFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private SearchView source, destination;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Button setPickup, confirmLocation;
    private ImageButton closeButton;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView txt,rate;
    private Marker sourceMarker;
    private double sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude;
    private Marker destinationMarker;
    private String date;
    private Polyline dottedLine;
    private int selectedHour, selectedMinute;
    private Calendar selectedCalendar;
    private FirebaseFirestore firestore;
    private String email;
    TextView hrs2,hrs4,hrs6,hrs8,hrs10,hrs12;
    String hrsValue;


    private final OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            moveCameraToCurrentLocation();
            requestLocationPermission();
            }

    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_outstation, container, false);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize FireStore
        firestore = FirebaseFirestore.getInstance();
        FirebaseApp.initializeApp(getContext());
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            email = currentUser.getEmail();
            Toast.makeText(getContext(), "Welcome " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();

        }

        // Schedule Dialog Setup
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_schedule);
        dialog.setCancelable(false);

        // Maps Fragment Setup
        source = view.findViewById(R.id.source);
        destination = view.findViewById(R.id.destination);
        confirmLocation = view.findViewById(R.id.confirmLocation);
//        showData = view.findViewById(R.id.showData);

        // Retrieve the SupportMapFragment from the layout using its ID
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        // Source SearchView listener
        source.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleLocationSearch(source, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Destination SearchView listener
        destination.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleLocationSearch(destination, false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Confirm location button click listener
        confirmLocation.setOnClickListener(v -> {
            if (source.getQuery().toString().isEmpty() || destination.getQuery().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please select Source and Destination", Toast.LENGTH_SHORT).show();
            } else {
                // Search for the location first
                handleLocationSearch(source, true);
                handleLocationSearch(destination, false);
                new Handler().postDelayed(() -> {
                    dialog.show();
                }, 1000); // Delay of 1000 milliseconds (1 second)

            }
        });

        // Schedule Dialog Setup
        setPickup = dialog.findViewById(R.id.setPickup);
        datePicker = dialog.findViewById(R.id.datePicker);
        txt = dialog.findViewById(R.id.txt);
        timePicker = dialog.findViewById(R.id.timePicker);
        closeButton = dialog.findViewById(R.id.closeButton);
        hrs2 = dialog.findViewById(R.id.hrs2);
        hrs4 = dialog.findViewById(R.id.hrs4);
        hrs6 = dialog.findViewById(R.id.hrs6);
        hrs8 = dialog.findViewById(R.id.hrs8);
        hrs10 = dialog.findViewById(R.id.hrs10);
        hrs12 = dialog.findViewById(R.id.hrs12);
        rate = dialog.findViewById(R.id.rate);


        hrs2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hrsValue = "2";
                rate.setText("Charges for 2 Hours are Rs.200");
            }
        });
        hrs4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hrsValue = "4";
                rate.setText("Charges for 4 Hours are Rs.400");
            }
        });
        hrs6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hrsValue = "6";
                rate.setText("Charges for 6 Hours are Rs.600");
            }
        });
        hrs8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hrsValue = "8";
                rate.setText("Charges for 8 Hours are Rs.800");
            }
        });
        hrs10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hrsValue = "10";
                rate.setText("Charges for 10 Hours are Rs.1000");
            }
        });
        hrs12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hrsValue = "12";
                rate.setText("Charges for 12 Hours are Rs.1200");
            }
        });


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {dialog.dismiss();            }

        });
        // DatePicker Configuration
        Calendar calendar = Calendar.getInstance();
        long today = calendar.getTimeInMillis();
        datePicker.setMinDate(today);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 7);
        long maxTime = maxDate.getTimeInMillis();
        datePicker.setMaxDate(maxTime);

        // Pickup button listener
//        showData.setOnClickListener(v -> {
//
//            showData();
//
//        });
        setPickup.setOnClickListener(view1 -> handlePickupTime(dialog, calendar));
    }

    private void handleLocationSearch(SearchView searchView, boolean isSource) {
        String location = searchView.getQuery().toString();
        if (location != null) {
            try {
                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> addressList = geocoder.getFromLocationName(location, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    if (isSource) {
                        sourceLatitude = address.getLatitude();
                        sourceLongitude = address.getLongitude();
                        if (sourceMarker != null) sourceMarker.remove();
                        sourceMarker = customIcon(latLng, "Source");
                    } else {
                        destinationLatitude = address.getLatitude();
                        destinationLongitude = address.getLongitude();
                        if (destinationMarker != null) destinationMarker.remove();
                        destinationMarker = customIcon(latLng, "Destination");
                    }
                    drawLineBetweenMarkers();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                } else {
                    Toast.makeText(getActivity(), "Please Enter a Valid Location", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePickupTime(Dialog dialog, Calendar calendar) {
        if (date != null && !date.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Pickup Scheduled");
            builder.setMessage(date);
            builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialogTime = builder.create();
            dialogTime.show();
            txt.setText(date);
        } else {
            selectedHour = timePicker.getCurrentHour();
            selectedMinute = timePicker.getCurrentMinute();

            selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(Calendar.YEAR, datePicker.getYear());
            selectedCalendar.set(Calendar.MONTH, datePicker.getMonth());
            selectedCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
            selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            selectedCalendar.set(Calendar.MINUTE, selectedMinute);

            if (selectedCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) && selectedHour * 60 + selectedMinute < calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE) + 60) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Alert");
                builder.setMessage("Please select a time at least 1 hour from now.");
                builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog dialogTime = builder.create();
                dialogTime.show();
            } else {
                String pickupMessage = "Pickup: " + getMonthName(datePicker.getMonth()) + " " + datePicker.getDayOfMonth() + "   " + String.format("%02d:%02d", selectedHour, selectedMinute);
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Pickup Scheduled");
                builder.setMessage(pickupMessage);
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    dialog.dismiss();
                    addData();
                });
                AlertDialog dialogTime = builder.create();
                dialogTime.show();
                txt.setText(pickupMessage);
            }
        }
    }

    private String getMonthName(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return month >= 0 && month < 12 ? monthNames[month] : "Invalid Month";
    }

    private Marker customIcon(LatLng latLng, String title) {
        return mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker))));
    }

    private void drawLineBetweenMarkers() {
        if (sourceMarker != null && destinationMarker != null) {
            LatLng sourceLatLng = sourceMarker.getPosition();
            LatLng destinationLatLng = destinationMarker.getPosition();
            if (dottedLine != null) {
                dottedLine.remove();
            }
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(sourceLatLng, destinationLatLng)
                    .color(0xFF000000)
                    .width(5)
                    .pattern(java.util.Arrays.asList(new com.google.android.gms.maps.model.Dash(30f), new com.google.android.gms.maps.model.Gap(10)));
            dottedLine = mMap.addPolyline(polylineOptions);
        }
    }

    public void addData() {
        Log.d("TAG", "addData: Method called");

        // Create a map to hold the data
        HashMap<String, Object> tripData = new HashMap<>();

        // Add source and destination addresses to the map
        tripData.put("saddress", source.getQuery().toString());
        tripData.put("daddress", destination.getQuery().toString());

        // Create GeoPoints for source and destination
        GeoPoint sourceGeoPoint = new GeoPoint(sourceLatitude,sourceLongitude );
        GeoPoint destinationGeoPoint = new GeoPoint(destinationLatitude, destinationLongitude);

        // Add the GeoPoints to the map
        tripData.put("source", sourceGeoPoint);
        tripData.put("destination", destinationGeoPoint);

        // Convert the pickup calendar to a human-readable format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date pickupTime = selectedCalendar.getTime();
        String formattedPickupTime = dateFormat.format(pickupTime);
        tripData.put("pickup", formattedPickupTime); // Store as human-readable string
        tripData.put("Trip Type", "Outstation");

        tripData.put("TripTime",hrsValue+"Hours");
        // Optionally, you can include userID or tripID as part of the document ID or data
        String userId = email; // Replace with dynamic user ID if necessary
        String tripId ="trip_" + System.currentTimeMillis(); // Create unique trip ID based on time
        tripData.put("trip_id", tripId); // You can store a trip ID as a unique identifier

        // Save the data to Firestore in the "TRIPS" collection
        firestore.collection("users")
                .document(userId) // Could be dynamic based on the current user
                .collection("Trips Details") // Storing trips for each user in a sub-collection
                .document(tripId) // Unique trip document ID
                .set(tripData, SetOptions.merge()) // Merge to avoid overwriting
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Trip Data Saved", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "onComplete: Data Saved successfully");

                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Log.e("TAG", "onComplete: Error saving data", e);
                        }
                        Toast.makeText(getContext(), "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

//    public void showData(){
//        String userId = "user3"; // Replace with dynamic user ID if necessary
//        firestore.collection("TRIPS").document(userId).collection("userTrips").document("trip_1740820654726")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()){
//                            String saddress = documentSnapshot.getString("saddress");
//                            String daddress = documentSnapshot.getString("daddress");
//                            Long timeStamp = documentSnapshot.getLong("pickup");
//                            String message = "Source Address: " + saddress + "\nDestination Address: " + daddress + "\nPick up Time: " + timeStamp;
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//                            builder.setTitle("Data from Firestore");
//                            builder.setMessage(message);
//                            builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
//                            AlertDialog dialog = builder.create();
//                            dialog.show();
//                        }
//                    }
//                });
//
//
//    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            moveCameraToCurrentLocation();
        }
    }

    private void moveCameraToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    if (sourceMarker != null) {
                        sourceMarker.remove();
                    }
                    sourceMarker = customIcon(currentLatLng, "Current Location");

                    Geocoder geocoder = new Geocoder(requireContext());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (!addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            String currentLocation = address.getAddressLine(0);
                            source.setQuery(currentLocation, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}