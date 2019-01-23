package com.icubed.loansticdroid.activities;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapView mMapView;
    GoogleMap mGoogleMap;

    private LatLng borrowerLatLng;
    private BorrowersTable borrower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        borrower = getIntent().getParcelableExtra("borrower");
        borrowerLatLng = new LatLng(borrower.getBorrowerLocationLatitude(), borrower.getBorrowerLocationLongitude());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        drawMarker();
    }

    private void drawMarker() {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(borrowerLatLng)
                    .title(borrower.getLastName() + " " +borrower.getFirstName()));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(borrowerLatLng, 15));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
