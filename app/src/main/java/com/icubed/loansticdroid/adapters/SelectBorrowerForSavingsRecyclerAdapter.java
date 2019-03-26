package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.request.RequestOptions;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.SelectBorrowerForSavingsActivity;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectBorrowerForSavingsRecyclerAdapter extends RecyclerView.Adapter<SelectBorrowerForSavingsRecyclerAdapter.ViewHolder> {
    Context context;
    private List<BorrowersTable> borrowersTableList;

    public SelectBorrowerForSavingsRecyclerAdapter(List<BorrowersTable> borrowersTableList) {
        this.borrowersTableList = borrowersTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_borrower_list_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setViews(borrowersTableList.get(position));

        //check for selected group leader
        if (((SelectBorrowerForSavingsActivity) context).selectedBorrower != null){
            Boolean isSelectedBefore = ((SelectBorrowerForSavingsActivity) context).selectedBorrower.getBorrowersId().equals(borrowersTableList.get(position).getBorrowersId());
            if(isSelectedBefore){
                ((SelectBorrowerForSavingsActivity) context).lastChecked = holder.addCheckMark;
                ((SelectBorrowerForSavingsActivity) context).selectedBorrower = borrowersTableList.get(position);
                ((SelectBorrowerForSavingsActivity) context).invalidateOptionsMenu();
                holder.addCheckMark.setVisibility(View.VISIBLE);
            }
        }

        //borrower on click listener
        holder.borrowerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.addCheckMark.getVisibility() == View.GONE) {

                    if(((SelectBorrowerForSavingsActivity) context).lastChecked != null ){
                        ((SelectBorrowerForSavingsActivity) context).lastChecked.setVisibility(View.GONE);
                    }

                    ((SelectBorrowerForSavingsActivity) context).lastChecked = holder.addCheckMark;
                    ((SelectBorrowerForSavingsActivity) context).selectedBorrower = borrowersTableList.get(position);
                    ((SelectBorrowerForSavingsActivity) context).invalidateOptionsMenu();
                    holder.addCheckMark.setVisibility(View.VISIBLE);
                    holder.addCheckMark.playAnimation();

                }else{
                    ((SelectBorrowerForSavingsActivity) context).lastChecked = null;
                    ((SelectBorrowerForSavingsActivity) context).selectedBorrower = null;
                    ((SelectBorrowerForSavingsActivity) context).invalidateOptionsMenu();
                    holder.addCheckMark.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return borrowersTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView borrowerNameEditText;
        public TextView borrowerbusinessEditText;
        public CircleImageView imageView;
        public FrameLayout borrowerFrame;
        public LottieAnimationView addCheckMark;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            borrowerbusinessEditText = mView.findViewById(R.id.borrower_busi);
            borrowerNameEditText = mView.findViewById(R.id.borrower_name);
            imageView = mView.findViewById(R.id.borrower_image);
            borrowerFrame = mView.findViewById(R.id.borrower_frame);
            addCheckMark = mView.findViewById(R.id.removeImage);
        }

        public void setViews(BorrowersTable borrowersTable){

            borrowerNameEditText.setText(borrowersTable.getLastName()+" "+borrowersTable.getFirstName());
            borrowerbusinessEditText.setText(borrowersTable.getBusinessName());

            if(borrowersTable.getBorrowerImageByteArray() == null) {
                RequestOptions placeholderOption = new RequestOptions();
                placeholderOption.placeholder(R.drawable.person_image);

                BitmapUtil.getImageAndThumbnailWithRequestOptionsGlide(mView.getContext(), borrowersTable.getProfileImageUri(), borrowersTable.getProfileImageThumbUri(), placeholderOption).into(imageView);
            }else{
                imageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(borrowersTable.getBorrowerImageByteArray()));
            }
        }
    }
}
