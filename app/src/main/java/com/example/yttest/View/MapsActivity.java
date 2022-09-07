package com.example.yttest.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.yttest.BuildConfig;
import com.example.yttest.Model.MarkerData;
import com.example.yttest.Model.UsersData;
import com.example.yttest.R;
import com.example.yttest.util.HttpConnection;
import com.example.yttest.util.PathJSONParser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap = null;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<MarkerData> markerData = new ArrayList<MarkerData>();
    private FirebaseAuth auth;
    private String email;
    private LocationManager locationManager;
    private LatLng userLocation;
    private UsersData userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toast.makeText(this, "Please allow to create user", Toast.LENGTH_LONG).show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();
        getUserDetail();
        checkLocationPermission();
        askStoragePermissions();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);

    }

    private void getUserDetail() {
        CollectionReference userRef = firebaseFirestore.collection("users");

// Create a query against the collection.
        Query query = userRef.whereEqualTo("email", email);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userData = document.toObject( UsersData.class);
                                getMarkerData();
                                setMarkerOnClick();
                            }
                        } else {

                        }
                    }
                });
    }
    @Override
    public void onLocationChanged(Location location) {
        userLocation = new LatLng(location.getLatitude(),location.getLongitude());
    }

    public void getMarkerData(){
        CollectionReference markersRef = firebaseFirestore.collection("markers");

        if(userData.isAdmin) {
            markersRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                System.out.println("Marker Size" + task.getResult().size());

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
        } else {
           markersRef.whereEqualTo("registeredUserEmail", email).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                System.out.println("Marker Size" + task.getResult().size());

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
    }

    public void setMarkerOnClick(){
        if(userData.isAdmin) {
            mMap.setOnInfoWindowClickListener(arg0 -> {
                Intent intent = new Intent(MapsActivity.this, UserSelectionScreenActivity.class);
                intent.putExtra("id",markerData.get(Integer.parseInt(Objects.requireNonNull(arg0.getTitle()))).getObjectID());
                startActivity(intent);
            });
        } else {
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    if(userLocation != null) {
                        String url = getMapsApiDirectionsUrl(userLocation, marker.getPosition());
                        ReadTask downloadTask = new ReadTask();
                        downloadTask.execute(url);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(midPoint(userLocation.latitude, userLocation.longitude, marker.getPosition().latitude, marker.getPosition().longitude)).zoom(14).bearing((float) angleBteweenCoordinate(userLocation.latitude, userLocation.longitude, marker.getPosition().latitude, marker.getPosition().longitude)).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),10,null);
                    }
                    return true;
                }
            });
        }
    }

    private LatLng midPoint(double lat1, double long1, double lat2,double long2) {

        return new LatLng((lat1+lat2)/2, (long1+long2)/2);

    }

    private double angleBteweenCoordinate(double lat1, double long1, double lat2,
                                          double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return brng;
    }
    public void addMarkers(){
        for (int i = 0; i < markerData.size(); i++) {
            if(i == 0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(markerData.get(i).getLat()),Double.parseDouble(markerData.get(i).getLon())),
                        13));
            }
            if(Objects.equals(markerData.get(i).getRegisteredUserEmail(), "")) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(markerData.get(i).getLat()),Double.parseDouble(markerData.get(i).getLon()))).title(String.valueOf(i))
                        .snippet("Allow to create user").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            } else {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(markerData.get(i).getLat()),Double.parseDouble(markerData.get(i).getLon()))).title(String.valueOf(i))
                        .snippet("Allow to create user"));
            }

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        addMarkers();

    }

    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        String waypoints = "waypoints=optimize:true|"
                + origin.latitude + "," + origin.longitude
                + "|" + "|" +  dest.latitude + ","
                + dest.longitude;
        String OriDest = "origin="+origin.latitude+","+origin.longitude+"&destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";
        String params = OriDest+"&%20"+waypoints + "&" + sensor+ "&key=AIzaSyAoRxZRQAs7FzmLXNdhZenOJLo_ZEJF_5g";
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        System.out.println(url);
        return url;
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            System.out.println(routes.toString());
            if(routes != null && routes.size() > 0) {

                ArrayList<LatLng> points = null;
                PolylineOptions polyLineOptions = null;

                // traversing through routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(2);
                    polyLineOptions.color(Color.BLUE);
                }

                mMap.addPolyline(polyLineOptions);
                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                    Bitmap bitmap;

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        bitmap = snapshot;
                        try {
                            saveImage(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                mMap.snapshot(callback);

            }

        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        auth.signOut();
        Intent intentToAdminLogIn = new Intent(MapsActivity.this,AdminLogInActivity.class);
        startActivity(intentToAdminLogIn);
        finish();

        return super.onOptionsItemSelected(item);
    }

    public void saveImage(Bitmap bitmap) throws IOException, DocumentException {
        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes);
        File f = new File(directoryPath + "/DCIM/example.jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        System.out.println("Save IMAGE");
        savePDF();
    }

    public void savePDF() throws DocumentException, IOException {
        Document document = new Document();

        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();

        PdfWriter.getInstance(document, new FileOutputStream(directoryPath + "/Documents/example.pdf")); //  Change pdf's name.

        document.open();

        Image image = Image.getInstance(directoryPath + "/DCIM/example.jpg");  // Change image's name and extension.

        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
        image.scalePercent(scaler);
        image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);

        document.add(image);
        document.close();
        File file = new File(directoryPath + "/Documents/example.pdf");
        Uri uri = FileProvider.getUriForFile(MapsActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);

        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(uri,"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    public void askStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }
    }
}





