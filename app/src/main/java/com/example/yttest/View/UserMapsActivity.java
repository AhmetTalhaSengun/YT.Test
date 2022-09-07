package com.example.yttest.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.yttest.Model.MarkerData;
import com.example.yttest.R;
import com.example.yttest.util.HttpConnection;
import com.example.yttest.util.PathJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.yttest.databinding.ActivityUserMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityUserMapsBinding binding;

    private FirebaseFirestore firebaseFirestore;
    private ArrayList<MarkerData> markerData = new ArrayList<MarkerData>();
    private String email;
    private LocationManager locationManager;
    private LatLng userLocation;
    private FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        auth= FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
    }


    @Override
    public void onLocationChanged(Location location) {
        userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,
                13));
    }


    public void getMarkerData(){
        CollectionReference markersRef = firebaseFirestore.collection("markers");
        Query query = markersRef.whereEqualTo("registeredUserEmail", email);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MarkerData data = document.toObject( MarkerData.class);
                                data.setObjectID(document.getId());
                                markerData.add(data);
                            }
                            if(mMap != null) {
                                addMarkers();
                            }
                        } else {

                        }
                    }
                });
    }

    public void addMarkers(){
        for (int i = 0; i < markerData.size(); i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(markerData.get(i).getLat()),Double.parseDouble(markerData.get(i).getLon()))).title(String.valueOf(i))
                    .snippet("Allow to create user"));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getMarkerData();
        mMap = googleMap;


        addMarkers();
    }


}
