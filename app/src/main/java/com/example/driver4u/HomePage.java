package com.example.driver4u;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;


public class HomePage extends AppCompatActivity {

    @Nullable
    DrawerLayout drawerLayout;
    ImageButton imageButton;


    @Override
    @SuppressWarnings("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
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
