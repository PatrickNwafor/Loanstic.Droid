package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.AddGroupBorrower;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.models.SelectedBorrowerForGroup;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;

public class SelectedBorrowerForGroupRecyclerAdapter extends RecyclerView.Adapter<SelectedBorrowerForGroupRecyclerAdapter.ViewHolder> {

    List<SelectedBorrowerForGroup> borrowersTableList;
    Context context;

    public SelectedBorrowerForGroupRecyclerAdapter(List<SelectedBorrowerForGroup> borrowersTableList) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setViews(borrowersTableList.get(position));

        Log.d(TAG, "onBindViewHolder: "+borrowersTableList.size());
        if(borrowersTableList.size() > 0) ((AddGroupBorrower) context).register.setVisible(true);

        holder.removeBorrower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrowersTableList.get(position).getSelectedImageView().setVisibility(View.GONE);
                borrowersTableList.remove(position);
                //hide next button when selected borrower list is empty
                if((borrowersTableList.isEmpty())) ((AddGroupBorrower) context).register.setVisible(false);
                ((AddGroupBorrower) context).selectedBorrowerForGroupRecyclerAdapter.notifyDataSetChanged();
                ((AddGroupBorrower) context).groupBorrowerListRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return borrowersTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CircleImageView imageView;
        public TextView nameTextView;
        public ImageView removeBorrower;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            imageView = mView.findViewById(R.id.image_view);
            nameTextView = mView.findViewById(R.id.borrower_name);
            removeBorrower = mView.findViewById(R.id.removeImage);
        }

        public void setViews(SelectedBorrowerForGroup borrowersTable){

            nameTextView.setText(borrowersTable.getLastName() + " " + borrowersTable.getFirstName());

            if(borrowersTable.getImageByteArray() == null) {
                RequestOptions placeholderOption = new RequestOptions();
                placeholderOption.placeholder(R.drawable.person_image);

                BitmapUtil.getImageAndThumbnailWithRequestOptionsGlide(mView.getContext(), borrowersTable.getImageUri(), borrowersTable.getImageThumbUri(), placeholderOption).into(imageView);
            }else{
                imageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(borrowersTable.getImageByteArray()));
            }
        }
    }
}
