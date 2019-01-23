package com.icubed.loansticdroid.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowerFilesTable;

import java.util.List;

public class DocumentRecyclerAdapter extends RecyclerView.Adapter<DocumentRecyclerAdapter.ViewHolder> {

    List<BorrowerFilesTable> borrowerFilesTables;

    public DocumentRecyclerAdapter(List<BorrowerFilesTable> borrowerFilesTables) {
        this.borrowerFilesTables = borrowerFilesTables;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_file_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setViews(borrowerFilesTables.get(position));
    }

    @Override
    public int getItemCount() {
        return borrowerFilesTables.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView fileDescTextView;
        public ImageView fileImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            fileDescTextView = mView.findViewById(R.id.file_desc);
            fileImageView = mView.findViewById(R.id.file_image);
        }

        public void setViews(BorrowerFilesTable borrowerFilesTable){

            fileDescTextView.setText(borrowerFilesTable.getFileDescription());

            Glide.with(mView.getContext()).load(borrowerFilesTable.getFileImageUri()).thumbnail(
                    Glide.with(mView.getContext()).load(borrowerFilesTable.getFileImageUriThumb())
            ).into(fileImageView);

        }
    }

}
