package com.example.driver4u;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Calendar;
import java.util.List;

public class OnewayFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private SearchView source,destination;
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Button setPickup,confirmLocation;
    DatePicker datePicker;
    TimePicker timePicker;
    TextView txt;
    Marker sourceMarker;
    Marker destinationMarker;
    String date;
    Polyline dottedLine;

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
        return inflater.inflate(R.layout.fragment_oneway, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Schedule Dialog
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_schedule);
        dialog.setCancelable(false);


        // Maps Fragment
        source = view.findViewById(R.id.source);
        destination = view.findViewById(R.id.destination);
        confirmLocation = view.findViewById(R.id.confirmLocation);

        // Retrieve the SupportMapFragment from the layout using its ID
        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            // Asynchronously get the GoogleMap instance and pass it to the callback
            mapFragment.getMapAsync(callback);
        }

        // Set a listener for query text submission in the source SearchView
        source.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = source.getQuery().toString();
                List<Address> addresslist = null;
                // check if the location is not empty
                if (location != null) {
                    // creating a geocoder instance
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        // get the address from the location name with limit of 1.
                        addresslist = geocoder.getFromLocationName(location, 1);
                    } catch (Exception e){
                        // if there is any exception will be print
                        e.printStackTrace();
                    }
                    // check if the addresslist is not empty or null
                    if (addresslist != null && !addresslist.isEmpty()) {
                        // get the first address from the list
                        Address address = addresslist.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        // adding the marker on map and moving camera to it.
                        if (sourceMarker != null) {
                            sourceMarker.remove();
                        }
                        sourceMarker = customIcon(latLng, "Source");
                        drawLineBetweenMarkers();

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Start Location", Toast.LENGTH_SHORT).show();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        destination.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = destination.getQuery().toString();
                // List to store address details obtained from geocoding
                List<Address> addresslist =null;

                // Check if a location (destination) has been entered
                if(location != null){
                    // Create a Geocoder object to convert location names into addresses
                    Geocoder geocoder = new Geocoder(getActivity());

                    try{
                        // Retrieve a list of addresses based on the provided location name, limited to 1 result
                        addresslist = geocoder.getFromLocationName(location, 1);
                    } catch (Exception e){
                        // If an error occurs during geocoding, print the stack trace for debugging
                        e.printStackTrace();
                    }
                    // Check if addresslist is null or empty after geocoding
                    if (addresslist != null && !addresslist.isEmpty()) {
                        // Get the first address from the list of addresses
                        Address address = addresslist.get(0);
                            // Create a LatLng object using the obtained latitude and longitude
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            // adding marker on map with the title location
                            if (destinationMarker != null) {
                                destinationMarker.remove();
                            }
                            destinationMarker = customIcon(latLng, "Destination");
                            drawLineBetweenMarkers();
                            // create a LatLngBounds.Builder to include both markers
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(sourceMarker.getPosition());
                            builder.include(destinationMarker.getPosition());

                            // Build the LatLngBounds
                            LatLngBounds bounds = builder.build();
                            // move camera to fit both markers
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));

                    } else {
                        // If no addresses are found, show a toast message
                        Toast.makeText(getActivity(), "Please Enter Destination Location", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(source.getQuery().toString().isEmpty() || destination.getQuery().toString().isEmpty()){
                    Toast.makeText(requireContext(), "Please select Source and Destination", Toast.LENGTH_SHORT).show();
                }
                else {

                    dialog.show();
                }
            }
        });

        // Schedule dialog variables initialization
        // Assigning UI elements from the dialog layout to variables.

        setPickup = dialog.findViewById(R.id.setPickup);
        datePicker = dialog.findViewById(R.id.datePicker);
        txt = dialog.findViewById(R.id.txt);
        timePicker = dialog.findViewById(R.id.timePicker);

        //close button from dialog
        AppCompatImageButton close = dialog.findViewById(R.id.closeButton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // end of initialization of dialog

        //setting the min date,
        // this will disable the previous dates,
        // so user cannot select past date.
        //this is for making sure user can only select present and future dates.

        Calendar calendar = Calendar.getInstance();

        long today = calendar.getTimeInMillis();
        datePicker.setMinDate(today);
        //max date to set 8 days next from today
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 7);
        long maxTime = maxDate.getTimeInMillis();
        datePicker.setMaxDate(maxTime);

        //Set pickup button on click listener
        setPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            // when user click on set pickup button, the date will be displayed on the text view and show today date if not selected.
            public void onClick(View view) {
                if (date != null && !date.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Pickup Scheduled");
                    builder.setMessage(date);
                    builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
                    AlertDialog dialogTime = builder.create();
                    dialogTime.show();
                    txt.setText(date);

                }
                else {
                    // if date is not selected then we will set the today's date.
                    Calendar calendar = Calendar.getInstance();
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    int currentMinute = calendar.get(Calendar.MINUTE);

                    int selectedHour = timePicker.getCurrentHour();
                    int selectedMinute = timePicker.getCurrentMinute();


                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(Calendar.YEAR, datePicker.getYear());
                    selectedCalendar.set(Calendar.MONTH, datePicker.getMonth());
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    selectedCalendar.set(Calendar.MINUTE, selectedMinute);

                    if (selectedCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) && selectedHour * 60 + selectedMinute < currentHour * 60 + currentMinute + 60) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Alert");
                        builder.setMessage("Please select a time at least 1 hour from now.");
                        builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
                        AlertDialog dialogTime = builder.create();
                        dialogTime.show();

                    } else {
                        String today = "Pickup: " + getMonthName(datePicker.getMonth()) + " " + datePicker.getDayOfMonth() + "   " + String.format("%02d:%02d", selectedHour, selectedMinute);
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Pickup Scheduled");
                        builder.setMessage(today);
                        builder.setPositiveButton("OK", (dialogInterface, i) -> {dialogInterface.dismiss();
                            dialog.dismiss();});
                        AlertDialog dialogTime = builder.create();
                        dialogTime.show();
                        txt.setText(today);
                    }
                }
            }
        });
    }

    private void requestLocationPermission() {
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION}
                    ,LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, move camera to current location
            moveCameraToCurrentLocation();
        }
    }

    private void moveCameraToCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null){
                    LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,15));
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
                }});
        }
    }

    private String getMonthName(int month){
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (month >= 0 && month < 12) {
            return monthNames[month];
        } else {
            // Handle invalid month number
            return "Invalid Month";
        }

    }

    private Marker customIcon(LatLng latLng, String title){
        return mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory
                                .decodeResource(getResources(),R.drawable.marker))));

    }

    private void drawLineBetweenMarkers() {
        if (sourceMarker != null && destinationMarker != null) {
            LatLng sourceLatLng = sourceMarker.getPosition();
            LatLng destinationLatLng = destinationMarker.getPosition();
            // Remove the existing line if present
            if (dottedLine != null) {
                dottedLine.remove();
            }
            // Create a PolylineOptions object to define the line
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(sourceLatLng, destinationLatLng)
                    .color(0xFF000000)
                    .width(5)
                    .pattern(java.util.Arrays.asList(new com.google.android.gms.maps.model.Dash(30f), new com.google.android.gms.maps.model.Gap(10)));
            // Add the Polyline to the map
            dottedLine = mMap.addPolyline(polylineOptions);
        }
    }
}
