package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.icubed.loansticdroid.localdatabase.LoansTable;

import java.util.List;

public class LoanRecyclerAdapter extends RecyclerView.Adapter<LoanRecyclerAdapter.ViewHolder> {

    List<LoansTable> loansTableList;
    Context context;

    public LoanRecyclerAdapter(List<LoansTable> loansTableList) {
        this.loansTableList = loansTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return loansTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }
    }
}
