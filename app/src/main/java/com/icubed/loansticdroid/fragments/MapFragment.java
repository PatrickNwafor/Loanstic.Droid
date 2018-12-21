package com.icubed.loansticdroid.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.icubed.loansticdroid.activities.MainActivity;
import com.icubed.loansticdroid.adapters.SlideUpPanelRecyclerAdapter;
import com.icubed.loansticdroid.models.Collection;
import com.icubed.loansticdroid.models.DueCollectionDetails;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.*;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icubed.loansticdroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback{

    MapView mMapView;
    GoogleMap mGoogleMap;
    private SlidingUpPanelLayout slidingLayout;
    private RecyclerView slideUpRecyclerView;
    private ImageView btnShow;
    TextView slideUp;
    private ImageView btnHide;
    Animation bounce,bounce1,blink;
    EditText search;

    private Collection collection;

    public List<DueCollectionDetails> dueCollectionList;
    public SlideUpPanelRecyclerAdapter slideUpPanelRecyclerAdapter;

    private static final String TAG = "MapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionGranted = false;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        btnShow = (ImageView) v.findViewById(R.id.btn_show);
        slideUp =  v.findViewById(R.id.slideUp);
        search =  v.findViewById(R.id.searchEditText);
        slideUpRecyclerView = v.findViewById(R.id.collection_list);

        bounce = AnimationUtils.loadAnimation( getContext(),R.anim.bounce);
        blink = AnimationUtils.loadAnimation( getContext(),R.anim.blink);
        bounce1 = AnimationUtils.loadAnimation( getContext(),R.anim.bounce1);
        btnShow.setAnimation(blink);
        slideUp.setAnimation(blink);
        search.setAnimation(bounce1);

        collection = new Collection(getActivity().getApplication(), getActivity());

        dueCollectionList = new ArrayList<>();
        slideUpRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        slideUpPanelRecyclerAdapter = new SlideUpPanelRecyclerAdapter(dueCollectionList);
        slideUpRecyclerView.setAdapter(slideUpPanelRecyclerAdapter);

        //btnHide = (ImageView) v.findViewById(R.id.btn_hide);

        mMapView = v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        getLocationPermission();
        //setting layout slide listener
        slidingLayout = (SlidingUpPanelLayout)v.findViewById(R.id.sliding_layout);

        //event
        slidingLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

                btnShow.setVisibility(View.GONE);
                slideUp.setVisibility(View.GONE);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {

            }
        });

      //  btnHide.setOnClickListener(onHideListener());
        btnShow.setOnClickListener(onShowListener());

        if(!collection.doesCollectionExistInLocalStorage()){
            collection.retrieveNewDueCollectionData();
        }else{
            collection.getDueCollectionData();
            collection.retrieveDueCollectionToLocalStorageAndCompareToCloud();
        }


        return v;
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

            }
        };
    }

    public void hideProgressBar() {
        ((MainActivity) getActivity()).hideProgressBar();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(43.1, -87.9)));
        mapOnClickListener();

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        mGoogleMap.moveCamera(cameraUpdate);

    }

    private void mapOnClickListener() {

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                hideKeyboardFrom();

                if(slidingLayout.getPanelState() == PanelState.EXPANDED){
                    slidingLayout.setPanelState(PanelState.COLLAPSED);
                }
            }
        });

    }

    /************Requesting Location Permission**********/
    private void getLocationPermission(){
        Log.d(TAG, "checking for permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;
            Log.d(TAG, "Permission already granted");
        }else{
            Log.d(TAG, "No permission yet");
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /************Accepting Permission*************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: permission failed");
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    //initialize our map
                }
            }
        }
    }

    public void hideKeyboardFrom() {
        View focuedView = getActivity().getCurrentFocus();
        if (focuedView != null) {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(focuedView.getWindowToken(), 0);
        }
    }

    public void hidePanel(){
        if(slidingLayout.getPanelState() == PanelState.EXPANDED)
        slidingLayout.setPanelState(PanelState.COLLAPSED);
    }

    public PanelState getPanelState(){
        return slidingLayout.getPanelState();
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
