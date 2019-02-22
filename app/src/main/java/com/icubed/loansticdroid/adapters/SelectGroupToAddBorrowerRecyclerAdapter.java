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

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.AddBorrowerToExistingGroupActivity;
import com.icubed.loansticdroid.activities.NewLoanWizard;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;

import java.util.List;

public class SelectGroupToAddBorrowerRecyclerAdapter extends RecyclerView.Adapter<SelectGroupToAddBorrowerRecyclerAdapter.ViewHolder> {
    List<GroupBorrowerTable> groupBorrowerTables;
    Context context;

    public SelectGroupToAddBorrowerRecyclerAdapter(List<GroupBorrowerTable> groupBorrowerTables) {
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

        if(((AddBorrowerToExistingGroupActivity) context).selectedGroup != null){
            if(((AddBorrowerToExistingGroupActivity) context).selectedGroup.getGroupId().equals(groupBorrowerTables.get(position).getGroupId())){
                holder.addCheckMark.setVisibility(View.VISIBLE);
                ((AddBorrowerToExistingGroupActivity) context).invalidateOptionsMenu();
            }
        }

        holder.groupNameTextView.setText(groupBorrowerTables.get(position).getGroupName());
        holder.groupCountTextView.setText("Group members: "+groupBorrowerTables.get(position).getNumberOfGroupMembers());

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.addCheckMark.getVisibility() == View.GONE) {

                    if(((AddBorrowerToExistingGroupActivity) context).lastChecked != null ){
                        ((AddBorrowerToExistingGroupActivity) context).lastChecked.setVisibility(View.GONE);
                    }

                    ((AddBorrowerToExistingGroupActivity) context).lastChecked = holder.addCheckMark;
                    ((AddBorrowerToExistingGroupActivity) context).selectedGroup = groupBorrowerTables.get(position);
                    holder.addCheckMark.setVisibility(View.VISIBLE);
                    ((AddBorrowerToExistingGroupActivity) context).invalidateOptionsMenu();

                }else{
                    ((AddBorrowerToExistingGroupActivity) context).lastChecked = null;
                    ((AddBorrowerToExistingGroupActivity) context).selectedGroup = null;
                    holder.addCheckMark.setVisibility(View.GONE);
                    ((AddBorrowerToExistingGroupActivity) context).invalidateOptionsMenu();
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
        public ImageView addCheckMark;

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
