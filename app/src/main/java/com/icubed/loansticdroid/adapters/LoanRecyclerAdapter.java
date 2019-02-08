package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoanRecyclerAdapter extends RecyclerView.Adapter<LoanRecyclerAdapter.ViewHolder> {

    List<LoansTable> loansTableList;
    Context context;

    public LoanRecyclerAdapter(List<LoansTable> loansTableList) {
        this.loansTableList = loansTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
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
        public CircleImageView borrowerImageView, expandedImage;
        public TextView borrowerNameTextView, loanTypeName, expandedBorrowerNameTextView, expandedLoanTypeName;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            borrowerImageView = mView.findViewById(R.id.loan_type_image);
            expandedImage = mView.findViewById(R.id.loan_type_image1);
            borrowerNameTextView = mView.findViewById(R.id.name_of_loanee);
            loanTypeName = mView.findViewById(R.id.loan_type_description);
            expandedBorrowerNameTextView = mView.findViewById(R.id.name_of_loanee1);
            expandedLoanTypeName = mView.findViewById(R.id.loan_type_description1);
        }

        public void setClosedView(){

        }
    }
}
