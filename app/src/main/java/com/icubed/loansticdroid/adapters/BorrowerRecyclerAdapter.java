package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.BorrowerDetailsSingle;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BorrowerRecyclerAdapter extends RecyclerView.Adapter<BorrowerRecyclerAdapter.ViewHolder> {

    List<BorrowersTable> borrowersTableList;
    Context context;

    public BorrowerRecyclerAdapter(List<BorrowersTable> borrowersTableList) {
        this.borrowersTableList = borrowersTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrower_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setBorrowerBusinessName(borrowersTableList.get(position).getBusinessName());
        holder.setBorrowerName(borrowersTableList.get(position).getFirstName(), borrowersTableList.get(position).getLastName());
        holder.setBorrowerImage(borrowersTableList.get(position).getProfileImageUri(), borrowersTableList.get(position).getProfileImageThumbUri());

        holder.borrowerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BorrowerDetailsSingle.class);
                intent.putExtra("borrower", borrowersTableList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return borrowersTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView borrowerNameTextView;
        public ImageView borrowerImageView;
        public TextView businessNameTextView;
        public FrameLayout borrowerFrame;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setBorrowerName(String firstName, String lastName){
            borrowerNameTextView = mView.findViewById(R.id.borrower_name);
            borrowerFrame = mView.findViewById(R.id.borrower_frame);
            borrowerNameTextView.setText(lastName +" "+firstName);
        }

        public void setBorrowerImage(String imageUri, String imagethumbUri){
            borrowerImageView = mView.findViewById(R.id.borrower_image);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.person_image);

            Glide.with(mView.getContext()).applyDefaultRequestOptions(placeholderOption).load(imageUri).thumbnail(
                    Glide.with(mView.getContext()).load(imagethumbUri)
            ).into(borrowerImageView);
        }

        public void setBorrowerBusinessName(String businessName){
            businessNameTextView = mView.findViewById(R.id.borrower_business);

            businessNameTextView.setText(businessName);
        }
    }

}
