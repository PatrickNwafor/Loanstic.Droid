package com.icubed.loansticdroid.activities;

import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.util.LocationProviderUtil;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = ".LocationActivity";
    MapView mMapView;
    GoogleMap mMap;

    private LatLng borrowerLatLng;
    private BorrowersTable borrower;
    private LocationProviderUtil locationProviderUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        locationProviderUtil = new LocationProviderUtil(this);
        locationProviderUtil.getLocationPermission();
        borrower = getIntent().getParcelableExtra("borrower");
        borrowerLatLng = new LatLng(borrower.getBorrowerLocationLatitude(), borrower.getBorrowerLocationLongitude());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawMarker();
    }

    private void drawMarker() {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(borrowerLatLng)
                    .title(borrower.getLastName() + " " +borrower.getFirstName()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(borrowerLatLng, 15));
        }

    }

    public void draw_route(View view) {

        locationProviderUtil.requestSingleUpdate(new LocationProviderUtil.LocationCallback() {
            @Override
            public void onNewLocationAvailable(LocationProviderUtil.GPSCoordinates location) {
                LatLng user = new LatLng(location.getLocation.getLatitude(), location.getLocation.getLongitude());
                LatLng bor = new LatLng(borrower.getBorrowerLocationLatitude(), borrower.getBorrowerLocationLongitude());

                //getRoute(user, bor);
                getRoute(user, bor);
            }
        });
    }

    public void getRoute(LatLng user, LatLng bor){

        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList<>();
        String origin = user.latitude +","+user.longitude;
        String destination = bor.latitude +","+bor.longitude;

        mMap.clear();
        placeMarkers(user, bor);

        //Execute Directions API request
        GeoApiContext context = new GeoApiContext()
                .setApiKey("AIzaSyDljIVWu1Pi1RO1Gl9DQrYpoXfKQ5TnHC8");


        DirectionsApiRequest req = DirectionsApi.newRequest(context)
                .origin(origin)
                .destination(destination)
                .mode(TravelMode.WALKING);

        try {
            DirectionsResult res = req.await();

            Log.d("Directions", res.toString());

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    public void placeMarkers(LatLng currentLatLng, LatLng borrowerLocation){

        ArrayList<Marker> markers = new ArrayList<>();

        markers.clear();

        MarkerOptions markerOptionsUser = new MarkerOptions();
        MarkerOptions markerOptionsBorrower = new MarkerOptions();

        //adding markerOptions properties for driver
        markerOptionsUser.position(currentLatLng);
        markerOptionsUser.title("Your Location");
        markerOptionsUser.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markers.add(mMap.addMarker(markerOptionsUser));

        //adding markerOptions properties for request
        markerOptionsBorrower.position(borrowerLocation);
        markerOptionsBorrower.title(borrower.getLastName() + " " + borrower.getFirstName());
        markerOptionsBorrower.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        markers.add(mMap.addMarker(markerOptionsBorrower));

        //to move the map camera to show both the driver and user location
        moveCamera(markers);

    }

    public void moveCamera(ArrayList<Marker> markers){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : markers) {

            builder.include(marker.getPosition());

        }
        LatLngBounds bounds = builder.build();

        //Then obtain a movement description object by using the factory: CameraUpdateFactory:

        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        //Finally move the map:
        //Or if you want an animation:

        mMap.animateCamera(cu);

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
