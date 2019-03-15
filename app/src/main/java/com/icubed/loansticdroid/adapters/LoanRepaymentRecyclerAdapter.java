package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.LoanPaymentActivity;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.models.LoanDetails;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoanRepaymentRecyclerAdapter extends RecyclerView.Adapter<LoanRepaymentRecyclerAdapter.ViewHolder> {

    List<LoanDetails> loanDetailsList;
    Context context;

    public LoanRepaymentRecyclerAdapter(List<LoanDetails> loanDetailsList) {
        this.loanDetailsList = loanDetailsList;
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
        holder.setViews(loanDetailsList.get(position));

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoanPaymentActivity.class);
                intent.putExtra("borrower", loanDetailsList.get(position).getBorrowersTable());
                intent.putExtra("group", loanDetailsList.get(position).getGroupBorrowerTable());
                intent.putExtra("loan", loanDetailsList.get(position).getLoansTable());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return loanDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public FrameLayout frameLayout;
        private TextView loanId, borrowerName, repaymentProgress;
        private CircleImageView borrowerImage;
        private android.widget.ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            frameLayout = mView.findViewById(R.id.frame);
            loanId = mView.findViewById(R.id.loan_id);
            borrowerName = mView.findViewById(R.id.name_of_loanee);
            repaymentProgress = mView.findViewById(R.id.repayment_progress_text_view);
            borrowerImage = mView.findViewById(R.id.loan_type_image);
            progressBar = mView.findViewById(R.id.progressBarLoan);
        }

        public void setViews(LoanDetails loanDetails) {
            BorrowersTable borrowersTable = loanDetails.getBorrowersTable();

            borrowerName.setText(borrowersTable.getLastName() + " " + borrowersTable.getFirstName());

            if(borrowersTable.getBorrowerImageByteArray() == null){
                BitmapUtil.getImageAndThumbnailWithGlide(
                        mView.getContext(),
                        borrowersTable.getProfileImageUri(),
                        borrowersTable.getProfileImageThumbUri()
                ).into(borrowerImage);
            }else{
                borrowerImage.setImageBitmap(BitmapUtil.getBitMapFromBytes(borrowersTable.getBorrowerImageByteArray()));
            }

            LoansTable loansTable = loanDetails.getLoansTable();

            double totalRepayment = loansTable.getLoanAmount()*(1+(loansTable.getLoanInterestRate()/100));
            double loanRepaymentRatio = 1 - ((totalRepayment - loansTable.getRepaymentMade())/(totalRepayment));
            double loanRepaymentPercent = loanRepaymentRatio * 100;
            double roundOff = Math.round(loanRepaymentPercent * 100.0) / 100.0;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress((int) roundOff, true);
            }else{
                progressBar.setProgress((int) roundOff);
            }

            repaymentProgress.setText(String.valueOf(roundOff) + "%");
            loanId.setText(loansTable.getLoanNumber());
        }
    }

}
