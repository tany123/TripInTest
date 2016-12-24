package com.selflearning.tripintest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Activity_Trip extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private RecyclerView rvTrip;
    private Adapter_TripList adapter_tripList;
    private List<Pojo_TripDetails> myList = new ArrayList<>();
    TextView txtSource;
    Context context;
    TextView txtDestination;
    TextView txtTotalTrip;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    private final long INTERVAL = 1000 * 10;
    private final long FASTEST_INTERVAL = 1000 * 5;
    private final int REQUEST_LOCATION = 121;
    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 122;
    Boolean is_location_requested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        findViews();

        is_location_requested = false;
        /** check if phone has google play services or not to get user's current location*/
        if (isGooglePlayServicesAvailable()) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
            createLocationRequest();
        } else {
            Toast.makeText(context, "Google play services are not available", Toast.LENGTH_SHORT).show();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult((Activity) context, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                startLocationUpdates();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


    private void findViews() {

        context = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rvTrip = (RecyclerView) findViewById(R.id.rvTrip);
        txtSource = (TextView) findViewById(R.id.txtSource);
        txtDestination = (TextView) findViewById(R.id.txtDestination);
        txtTotalTrip = (TextView) findViewById(R.id.txtTotalTrip);

        adapter_tripList = new Adapter_TripList(myList, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvTrip.setLayoutManager(mLayoutManager);
        rvTrip.setItemAnimator(new DefaultItemAnimator());
        rvTrip.setAdapter(adapter_tripList);

        txtSource.setText("RAJKOT");
        txtDestination.setText("AHMEDABAD");
        txtTotalTrip.setText("Total trip - 1120 kms");

        create_list();


    }

    public void onClick(View view) {
        if (mCurrentLocation != null) {
            String strLatitude = String.valueOf(mCurrentLocation.getLatitude());
            String strLongitude = String.valueOf(mCurrentLocation.getLongitude());
            pass_values_to_server(strLatitude, strLongitude);
        }
    }

    private void pass_values_to_server(String strLatitude, String strLongitude) {
        Toast.makeText(context, "current position " + strLatitude + "," + strLongitude, Toast.LENGTH_LONG).show();
    }

    /**
     * create trip array list to assign it to RecyclerView
     */
    private void create_list() {
        Pojo_TripDetails pojo_tripDetails = new Pojo_TripDetails();
        pojo_tripDetails.setIs_Start(true);
        pojo_tripDetails.setIs_visited(true);
        pojo_tripDetails.setIs_visiting(true);
        pojo_tripDetails.setIs_Stops(false);
        pojo_tripDetails.setIs_Destination(false);
        pojo_tripDetails.setName("ARYA INDUSTRIES PVT. LTD.");
        pojo_tripDetails.setAddress("301, Summit Business Bay, Near metro station, Rajkot- 3800076");
        pojo_tripDetails.setContactName("Gajendra Patel");
        pojo_tripDetails.setContactNo("+91 98310 98310");
        myList.add(pojo_tripDetails);

        pojo_tripDetails = new Pojo_TripDetails();
        pojo_tripDetails.setIs_Start(false);
        pojo_tripDetails.setIs_Stops(true);
        pojo_tripDetails.setIs_Destination(false);
        pojo_tripDetails.setIs_visited(false);
        pojo_tripDetails.setIs_visiting(true);
        pojo_tripDetails.setName("Jain Marbles Corps");
        pojo_tripDetails.setAddress("Rajkot, Patel Road");
        myList.add(pojo_tripDetails);

        pojo_tripDetails = new Pojo_TripDetails();
        pojo_tripDetails.setIs_Start(false);
        pojo_tripDetails.setIs_Stops(true);
        pojo_tripDetails.setIs_Destination(false);
        pojo_tripDetails.setIs_visited(false);
        pojo_tripDetails.setIs_visiting(false);
        pojo_tripDetails.setName("Vallabhai Patel Stadium");
        pojo_tripDetails.setAddress("Rajkot, Rajat Shah Marg");
        myList.add(pojo_tripDetails);

        pojo_tripDetails = new Pojo_TripDetails();
        pojo_tripDetails.setIs_Start(false);
        pojo_tripDetails.setIs_Stops(false);
        pojo_tripDetails.setIs_Destination(true);
        pojo_tripDetails.setIs_visited(false);
        pojo_tripDetails.setIs_visiting(false);
        pojo_tripDetails.setName("Sarkar & Sarkar Factory");
        pojo_tripDetails.setAddress("Ahmedabad, M.G. Road");
        myList.add(pojo_tripDetails);

        adapter_tripList.notifyDataSetChanged();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION);
            } else {
                is_location_requested = true;
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
                if (mMap != null)
                    mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_LOCATION) {
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        LatLng current_location = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(current_location).zoom(14).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null)
            if (is_location_requested) {
                is_location_requested = false;
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }
        }
    }
}
