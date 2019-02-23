package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.CollectionDetailsActivity;
import com.icubed.loansticdroid.activities.LoginActivity;
import com.icubed.loansticdroid.activities.ResetPasswordActivity;
import com.icubed.loansticdroid.models.DueCollectionDetails;

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

        String firstName = collectionList.get(position).getFirstName();
        String lastName = collectionList.get(position).getLastName();
        holder.setCollectionName(firstName, lastName);
        holder.setCollectionAmount(collectionList.get(position).getDueAmount());
        holder.setBusiness(collectionList.get(position).getBusinessName());

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
                context.startActivity(intent);
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

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setCollectionName(String firstName, String lastName){

            collectionNameTextView = mView.findViewById(R.id.collectionNameTextView);
            detailsTextView = mView.findViewById(R.id.detailsTextView);

            collectionNameTextView.setText(lastName + " " + firstName);

        }

        public void setCollectionAmount(double collectionAmount){
            amountTextView = mView.findViewById(R.id.amountTextView);
            amountTextView.setText(String.valueOf(collectionAmount));
        }

        public void setBusiness(String businessName) {
            jobTextView = mView.findViewById(R.id.jobTextView);
            jobTextView.setText(businessName);
        }
    }

}
