package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowerCloudDetails;

import java.util.List;

public class BorrowerRecyclerAdapter extends RecyclerView.Adapter<BorrowerRecyclerAdapter.ViewHolder> {

    List<BorrowerCloudDetails> borrowersTableList;
    Context context;

    public BorrowerRecyclerAdapter(List<BorrowerCloudDetails> borrowersTableList) {
        this.borrowersTableList = borrowersTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.borrower_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return borrowersTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }
    }

}
