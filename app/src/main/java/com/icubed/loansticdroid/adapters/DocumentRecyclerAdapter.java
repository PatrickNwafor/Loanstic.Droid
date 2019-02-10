package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.PictureViewActivity;
import com.icubed.loansticdroid.localdatabase.BorrowerFilesTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;

public class DocumentRecyclerAdapter extends RecyclerView.Adapter<DocumentRecyclerAdapter.ViewHolder> {

    List<BorrowerFilesTable> borrowerFilesTables;
    Context context;

    public DocumentRecyclerAdapter(List<BorrowerFilesTable> borrowerFilesTables) {
        this.borrowerFilesTables = borrowerFilesTables;
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
        holder.setViews(borrowerFilesTables.get(position));

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(context, PictureViewActivity.class);
                pictureIntent.putExtra("file", borrowerFilesTables.get(position));
                pictureIntent.putExtra("file_byte", borrowerFilesTables.get(position).getImageByteArray());
                context.startActivity(pictureIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return borrowerFilesTables.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView fileDescTextView;
        public ImageView fileImageView;
        public FrameLayout frameLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            fileDescTextView = mView.findViewById(R.id.file_desc);
            fileImageView = mView.findViewById(R.id.file_image);
            frameLayout = mView.findViewById(R.id.file_frame);
        }

        public void setViews(BorrowerFilesTable borrowerFilesTable){

            fileDescTextView.setText(borrowerFilesTable.getFileDescription());

            if(borrowerFilesTable.getImageByteArray() == null) {
                BitmapUtil.getImageAndThumbnailWithGlide(mView.getContext(), borrowerFilesTable.getFileImageUri(), borrowerFilesTable.getFileImageUriThumb()).into(fileImageView);
            }else{
                fileImageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(borrowerFilesTable.getImageByteArray()));
            }

        }
    }

}
