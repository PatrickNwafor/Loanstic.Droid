package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.NewLoanWizard;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingleLoanRecyclerAdapter extends RecyclerView.Adapter<SingleLoanRecyclerAdapter.ViewHolder> {

    List<BorrowersTable> borrowersTableList;
    Context context;

    public SingleLoanRecyclerAdapter(List<BorrowersTable> borrowersTableList) {
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

        holder.borrowerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.addCheckMark.getVisibility() == View.GONE) {

                    if(((NewLoanWizard) context).lastChecked != null ){
                        ((NewLoanWizard) context).lastChecked.setVisibility(View.GONE);
                    }

                    ((NewLoanWizard) context).lastChecked = holder.addCheckMark;
                    ((NewLoanWizard) context).selectedBorrower = borrowersTableList.get(position);
                    ((NewLoanWizard) context).selectedGroup = null;
                    holder.addCheckMark.setVisibility(View.VISIBLE);
                    ((NewLoanWizard) context).invalidateOptionsMenu();

                }else{
                    ((NewLoanWizard) context).lastChecked = null;
                    ((NewLoanWizard) context).selectedBorrower = null;
                    ((NewLoanWizard) context).selectedGroup = null;
                    holder.addCheckMark.setVisibility(View.GONE);
                    ((NewLoanWizard) context).invalidateOptionsMenu();
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
        public ImageView addCheckMark;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            borrowerbusinessEditText = mView.findViewById(R.id.borrower_busi);
            borrowerNameEditText = mView.findViewById(R.id.borrower_name);
            imageView = mView.findViewById(R.id.borrower_image);
            borrowerFrame = mView.findViewById(R.id.borrower_frame);
            addCheckMark = mView.findViewById(R.id.removeImage);
        }

        public void setViews(BorrowersTable borrowersTable) {
            borrowerNameEditText.setText(borrowersTable.getLastName()+" "+borrowersTable.getFirstName());
            borrowerbusinessEditText.setText(borrowersTable.getBusinessName());

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.person_image);

            Glide.with(mView.getContext()).applyDefaultRequestOptions(placeholderOption).load(borrowersTable.getProfileImageUri()).thumbnail(
                    Glide.with(mView.getContext()).load(borrowersTable.getProfileImageThumbUri())
            ).into(imageView);
        }
    }
}
