package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.PictureViewActivity;
import com.icubed.loansticdroid.localdatabase.BorrowerPhotoValidationTable;
import com.icubed.loansticdroid.localdatabase.GroupPhotoValidationTable;

import java.util.List;

public class BorrowerBusinessVerificationRecyclerAdapter extends RecyclerView.Adapter<BorrowerBusinessVerificationRecyclerAdapter.ViewHolder> {

    List<BorrowerPhotoValidationTable> borrowerPhotoValidationTables;
    Context context;

    public BorrowerBusinessVerificationRecyclerAdapter(List<BorrowerPhotoValidationTable> borrowerPhotoValidationTables) {
        this.borrowerPhotoValidationTables = borrowerPhotoValidationTables;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_file_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setViews(borrowerPhotoValidationTables.get(position));

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(context, PictureViewActivity.class);
                pictureIntent.putExtra("borrower_photo_valid", borrowerPhotoValidationTables.get(position));
                context.startActivity(pictureIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return borrowerPhotoValidationTables.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView verificationTextView;
        public ImageView verificationImageView;
        public FrameLayout frameLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            verificationTextView = mView.findViewById(R.id.file_desc);
            verificationImageView = mView.findViewById(R.id.file_image);
            frameLayout = mView.findViewById(R.id.file_frame);
        }

        public void setViews(BorrowerPhotoValidationTable groupPhotoValidationTable){

            verificationTextView.setVisibility(View.GONE);

            Glide.with(mView.getContext()).load(groupPhotoValidationTable.getPhotoUri()).thumbnail(
                    Glide.with(mView.getContext()).load(groupPhotoValidationTable.getPhotoThumbUri())
            ).into(verificationImageView);

        }
    }

}
