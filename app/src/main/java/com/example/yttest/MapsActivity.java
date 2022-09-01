package com.example.yttest;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.yttest.databinding.ActivityMapsBinding;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toast.makeText(this, "Please allow to create user", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                if(arg0 != null && arg0.getTitle().equals("User1")){
                    Intent intent1 = new Intent(MapsActivity.this, UserSignUpActivity.class);
                    startActivity(intent1);}

                if(arg0 != null && arg0.getTitle().equals("User2")){
                    Intent intent2 = new Intent(MapsActivity.this, UserSignUpActivity.class);
                    startActivity(intent2);}

                if(arg0 != null && arg0.getTitle().equals("User3")){
                    Intent intent3 = new Intent(MapsActivity.this, UserSignUpActivity.class);
                    startActivity(intent3);}

                if(arg0 != null && arg0.getTitle().equals("User4")){
                    Intent intent4 = new Intent(MapsActivity.this, UserSignUpActivity.class);
                    startActivity(intent4);}
            }
        });
        LatLng User1 = new LatLng(41.015137, 28.979530);
        LatLng User2 = new LatLng(39.925533, 32.866287);
        LatLng User3 = new LatLng(38.423733, 27.142826);
        LatLng User4 = new LatLng(38.680969, 39.226398);
        mMap.addMarker(new MarkerOptions()
                .position(User1)
                .title("User1")
                .snippet("Allow to create user"));
        mMap.addMarker(new MarkerOptions()
                .position(User2)
                .title("User2")
                .snippet("Allow to create user"));
        mMap.addMarker(new MarkerOptions()
                .position(User3)
                .title("User3")
                .snippet("Allow to create user"));
        mMap.addMarker(new MarkerOptions()
                .position(User4)
                .title("User4")
                .snippet("Allow to create user"));

    }
}





