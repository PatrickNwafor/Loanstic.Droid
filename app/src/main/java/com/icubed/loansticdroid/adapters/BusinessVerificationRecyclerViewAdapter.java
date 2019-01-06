package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.BusinessVerification;

import java.util.ArrayList;

public class BusinessVerificationRecyclerViewAdapter extends RecyclerView.Adapter<BusinessVerificationRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "BusinessVerAdapter";

    //vars

    private ArrayList<Bitmap> mImageBitmap;
    private Context mContext;

    public BusinessVerificationRecyclerViewAdapter(ArrayList<Bitmap> mImageBitmap) {
        this.mImageBitmap = mImageBitmap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_images, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.image.setImageBitmap(mImageBitmap.get(position));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on an image: ");
                ((BusinessVerification) mContext).showBigImage(mImageBitmap.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageBitmap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);

        }
    }
}