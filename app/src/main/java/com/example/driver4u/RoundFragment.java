package com.example.driver4u;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class RoundFragment extends Fragment {
    private SearchView searchView;
    GoogleMap mMap;
    SupportMapFragment mapFragment;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng pune = new LatLng(18.516726, 73.856255);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(pune));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_round, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.mapSearch);
        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addresslist =null;
                if(location != null){
                    Geocoder geocoder = new Geocoder(getActivity());
                    try{
                        addresslist = geocoder.getFromLocationName(location, 3);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    if (addresslist != null && !addresslist.isEmpty()) {
                        Address address = addresslist.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                    } else {

                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mapFragment.getMapAsync(callback);
                return false;
            }
        });
    }
}