package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.CollectionDetailsActivity;
import com.icubed.loansticdroid.activities.LoanRepayment;
import com.icubed.loansticdroid.fragments.HomeFragments.MapFragment;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.models.DueCollectionDetails;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.CustomDialogBox.PaymentDialogBox;
import com.icubed.loansticdroid.util.MapInfoWindow.OnInfoWindowElemTouchListener;

import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SlideUpPanelRecyclerAdapter extends RecyclerView.Adapter<SlideUpPanelRecyclerAdapter.ViewHolder> {

    List<DueCollectionDetails> collectionList;
    List<CollectionTable> collections;
    Context context;
    MapFragment fragment;
    private AlertDialog.Builder builder;
    private PaymentDialogBox paymentDialogBox;

    private FragmentActivity fragmentActivity;

    public SlideUpPanelRecyclerAdapter(List<DueCollectionDetails> collectionList, FragmentActivity activity, List<CollectionTable> collectionTable) {
        this.collectionList = collectionList;
        this.collections = collectionTable;
        fragmentActivity = activity;
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        fragment = (MapFragment) fm.findFragmentByTag("home");
        builder = new AlertDialog.Builder(fragmentActivity);
        paymentDialogBox = new PaymentDialogBox(fragmentActivity);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final String firstName = collectionList.get(position).getFirstName();
        final String lastName = collectionList.get(position).getLastName();
        final String groupName = collectionList.get(position).getGroupName();
        holder.setCollectionName(firstName, lastName,groupName);
        holder.setCollectionAmount(collectionList.get(position).getDueAmount(), collectionList.get(position).getAmountPaid());
        holder.setBusiness(collectionList.get(position).getBusinessName(), groupName);
        holder.setImage(collectionList.get(position));

        holder.detailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CollectionDetailsActivity.class);
                intent.putExtra("dueCollectionDetails", collectionList.get(position));
                intent.putExtra("collection", collectionList.get(position).getCollectionTable());
                intent.putExtra("lastUpdatedAt", collectionList.get(position).getCollectionTable().getLastUpdatedAt());
                intent.putExtra("dueDate", collectionList.get(position).getCollectionTable().getCollectionDueDate());
                intent.putExtra("timestamp", collectionList.get(position).getCollectionTable().getTimestamp());
                context.startActivity(intent);
            }
        });

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) fragmentActivity.getSystemService(fragmentActivity.LAYOUT_INFLATER_SERVICE);
                final View view = layoutInflater.inflate(R.layout.custom_marker_layout_collection, null);
                final CircleImageView circleImageView = view.findViewById(R.id.user_dp);

                fragment.mGoogleMap.clear();
                final ArrayList<Marker> markers = new ArrayList<>();

                final MarkerOptions markerOptions = new MarkerOptions();

                LatLng latLng = new LatLng(collectionList.get(position).getLatitude(), collectionList.get(position).getLongitude());
                //adding markerOptions properties for driver
                markerOptions.position(latLng);
                markerOptions.anchor(0.5f, 0.5f);
                if(collectionList.get(position).getGroupName() == null){
                    markerOptions.title(lastName + " " + firstName);
                    if(collectionList.get(position).getImageByteArray() == null)
                        circleImageView.setImageResource(R.drawable.new_borrower);
                    else
                        circleImageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(collectionList.get(position).getImageByteArray()));
                }else {
                    markerOptions.title(groupName);
                    circleImageView.setImageResource(R.drawable.new_group);
                }

                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.convertViewsToBitmap(view)));

                fragment.hidePanel();
                Marker mark = fragment.mGoogleMap.addMarker(markerOptions);

                mark.setTag(collections.get(position));

                customInfoWindowInit(fragment.mGoogleMap);

                //adding marker to map
                markers.add(mark);

                fragment.myMarker = fragment.mGoogleMap.addMarker(fragment.markerOptions);
                markers.add(fragment.myMarker);

                fragment.getRoute(fragment.markerOptions.getPosition(), markerOptions.getPosition(), markers);
                fragment.selectedUserLatLng = latLng;
            }
        });
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private void customInfoWindowInit(GoogleMap map) {
        fragment.mapWrapperLayout.init(map, getPixelsFromDp(fragment.getContext(), 39 + 20));

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        final View infoWindow = fragmentActivity.getLayoutInflater().inflate(R.layout.custom_info_layout, null);
        final TextView infoTitle = infoWindow.findViewById(R.id.title);
        final TextView colTitle = infoWindow.findViewById(R.id.title1);
        final ImageButton navBtn = infoWindow.findViewById(R.id.nav);
        final ImageButton colBtn = infoWindow.findViewById(R.id.col);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        final OnInfoWindowElemTouchListener infoButtonListener = new OnInfoWindowElemTouchListener(navBtn,
                fragment.getResources().getDrawable(R.color.whiteEnd),
                fragment.getResources().getDrawable(R.color.darkGrey))
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+marker.getPosition().latitude+","+marker.getPosition().longitude+""));
                fragment.getActivity().startActivity(intent);
            }
        };
        navBtn.setOnTouchListener(infoButtonListener);

        //custom info window collection button click listener
        final OnInfoWindowElemTouchListener infoButtonListener2 = new OnInfoWindowElemTouchListener(colBtn, fragment.getResources().getDrawable(R.color.whiteEnd), fragment.getResources().getDrawable(R.color.darkGrey)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                CollectionTable collectionTable = (CollectionTable) marker.getTag();
                makePayment(collectionTable);
            }
        };
        colBtn.setOnTouchListener(infoButtonListener2);


        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                if(marker.getTitle().equals("Your Location")){
                    navBtn.setVisibility(GONE);
                    colBtn.setVisibility(GONE);
                    colTitle.setVisibility(GONE);
                }else {
                    navBtn.setVisibility(VISIBLE);
                    colBtn.setVisibility(VISIBLE);
                    colTitle.setVisibility(VISIBLE);
                }

                infoTitle.setText(marker.getTitle());
                CollectionTable collectionTable = (CollectionTable) marker.getTag();
                colTitle.setText("Collection Number: " + collectionTable.getCollectionNumber());
                infoButtonListener.setMarker(marker);
                infoButtonListener2.setMarker(marker);
                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                fragment.mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
    }

    private void makePayment(final CollectionTable collectionTable) {
        paymentDialogBox.setOnYesClicked(new PaymentDialogBox.OnButtonClick() {
            @Override
            public void onYesButtonClick() {
                Intent intent = new Intent(fragment.getContext(), LoanRepayment.class);
                intent.putExtra("collection", collectionTable);
                intent.putExtra("lastUpdatedAt", collectionTable.getLastUpdatedAt());
                intent.putExtra("dueDate", collectionTable.getCollectionDueDate());
                intent.putExtra("timestamp", collectionTable.getTimestamp());
                fragment.startActivity(intent);
            }
        });
        paymentDialogBox.show();
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        private TextView jobTextView;
        private CircleImageView collectionImageView;
        private TextView collectionNameTextView;
        private TextView amountTextView;
        public TextView detailsTextView;
        public FrameLayout frameLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            collectionNameTextView = mView.findViewById(R.id.collectionNameTextView);
            amountTextView = mView.findViewById(R.id.amountTextView);
            jobTextView = mView.findViewById(R.id.jobTextView);
            collectionImageView = mView.findViewById(R.id.circleImageView);
            detailsTextView = mView.findViewById(R.id.detailsTextView);
            frameLayout = mView.findViewById(R.id.frame);
        }

        public void setCollectionName(String firstName, String lastName, String groupName){
            if(groupName == null) collectionNameTextView.setText(lastName + " " + firstName);
            else collectionNameTextView.setText(groupName);
        }

        public void setCollectionAmount(double collectionAmount, double amountPaid){
            amountTextView.setText(String.valueOf(collectionAmount-amountPaid));
        }

        public void setBusiness(String businessName, String groupName) {
            if(groupName == null) jobTextView.setText(businessName);
            else jobTextView.setText("Group");
        }

        public void setImage(DueCollectionDetails dueCollectionDetails) {

            if(dueCollectionDetails.getGroupName() == null) {
                if (dueCollectionDetails.getImageByteArray() == null) {
                    BitmapUtil.getImageAndThumbnailWithGlide(mView.getContext(), dueCollectionDetails.getImageUri(), dueCollectionDetails.getImageUriThumb()).into(collectionImageView);
                } else {
                    collectionImageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(dueCollectionDetails.getImageByteArray()));
                }
            }else{
                collectionImageView.setImageResource(R.drawable.new_group);
            }
        }
    }

}
