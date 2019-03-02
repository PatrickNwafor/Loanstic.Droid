package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.CollectionDetailsActivity;
import com.icubed.loansticdroid.fragments.HomeFragments.MapFragment;
import com.icubed.loansticdroid.models.DueCollectionDetails;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class SlideUpPanelRecyclerAdapter extends RecyclerView.Adapter<SlideUpPanelRecyclerAdapter.ViewHolder> {

    List<DueCollectionDetails> collectionList;
    Context context;
    MapFragment fragment;

    private FragmentActivity fragmentActivity;

    public SlideUpPanelRecyclerAdapter(List<DueCollectionDetails> collectionList, FragmentActivity activity) {
        this.collectionList = collectionList;
        fragmentActivity = activity;
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        fragment = (MapFragment) fm.findFragmentByTag("home");
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
        holder.setCollectionAmount(collectionList.get(position).getDueAmount());
        holder.setBusiness(collectionList.get(position).getBusinessName(), groupName);
        holder.setImage(collectionList.get(position));

        holder.detailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CollectionDetailsActivity.class);
                intent.putExtra("firstName", collectionList.get(position).getFirstName());
                intent.putExtra("lastName", collectionList.get(position).getLastName());
                intent.putExtra("businessName", collectionList.get(position).getBusinessName());
                intent.putExtra("collectionAmount", collectionList.get(position).getDueAmount());
                intent.putExtra("isDueCollected", collectionList.get(position).getIsDueCollected());
                intent.putExtra("collectionDueDate", collectionList.get(position).getDueCollectionDate());
                intent.putExtra("collectionNumber", collectionList.get(position).getCollectionNumber());
                intent.putExtra("workAddress", collectionList.get(position).getWorkAddress());
                intent.putExtra("groupName", collectionList.get(position).getGroupName());
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
                markers.add(fragment.mGoogleMap.addMarker(markerOptions));

                fragment.myMarker = fragment.mGoogleMap.addMarker(fragment.markerOptions);
                markers.add(fragment.myMarker);

                fragment.getRoute(fragment.markerOptions.getPosition(), markerOptions.getPosition(), markers);
                fragment.selectedUserLatLng = latLng;
                fragment.navButton.setVisibility(View.VISIBLE);
            }
        });

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

        public void setCollectionAmount(double collectionAmount){
            amountTextView.setText(String.valueOf(collectionAmount));
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
