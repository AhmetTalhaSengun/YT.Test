package com.example.yttest.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.yttest.Model.MarkerData;
import com.example.yttest.Model.UsersData;
import com.example.yttest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap = null;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<MarkerData> markerData = new ArrayList<MarkerData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toast.makeText(this, "Please allow to create user", Toast.LENGTH_LONG).show();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void getMarkerData(){
        CollectionReference markersRef = firebaseFirestore.collection("markers");

// Create a query against the collection.
        Query query = markersRef.whereEqualTo("registeredUserEmail", "");
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
        mMap.setOnInfoWindowClickListener(arg0 -> {
            Intent intent = new Intent(MapsActivity.this, UserSelectionScreenActivity.class);
            intent.putExtra("id",markerData.get(Integer.parseInt(Objects.requireNonNull(arg0.getTitle()))).getObjectID());
            startActivity(intent);
        });
        addMarkers();

    }
}





