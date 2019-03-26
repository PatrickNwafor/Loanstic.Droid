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

import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.ViewCollection;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.models.LoanDetails;
import com.icubed.loansticdroid.models.SavingsDetails;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SavingsRecyclerAdapter extends RecyclerView.Adapter<SavingsRecyclerAdapter.ViewHolder> {
    
    List<SavingsDetails> savingsDetailsList;
    Context context;

    public SavingsRecyclerAdapter(List<SavingsDetails> savingsDetailsList) {
        this.savingsDetailsList = savingsDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_repayments_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setViews(savingsDetailsList.get(position));

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewCollection.class);
                intent.putExtra("borrower", savingsDetailsList.get(position).getBorrowersTable());
                intent.putExtra("savings", savingsDetailsList.get(position).getSavingsTable());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return savingsDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public FrameLayout frameLayout;
        private TextView loanId, borrowerName, repaymentProgress;
        private CircleImageView borrowerImage;
        private AnimateHorizontalProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            frameLayout = mView.findViewById(R.id.frame);
            loanId = mView.findViewById(R.id.loan_id);
            borrowerName = mView.findViewById(R.id.name_of_loanee);
            borrowerImage = mView.findViewById(R.id.loan_type_image);
            progressBar = mView.findViewById(R.id.progressBarLoan1);
            repaymentProgress = mView.findViewById(R.id.repayment_progress_text_view);
        }

        public void setViews(SavingsDetails savingsDetails) {

            repaymentProgress.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            BorrowersTable borrowersTable = savingsDetails.getBorrowersTable();
            borrowerName.setText(borrowersTable.getLastName() + " " + borrowersTable.getFirstName());
            if (borrowersTable.getBorrowerImageByteArray() == null) {
                BitmapUtil.getImageAndThumbnailWithGlide(mView.getContext(), borrowersTable.getProfileImageUri(), borrowersTable.getProfileImageThumbUri()).into(borrowerImage);
            } else {
                borrowerImage.setImageBitmap(BitmapUtil.getBitMapFromBytes(borrowersTable.getBorrowerImageByteArray()));
            }

            SavingsTable savingsTable = savingsDetails.getSavingsTable();
            loanId.setText(savingsTable.getAccountNumber());
        }
    }
}
