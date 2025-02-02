package com.example.driver4u;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


public class HomePage extends AppCompatActivity {

    @Nullable
    private FusedLocationProviderClient fusedLocationClient;

    DrawerLayout drawerLayout;
    ImageButton imageButton;
    private ActivityResultLauncher<String[]> locationPermissionRequest;


    @Override
    @SuppressWarnings("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
        locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                        result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

                                fusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(HomePage.this, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                // Got last known location. In some rare situations this can be null.
                                                if (location != null) {
                                                    // Logic to handle location object
                                                    double latitude = location.getLatitude();
                                                    double longitude = location.getLongitude();
                                                    LatLng currentLatLng = new LatLng(latitude, longitude);

                                                    HomeFragment.mMap.addMarker(new MarkerOptions()
                                                            .position(currentLatLng)
                                                            .title("Current Location")
                                                            .icon(BitmapDescriptorFactory
                                                                    .fromBitmap(BitmapFactory
                                                                            .decodeResource(getResources(),R.drawable.driver))));

                                                    // Move camera to current location
                                                    HomeFragment.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                                                }
                                            }
                                        });
                                Log.d("Location", "precise location access granted");
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                Log.d("Location", "Only approximate location access granted.");
                            } else {
                                // No location access granted.
                                Log.d("Location", "No location access granted");
                            }
                        }
                );
        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show any
        // educational UI about why you need the permissions.
        List<String> permission = new ArrayList<>();
        permission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        locationPermissionRequest.launch(
                permission.toArray(new String[0]));


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadFragment(new HomeFragment(), 0);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home_icon_id) {
                    loadFragment(new HomeFragment(), 1);
                    return true;
                } else if (itemId == R.id.oneway_icon_id) {
                    loadFragment(new OnewayFragment(), 1);
                    return true;
                } else if (itemId == R.id.round_icon_id) {
                    loadFragment(new RoundFragment(), 1);
                    return true;
                } else if (itemId == R.id.outstation_icon_id) {
                    loadFragment(new OutstationFragment(), 1);
                    return true;
                }
                return false;
            }
        });
        drawerLayout = findViewById(R.id.main);
        imageButton = findViewById(R.id.userButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        NavigationView navigationView = findViewById(R.id.drawerMenu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.profileIconId) {
                    Toast.makeText(HomePage.this, "Profile Icon", Toast.LENGTH_SHORT).show();
                    loadActivity(ProfileActivity.class);
                    drawerLayout.close();
                    return true;
                } else if (itemId== R.id.addressIconId) {
                    Toast.makeText(HomePage.this, "Address Icon", Toast.LENGTH_SHORT).show();
                    loadActivity(AddressActivity.class);
                    drawerLayout.close();
                    return true;
                } else if (itemId == R.id.historyIconId) {
                    Toast.makeText(HomePage.this, "History Icon", Toast.LENGTH_SHORT).show();
                    loadActivity(HistoryActivity.class);
                    drawerLayout.close();
                    return true;
                } else if (itemId == R.id.paymentIconId) {
                    Toast.makeText(HomePage.this, "Payment Icon", Toast.LENGTH_SHORT).show();
                    loadActivity(PaymentsActivity.class);
                    drawerLayout.close();
                    return true;
                } else if (itemId == R.id.helpIconId) {
                    Toast.makeText(HomePage.this, "Help Icon", Toast.LENGTH_SHORT).show();
                    loadActivity(HelpActivity.class);
                    drawerLayout.close();
                    return true;
                } else if (itemId == R.id.aboutIconId) {
                    Toast.makeText(HomePage.this, "About Icon", Toast.LENGTH_SHORT).show();
                    loadActivity(AboutActivity.class);
                    drawerLayout.close();
                    return true;
                } else if (itemId == R.id.partnerIconId) {
                    Toast.makeText(HomePage.this, "Partner Icon", Toast.LENGTH_SHORT).show();
                    loadActivity(PartnerActivity.class);
                    drawerLayout.close();
                    return true;
                } else if (itemId == R.id.logoutIconId) {
                    Toast.makeText(HomePage.this, "Logout Icon", Toast.LENGTH_SHORT).show();
                    drawerLayout.close();
                    return true;
                }

                return false;
            }
        });
    }

    void loadFragment(Fragment fragment, int flag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (flag == 0) {
            fragmentTransaction.add(R.id.fragment_container, fragment);
        } else {
            fragmentTransaction.replace(R.id.fragment_container, fragment);
        }
        fragmentTransaction.commit();
    }

    void loadActivity(Class activity){

        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}
