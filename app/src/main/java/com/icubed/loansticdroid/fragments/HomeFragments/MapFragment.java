package com.icubed.loansticdroid.fragments.HomeFragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;
import com.icubed.loansticdroid.activities.MainActivity;
import com.icubed.loansticdroid.fragments.CollectionFragments.DueCollectionFragment;
import com.icubed.loansticdroid.fragments.CollectionFragments.OverDueCollectionFragment;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.CustomDialogBox.PaymentDialogBox;
import com.icubed.loansticdroid.util.KeyboardUtil;
import com.icubed.loansticdroid.util.MapInfoWindow.MapWrapperLayout;
import com.icubed.loansticdroid.util.MapInfoWindow.OnInfoWindowElemTouchListener;
import com.icubed.loansticdroid.util.PlayServiceUtil;
import com.icubed.loansticdroid.util.LocationProviderUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icubed.loansticdroid.R;

import java.util.ArrayList;
import java.util.List;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    MapView mMapView;
    public GoogleMap mGoogleMap;
    private SlidingUpPanelLayout slidingLayout;
    private ImageView btnShow,btnShow1,collectionImage;
    TextView slideUp;
    Animation bounce, bounce1, blink;
    EditText search;
    private static boolean isLocationModeCheck = true;
    private Polyline polyline;

    public MarkerOptions markerOptions;
    public SegmentedButtonGroup sbg;
    public LinearLayout progressLayout, collectionLayout;
    public Marker myMarker = null;
    public boolean isNoCol = true;

    private static final String TAG = "MapFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    public LocationProviderUtil locationProviderUtil;
    private PlayServiceUtil playServiceUtil;
    public LatLng selectedUserLatLng = null;
    private DueCollectionFragment dueCollectionFragment;
    private OverDueCollectionFragment overDueCollectionFragment;
    public MapWrapperLayout mapWrapperLayout;
    private boolean fromMarkerClick = false;
    private boolean firstLaunch;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {

        firstLaunch = false;

        collectionImage = v.findViewById(R.id.collection_image);

        btnShow = v.findViewById(R.id.btn_show);
        btnShow1 = v.findViewById(R.id.btn_show1);
        slideUp = v.findViewById(R.id.slideUp);
        sbg = v.findViewById(R.id.segmentedButtonGroup);
        search = v.findViewById(R.id.searchEditText);
        mapWrapperLayout = v.findViewById(R.id.map_relative_layout);


        bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        blink = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
        bounce1 = AnimationUtils.loadAnimation(getContext(), R.anim.bounce1);
        btnShow.setAnimation(blink);
        btnShow1.setAnimation(blink);
        slideUp.setAnimation(blink);
        search.setAnimation(bounce1);

        playServiceUtil = new PlayServiceUtil(getContext());
        locationProviderUtil = new LocationProviderUtil(getContext());

        mMapView = v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        //setting layout slide listener
        slidingLayout = v.findViewById(R.id.sliding_layout);
        //event
        final float[] prevProgress = new float[1];
        prevProgress[0] = 0;
        slidingLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                btnShow.setVisibility(View.GONE);
                slideUp.setVisibility(View.GONE);
                collectionImage.setVisibility(View.VISIBLE);

                float diff = v - prevProgress[0];
                if(diff > 0){
                    //increasing
                    Log.d(TAG, "increase: "+v);
                }
                else{
                    //decrease
                    Log.d(TAG, "decrease: "+v);
                }
                prevProgress[0] = v;
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                if(newState == PanelState.EXPANDED){
                    btnShow.setVisibility(View.GONE);
                    collectionImage.setVisibility(View.GONE);
                    ((MainActivity) getContext()).viewSwitch1.setVisibility(GONE);
                }else if(newState == PanelState.COLLAPSED){
                    btnShow.setVisibility(View.VISIBLE);
                    collectionImage.setVisibility(View.VISIBLE);
                    ((MainActivity) getContext()).viewSwitch1.setVisibility(VISIBLE);
                }
            }

        });

        dueCollectionFragment = new DueCollectionFragment();
        overDueCollectionFragment = new OverDueCollectionFragment();

        btnShow.setOnClickListener(onShowListener());
    }

    /**
     * Request show sliding layout when clicked
     * @return
     */
    private View.OnClickListener onShowListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show sliding layout in bottom of screen (not expand it)
                slidingLayout.setPanelState(PanelState.EXPANDED);
                collectionImage.setVisibility(View.GONE);
            }
        };
    }

    /************Instantiate fragment transactions**********/
    private void startFragment(Fragment fragment, String fragmentTag){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment, fragmentTag);
        transaction.commit();
    }

    public void hideProgressBar() {
        progressLayout.setVisibility(GONE);
        collectionLayout.setVisibility(VISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        int height = getResources().getDisplayMetrics().heightPixels;
        mGoogleMap.setPadding(0, (int) (0.2*height), 0, (int) (0.15*height));
        //customInfoWindowInit(mGoogleMap);
        onMapReadyFeatures();
    }

    private void onMapReadyFeatures(){
        if(checkIfLocationModeIsHighAccuracy()) {
            mapOnClickListener();
            initMap();
            startFragment(dueCollectionFragment, "due");
            //segmented control
            sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
                @Override
                public void onClickedButtonPosition(int position) {
                    if (position == 0) {

                        startFragment(dueCollectionFragment, "due");
                    } else if (position == 1) {

                        startFragment(overDueCollectionFragment, "overdue");
                    }
                }
            });
        }else {
            isLocationModeCheck = false;

            boolean isGPSEnabled = LocationProviderUtil.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = LocationProviderUtil.mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!(isGPSEnabled || isNetworkEnabled)) AndroidUtils.gpsDisabledMessage(getContext());
            else AndroidUtils.changeLocationModeToHighAccuracy(getContext());
        }
    }

    private boolean checkIfLocationModeIsHighAccuracy(){
        int locationMode = 4234;
        try {
            locationMode = Settings.Secure.getInt(getContext().getContentResolver(),Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY); //check location mode

    }


    public void moveCamera(ArrayList<Marker> markers, List<LatLng> path){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if(path != null) {
            for (LatLng marker : path) {

                builder.include(marker);

            }
        }else{
            for (Marker marker : markers) {

                builder.include(marker.getPosition());

            }
        }

        LatLngBounds bounds = builder.build();

        //Then obtain a movement description object by using the factory: CameraUpdateFactory:
        int padding = 50;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
        mGoogleMap.animateCamera(cu);

        if(fromMarkerClick){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomBy(-1f));
                    fromMarkerClick = false;
                }
            }, 2000);
        }
    }

    public void getRoute(LatLng user, LatLng bor, final ArrayList<Marker> markers){

        //Define list to get all latlng for the route
        String origin = user.latitude +","+user.longitude;
        String destination = bor.latitude +","+bor.longitude;

        GeoApiContext geoApiContext = new GeoApiContext().setApiKey("AIzaSyDljIVWu1Pi1RO1Gl9DQrYpoXfKQ5TnHC8");

        //Execute Directions API request
        final DirectionsApiRequest req = DirectionsApi.newRequest(geoApiContext)
                .origin(origin)
                .destination(destination);

        req.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(final DirectionsResult res) {
                Log.d("Directions", res.toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawRoute(res, markers);
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        fromMarkerClick = true;
        marker.showInfoWindow();
        if(!marker.equals(myMarker)){
            ArrayList<Marker> arrayList = new ArrayList<>();
            arrayList.add(marker);
            arrayList.add(myMarker);
            selectedUserLatLng = marker.getPosition();
            getRoute(myMarker.getPosition(), marker.getPosition(), arrayList);
            return true;
        }

        return false;
    }

    private void drawRoute(DirectionsResult res, ArrayList<Marker> markers) {

        final List<LatLng> path = new ArrayList<>();

        //Loop through legs and steps to get encoded polylines of each step
        if (res.routes != null && res.routes.length > 0) {
            DirectionsRoute route = res.routes[0];

            path.add(markers.get(1).getPosition());
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
            path.add(markers.get(0).getPosition());
        }

        //Draw the polyline
        if (path.size() > 0) {
            if(polyline != null) polyline.remove();
            PolylineOptions opts = new PolylineOptions().addAll(path).color(R.color.colorPrimary).width(20);
            polyline = mGoogleMap.addPolyline(opts);
        }

        moveCamera(markers, path);
    }

    private void mapOnClickListener() {

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                KeyboardUtil.hideKeyboard(getActivity());

                if (slidingLayout.getPanelState() == PanelState.EXPANDED) {
                    slidingLayout.setPanelState(PanelState.COLLAPSED);
                }
            }
        });

    }

    //Initialize map
    private void initMap() {
        if (playServiceUtil.isGooglePlayServicesAvailable() && checkIfLocationModeIsHighAccuracy()) {
            if (mGoogleMap != null) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                //Add Maps code here
                getCurrentLocation();
            }
        }
    }

    public void getCurrentLocation() {
        locationProviderUtil.requestSingleUpdate(new LocationProviderUtil.LocationCallback() {
            @Override
            public void onNewLocationAvailable(LocationProviderUtil.GPSCoordinates location) {
                getCurrentLocationMarker(location.getLocation);
            }
        });
    }

    private void getCurrentLocationMarker(Location location) {
        markerOptions = new MarkerOptions();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //adding markerOptions properties for driver
        markerOptions.position(latLng);
        markerOptions.title("Your Location");
        markerOptions.anchor(0.5f, 0.5f);


        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_marker_layout_user, null);
        CircleImageView circleImageView = view.findViewById(R.id.user_dp);
        circleImageView.setImageResource(R.drawable.new_borrower_green);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.convertViewsToBitmap(view)));

        if(isNoCol){
            drawMarker(markerOptions);
            isNoCol = false;
        }
    }

    public void drawMarker(MarkerOptions markerOptions){
        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15));
        myMarker = mGoogleMap.addMarker(markerOptions);
    }

    /***************************Accepting Permission***********************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called");
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    getCurrentLocation();
                    //initialize our map
                }
            }
        }
    }

    public void hidePanel(){
        if(slidingLayout.getPanelState() == PanelState.EXPANDED || slidingLayout.getPanelState() == PanelState.ANCHORED)
        slidingLayout.setPanelState(PanelState.COLLAPSED);
    }

    public PanelState getPanelState(){
        return slidingLayout.getPanelState();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if(firstLaunch) {
            if (!isLocationModeCheck) onMapReadyFeatures();
            getCurrentLocation();
        }

        firstLaunch = true;
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
