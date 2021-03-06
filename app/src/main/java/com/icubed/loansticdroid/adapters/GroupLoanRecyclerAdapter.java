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

import com.airbnb.lottie.LottieAnimationView;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.BorrowerActivity;
import com.icubed.loansticdroid.activities.BorrowerDetailsGroup;
import com.icubed.loansticdroid.activities.NewLoanWizard;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;

import java.util.List;

public class GroupLoanRecyclerAdapter extends RecyclerView.Adapter<GroupLoanRecyclerAdapter.ViewHolder> {

    List<GroupBorrowerTable> groupBorrowerTables;
    Context context;

    public GroupLoanRecyclerAdapter(List<GroupBorrowerTable> groupBorrowerTables) {
        this.groupBorrowerTables = groupBorrowerTables;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.groupNameTextView.setText(groupBorrowerTables.get(position).getGroupName());
        holder.groupCountTextView.setText("Group members: "+groupBorrowerTables.get(position).getNumberOfGroupMembers());

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.addCheckMark.getVisibility() == View.GONE) {

                    if(((NewLoanWizard) context).lastChecked != null ){
                        ((NewLoanWizard) context).lastChecked.setVisibility(View.GONE);
                    }

                    ((NewLoanWizard) context).lastChecked = holder.addCheckMark;
                    ((NewLoanWizard) context).selectedGroup = groupBorrowerTables.get(position);
                    ((NewLoanWizard) context).selectedBorrower = null;
                    holder.addCheckMark.setVisibility(View.VISIBLE);
                    holder.addCheckMark.playAnimation();
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
        return groupBorrowerTables.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView groupNameTextView, groupCountTextView;
        public FrameLayout frameLayout;
        //public ImageView addCheckMark;
        public LottieAnimationView addCheckMark;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            groupNameTextView = mView.findViewById(R.id.group_name);
            groupCountTextView = mView.findViewById(R.id.group_members_count);
            frameLayout = mView.findViewById(R.id.group_frame);
            addCheckMark = mView.findViewById(R.id.select_btn);
        }
    }
}
