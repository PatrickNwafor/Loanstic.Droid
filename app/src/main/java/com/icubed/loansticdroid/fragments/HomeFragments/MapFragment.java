package com.icubed.loansticdroid.fragments.HomeFragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.models.Collection;
import com.icubed.loansticdroid.models.DueCollectionDetails;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    GoogleMap mGoogleMap;
    private SlidingUpPanelLayout slidingLayout;
    private RecyclerView slideUpRecyclerView;
    private ImageView btnShow;
    TextView slideUp;
    Animation bounce, bounce1, blink;
    EditText search;

    private Collection collection;
    private Account account;
    private BorrowersQueries borrowersQueries;

    public List<DueCollectionDetails> dueCollectionList;
    public SlideUpPanelRecyclerAdapter slideUpPanelRecyclerAdapter;

    private static final String TAG = "MapFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private LocationProviderUtil locationProviderUtil;
    private PlayServiceUtil playServiceUtil;

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
        btnShow = v.findViewById(R.id.btn_show);
        slideUp = v.findViewById(R.id.slideUp);
        search = v.findViewById(R.id.searchEditText);
        slideUpRecyclerView = v.findViewById(R.id.collection_list);

        bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        blink = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
        bounce1 = AnimationUtils.loadAnimation(getContext(), R.anim.bounce1);
        btnShow.setAnimation(blink);
        slideUp.setAnimation(blink);
        search.setAnimation(bounce1);

        collection = new Collection(getActivity().getApplication(), getActivity());
        account = new Account();
        borrowersQueries = new BorrowersQueries(getContext());
        playServiceUtil = new PlayServiceUtil(getContext());
        locationProviderUtil = new LocationProviderUtil(getContext());

        dueCollectionList = new ArrayList<>();
        slideUpRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        slideUpPanelRecyclerAdapter = new SlideUpPanelRecyclerAdapter(dueCollectionList);
        slideUpRecyclerView.setAdapter(slideUpPanelRecyclerAdapter);

        mMapView = v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        //setting layout slide listener
        slidingLayout = v.findViewById(R.id.sliding_layout);
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

        btnShow.setOnClickListener(onShowListener());

        /*****
         * @Todo
         * to un comment the line of code below to load due collections
         * to remove the hide progress bar method
         */
//        if (!collection.doesCollectionExistInLocalStorage()) {
//            collection.retrieveNewDueCollectionData();
//        } else {
//            collection.getDueCollectionData();
//            collection.retrieveDueCollectionToLocalStorageAndCompareToCloud();
//        }
        hideProgressBar();
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
        mapOnClickListener();
        initMap();
    }

    private void mapOnClickListener() {

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                hideKeyboardFrom();

                if (slidingLayout.getPanelState() == PanelState.EXPANDED) {
                    slidingLayout.setPanelState(PanelState.COLLAPSED);
                }
            }
        });

    }

    //Initialize map
    private void initMap() {
        if (playServiceUtil.isGooglePlayServicesAvailable()) {
            if (mGoogleMap != null) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                //Add Maps code here
                mGoogleMap.getUiSettings().setCompassEnabled(false);
                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {
        locationProviderUtil.requestSingleUpdate(new LocationProviderUtil.LocationCallback() {
            @Override
            public void onNewLocationAvailable(LocationProviderUtil.GPSCoordinates location) {
                drawMarker(location.getLocation);
            }
        });
    }

    /****************************set marker on map******************************/
    private void drawMarker(Location location) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("Current Position"));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 15));
        }

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
        getCurrentLocation();
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