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
import com.icubed.loansticdroid.activities.SelectGroupLeader;
import com.icubed.loansticdroid.models.SelectedBorrowerForGroup;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupLeaderRecyclerAdapter extends RecyclerView.Adapter<GroupLeaderRecyclerAdapter.ViewHolder> {

    Context context;
    List<SelectedBorrowerForGroup> selectedBorrowerForGroups;

    public GroupLeaderRecyclerAdapter(List<SelectedBorrowerForGroup> selectedBorrowerForGroups) {
        this.selectedBorrowerForGroups = selectedBorrowerForGroups;
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

        holder.setViews(selectedBorrowerForGroups.get(position));

        //check for selected group leader
        if (((SelectGroupLeader) context).selectedGroupLeader != null){
            Boolean isSelectedBefore = ((SelectGroupLeader) context).selectedGroupLeader.getBorrowersId().equals(selectedBorrowerForGroups.get(position).getBorrowersId());
            if(isSelectedBefore){
                ((SelectGroupLeader) context).lastChecked = holder.addCheckMark;
                ((SelectGroupLeader) context).selectedGroupLeader = selectedBorrowerForGroups.get(position);
                ((SelectGroupLeader) context).isNextVisible = true;
                ((SelectGroupLeader) context).invalidateOptionsMenu();
                holder.addCheckMark.setVisibility(View.VISIBLE);
            }
        }

        //borrower on click listener
        holder.borrowerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.addCheckMark.getVisibility() == View.GONE) {

                    if(((SelectGroupLeader) context).lastChecked != null ){
                        ((SelectGroupLeader) context).lastChecked.setVisibility(View.GONE);
                    }

                    ((SelectGroupLeader) context).lastChecked = holder.addCheckMark;
                    ((SelectGroupLeader) context).selectedGroupLeader = selectedBorrowerForGroups.get(position);
                    ((SelectGroupLeader) context).isNextVisible = true;
                    ((SelectGroupLeader) context).invalidateOptionsMenu();
                    holder.addCheckMark.setVisibility(View.VISIBLE);

                }else{
                    ((SelectGroupLeader) context).lastChecked = null;
                    ((SelectGroupLeader) context).selectedGroupLeader = null;
                    ((SelectGroupLeader) context).isNextVisible = false;
                    ((SelectGroupLeader) context).invalidateOptionsMenu();
                    holder.addCheckMark.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedBorrowerForGroups.size();
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

        public void setViews(SelectedBorrowerForGroup borrowersTable){

            borrowerNameEditText.setText(borrowersTable.getLastName()+" "+borrowersTable.getFirstName());
            borrowerbusinessEditText.setText(borrowersTable.getBusinessName());

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
