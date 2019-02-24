package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.CollectionDetailsActivity;
import com.icubed.loansticdroid.activities.LoginActivity;
import com.icubed.loansticdroid.activities.MainActivity;
import com.icubed.loansticdroid.activities.ResetPasswordActivity;
import com.icubed.loansticdroid.models.Collection;
import com.icubed.loansticdroid.models.DueCollectionDetails;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class SlideUpPanelRecyclerAdapter extends RecyclerView.Adapter<SlideUpPanelRecyclerAdapter.ViewHolder> {

    List<DueCollectionDetails> collectionList;
    Context context;

    public SlideUpPanelRecyclerAdapter(List<DueCollectionDetails> collectionList) {
        this.collectionList = collectionList;
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
                MarkerOptions markerOptions = new MarkerOptions();

                LatLng latLng = new LatLng(collectionList.get(position).getLatitude(), collectionList.get(position).getLongitude());
                //adding markerOptions properties for driver
                markerOptions.position(latLng);
                markerOptions.anchor(0.5f, 0.5f);
                if(collectionList.get(position).getGroupName() == null){
                    markerOptions.title(lastName + " " + firstName);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                }else {
                    markerOptions.title(groupName);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                }

                ((MainActivity) context).drawMarker(markerOptions);
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
                collectionImageView.setVisibility(View.GONE);
            }
        }
    }

}
