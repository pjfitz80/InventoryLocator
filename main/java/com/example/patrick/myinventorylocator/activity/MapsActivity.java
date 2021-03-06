package com.example.patrick.myinventorylocator.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.example.patrick.myinventorylocator.R;
import com.example.patrick.myinventorylocator.fragment.BarcodeScanFragment;
import com.example.patrick.myinventorylocator.model.Vehicle;
import com.example.patrick.myinventorylocator.utility.MyDialogs;
import com.example.patrick.myinventorylocator.utility.MyFileReadWrite;
import com.example.patrick.myinventorylocator.utility.MyXMLParser;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, BarcodeScanFragment.OnFragmentInteractionListener {

    private static final int LOCATION_PERMISSION_FINE_PERMISSION = 0x000000;
    private static final String THE_XML_FILE_NAME = "auction.xml";

    private GoogleMap mMap; // The Google Map instance.
    public HashMap<String,Vehicle> mInventoryHashMap; // HashMap of Vehicles in Inventory.
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 1000;  /* 1 secs */
    private long FASTEST_INTERVAL = 500; /* .5 sec */
    private double mDeviceCurrentLat; // The current latitude of the device.
    private double mDeviceCurrentLong; // The current longitude of the device.
    private EditText mEditText;
    private FloatingActionButton fab;
    private MyDialogs myDialogs;
    private MyFileReadWrite myFileRW;
    private String subFolder = "/userdata";
    private String file = "test.ser";
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //createInventoryHashMapFromFile(THE_XML_FILE_NAME);

        myDialogs = new MyDialogs(this);
        myFileRW = new MyFileReadWrite(getApplication());
        mInventoryHashMap = myFileRW.readFile(subFolder, file);
        mp = MediaPlayer.create(this, R.raw.mario_pipe_2);

        mEditText = (EditText) findViewById(R.id.search);
        mEditText.setTransformationMethod(null);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Close soft keyboard on search button pressed.
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                    String mStockSearch = mEditText.getText().toString();
                    addMarker(mStockSearch);
                    mEditText.getText().clear();
                    return true;
                }
                return false;
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.custom_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBarcodeScanner();
                mEditText.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.INVISIBLE);
            }
        });

        updateMyCurrentLocation();
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
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if(checkPermissions()) {
            mMap.setMyLocationEnabled(true);
        }

        LatLng startLocation = new LatLng(47.20629614, -122.296838);
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(startLocation).zoom(18).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mp.start();
    }

    /**
     *
     */
    protected void updateMyCurrentLocation() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    /**
     * This method animates map so that current bearing is shown at top of device screen.
     * @param googleMap
     * @param bearing
     */
    private void updateCameraBearing(GoogleMap googleMap, float bearing) {
        if ( googleMap == null) return;
        CameraPosition camPos = CameraPosition
                .builder(googleMap.getCameraPosition())
                .bearing(bearing)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    /**
     *
     * @param location
     */
    public void onLocationChanged(Location location) {
        mDeviceCurrentLat = location.getLatitude();
        mDeviceCurrentLong = location.getLongitude();
        //updateCameraBearing(mMap, location.getBearing());
    }

    /**
     * Check to see that Location permissions have been granted.
     * @return boolean for permissions granted/not granted.
     */
    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_FINE_PERMISSION);
            return false;
        }
    }

    /**
     * Read and parser auction run list xml file from assets folder.
     * @param theFileName the String name of the asset file to be opened.
     */
    private void createInventoryHashMapFromFile(String theFileName) {
        try{
            MyXMLParser parser = new MyXMLParser();
            InputStream is = getAssets().open(theFileName);
            mInventoryHashMap = parser.parse(is);
        } catch (IOException e) {e.printStackTrace();}
    }

    /**
     * This method starts the BarcodeScan Fragment.
     */
    private void openBarcodeScanner() {
        BarcodeScanFragment tempFrag = new BarcodeScanFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.map, tempFrag, "")
                .addToBackStack(null).commit();
    }

    /**
     * This method build and returns a string based on values stored in the Vehicle object
     * for display as the map marker snippet.
     * @param theStockNumber String stock number of the vehicle who's info you want.
     * @return theResult A single String built from Vehicle object fields.
     */
    private String getVehicleInfo(String theStockNumber) {
        StringBuilder builder = new StringBuilder();
        builder.append(mInventoryHashMap.get(theStockNumber).getYear());
        builder.append(" ");
        builder.append(mInventoryHashMap.get(theStockNumber).getMake());
        builder.append(" ");
        builder.append(mInventoryHashMap.get(theStockNumber).getModel());
        builder.append(" ");
        builder.append(mInventoryHashMap.get(theStockNumber).getTrim());
        String theResult = builder.toString();

        return theResult;
    }

    /**
     * This method adds a marker to the map using the lat/long attached to the Vehicle object.
     * @param theString
     */
    private void addMarker(String theString) {
        if(mInventoryHashMap.containsKey(theString)) {
            double tempLat = mInventoryHashMap.get(theString).getLat();
            double tempLong = mInventoryHashMap.get(theString).getLong();
            LatLng temp = new LatLng(tempLat, tempLong);
            String tempSnippet = getVehicleInfo(theString);
            mMap.addMarker(new MarkerOptions().position(temp).title(theString).snippet(tempSnippet)).showInfoWindow();
            CameraPosition cameraPosition = new CameraPosition.Builder().target(temp).zoom(18).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else {
            myDialogs.stockNumberNotFoundDialog(); // Dialog handling input not found in Inventory hashmap.
        }
    }

    /**
     * Use devices current location to set Vehicle GPS coordinates.
     * @param theString
     */
    private void setCurrentLocation(String theString) {
        if(!mInventoryHashMap.containsKey(theString)) {
            Vehicle tempVehicle = new Vehicle();
            tempVehicle.setStockNumber(theString);
            mInventoryHashMap.put(theString, tempVehicle);
        }
        mInventoryHashMap.get(theString).setLat(mDeviceCurrentLat);
        mInventoryHashMap.get(theString).setLong(mDeviceCurrentLong);
    }

    /**
     * Callback method that receives barcode scan result as a String.
     * @param theStockNumber
     */
    @Override
    public void onFragmentInteraction(String theStockNumber) {
        setCurrentLocation(theStockNumber);
    }

    @Override
    public void onBackPressed() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            mEditText.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        myFileRW.writeFile(mInventoryHashMap, subFolder, file); // Save the current Inventory hashmap to internal storage before app closes.
    }
}