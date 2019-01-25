package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.BorrowerDetailsGroup;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;

import java.util.List;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder> {

    List<GroupBorrowerTable> groupBorrowerTables;
    Context context;

    public GroupRecyclerAdapter(List<GroupBorrowerTable> groupBorrowerTables) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.groupNameTextView.setText(groupBorrowerTables.get(position).getGroupName());
        holder.groupCountTextView.setText("Group members: "+groupBorrowerTables.get(position).getNumberOfGroupMembers());

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BorrowerDetailsGroup.class);
                intent.putExtra("group", groupBorrowerTables.get(position));
                context.startActivity(intent);
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

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            groupNameTextView = mView.findViewById(R.id.group_name);
            groupCountTextView = mView.findViewById(R.id.group_members_count);
            frameLayout = mView.findViewById(R.id.group_frame);
        }
    }

}
