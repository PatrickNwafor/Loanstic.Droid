package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.AddGroupBorrower;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.models.SelectedBorrowerForGroup;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupBorrowerListRecyclerAdapter extends RecyclerView.Adapter<GroupBorrowerListRecyclerAdapter.ViewHolder> {

    List<BorrowersTable> borrowersTableList;
    Context context;
    SelectedBorrowerForGroup selectedBorrower;

    public GroupBorrowerListRecyclerAdapter(List<BorrowersTable> borrowersTableList) {
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

        if(((AddGroupBorrower) context).alreadyAddedBorrower == null) {
            loadUI(holder, position);
        }else{
            for (BorrowersTable borrowersTable : ((AddGroupBorrower) context).alreadyAddedBorrower) {
                if(borrowersTableList.get(position).getBorrowersId().equals(borrowersTable.getBorrowersId())){
                    return;
                }
            }
            loadUI(holder, position);
        }
    }

    private void loadUI(final ViewHolder holder, final int position){
        holder.setViews(borrowersTableList.get(position));

        //to check if borrower is already added incase the user uses the search field to find the borrower
        holder.addOrRemoveCheck(((AddGroupBorrower) context).selectedBorrowerList, borrowersTableList.get(position).getBorrowersId());

        holder.borrowerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting up selected borrowers table
                SelectedBorrowerForGroup selectedBorrowerForGroup = new SelectedBorrowerForGroup();
                selectedBorrowerForGroup.setBorrowersId(borrowersTableList.get(position).getBorrowersId());
                selectedBorrowerForGroup.setFirstName(borrowersTableList.get(position).getFirstName());
                selectedBorrowerForGroup.setLastName(borrowersTableList.get(position).getLastName());
                selectedBorrowerForGroup.setBusinessName(borrowersTableList.get(position).getBusinessName());
                selectedBorrowerForGroup.setImageThumbUri(borrowersTableList.get(position).getProfileImageThumbUri());
                selectedBorrowerForGroup.setImageUri(borrowersTableList.get(position).getProfileImageUri());
                selectedBorrowerForGroup.setBelongsToGroup(borrowersTableList.get(position).getBelongsToGroup());
                selectedBorrowerForGroup.setSelectedImageView(holder.addCheckMark);

                Boolean isBorrowerAlreadyAdded = false;
                for (SelectedBorrowerForGroup borrowerForGroup : ((AddGroupBorrower) context).selectedBorrowerList) {
                    if (borrowerForGroup.getBorrowersId().equals(borrowersTableList.get(position).getBorrowersId())) {
                        isBorrowerAlreadyAdded = true;
                        selectedBorrower = borrowerForGroup;
                        break;
                    }
                }

                if (!isBorrowerAlreadyAdded) {
                    holder.addCheckMark.setVisibility(View.VISIBLE);
                    ((AddGroupBorrower) context).selectedBorrowerList.add(0, selectedBorrowerForGroup);
                    ((AddGroupBorrower) context).selectedBorrowerForGroupRecyclerAdapter.notifyDataSetChanged();
                } else {
                    holder.addCheckMark.setVisibility(View.GONE);
                    ((AddGroupBorrower) context).selectedBorrowerList.remove(selectedBorrower);
                    ((AddGroupBorrower) context).selectedBorrowerForGroupRecyclerAdapter.notifyDataSetChanged();
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

        public void setViews(BorrowersTable borrowersTable){

            borrowerNameEditText.setText(borrowersTable.getLastName()+" "+borrowersTable.getFirstName());
            borrowerbusinessEditText.setText(borrowersTable.getBusinessName());

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.person_image);

            Glide.with(mView.getContext()).applyDefaultRequestOptions(placeholderOption).load(borrowersTable.getProfileImageUri()).thumbnail(
                    Glide.with(mView.getContext()).load(borrowersTable.getProfileImageThumbUri())
            ).into(imageView);
        }

        public void addOrRemoveCheck(List<SelectedBorrowerForGroup> selectedBorrowerList, String borrowersId){
            addCheckMark.setVisibility(View.GONE);
            for(SelectedBorrowerForGroup borrowerForGroup : selectedBorrowerList){
                if(borrowerForGroup.getBorrowersId().equals(borrowersId)){
                    addCheckMark.setVisibility(View.VISIBLE);
                    Log.d("BorrowerSelected", selectedBorrowerList.toString());
                    break;
                }
            }
        }
    }
}
