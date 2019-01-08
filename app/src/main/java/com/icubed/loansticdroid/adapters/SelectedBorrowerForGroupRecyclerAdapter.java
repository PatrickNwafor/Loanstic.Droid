package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedBorrowerForGroupRecyclerAdapter extends RecyclerView.Adapter<SelectedBorrowerForGroupRecyclerAdapter.ViewHolder> {

    List<BorrowersTable> borrowersTableList;
    Context context;

    public SelectedBorrowerForGroupRecyclerAdapter(List<BorrowersTable> borrowersTableList) {
        this.borrowersTableList = borrowersTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_borrowers, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setViews(borrowersTableList.get(position));
    }

    @Override
    public int getItemCount() {
        return borrowersTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CircleImageView imageView;
        public TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            imageView = itemView.findViewById(R.id.image_view);
            nameTextView = itemView.findViewById(R.id.borrower_name);
        }

        public void setViews(BorrowersTable borrowersTable){

            nameTextView.setText(borrowersTable.getLastName() + " " + borrowersTable.getFirstName());
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.person_image);

            Glide.with(mView.getContext()).applyDefaultRequestOptions(placeholderOption).load(borrowersTable.getProfileImageUri()).thumbnail(
                    Glide.with(mView.getContext()).load(borrowersTable.getProfileImageThumbUri())
            ).into(imageView);
        }
    }
}
