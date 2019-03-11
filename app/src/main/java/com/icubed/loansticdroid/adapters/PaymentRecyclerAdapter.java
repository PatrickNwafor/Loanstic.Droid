package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.PictureViewActivity;
import com.icubed.loansticdroid.localdatabase.BorrowerFilesTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PaymentRecyclerAdapter extends RecyclerView.Adapter<PaymentRecyclerAdapter.ViewHolder> {

    List<Bitmap> bitmapList;
    Context context;


    public PaymentRecyclerAdapter(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
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
        holder.fileDescTextView.setVisibility(View.GONE);
        holder.fileImageView.setImageBitmap(bitmapList.get(position));
        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(context, PictureViewActivity.class);
                pictureIntent.putExtra("payment", bitmapList.get(position));
                context.startActivity(pictureIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public TextView fileDescTextView;
        public CircleImageView fileImageView;
        public FrameLayout frameLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            fileDescTextView = mView.findViewById(R.id.file_desc);
            fileImageView = mView.findViewById(R.id.file_image);
            frameLayout = mView.findViewById(R.id.file_frame);
        }
    }
}
