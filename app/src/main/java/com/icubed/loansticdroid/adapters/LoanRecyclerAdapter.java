package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.AllBorrowerLoan;
import com.icubed.loansticdroid.activities.LoginActivity;
import com.icubed.loansticdroid.activities.ResetPasswordActivity;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.models.LoanDetails;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.DateUtil;
import com.ramotion.foldingcell.FoldingCell;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoanRecyclerAdapter extends RecyclerView.Adapter<LoanRecyclerAdapter.ViewHolder> {

    List<LoanDetails> loansTableList;
    Context context;

    public LoanRecyclerAdapter(List<LoanDetails> loansTableList) {
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
        holder.setClosedView(loansTableList.get(position));

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent allLoan = new Intent(context, AllBorrowerLoan.class);
               context.startActivity(allLoan);
                //Toast.makeText(context, "You clicked me", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return loansTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CircleImageView borrowerImageView, expandedImage;
        public TextView borrowerNameTextView, loanTypeName, expandedBorrowerNameTextView, expandedLoanTypeName
                , principalTextView, releaseDateTextView, maturityTextView, collection_text_view
                , collectionDueTextView, amountPaidTextView, balanceTextView, lastCollectionTextView;
        public Button moreBtn;
        // get our folding cell
        public FoldingCell fc;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            borrowerImageView = mView.findViewById(R.id.loan_type_image);
            expandedImage = mView.findViewById(R.id.loan_type_image1);
            borrowerNameTextView = mView.findViewById(R.id.name_of_loanee);
            loanTypeName = mView.findViewById(R.id.loan_type_description);
            expandedBorrowerNameTextView = mView.findViewById(R.id.name_of_loanee1);
            expandedLoanTypeName = mView.findViewById(R.id.loan_type_description1);
            principalTextView = mView.findViewById(R.id.principal_text_view);
            releaseDateTextView = mView.findViewById(R.id.released_date_text_view);
            maturityTextView = mView.findViewById(R.id.maturity_text_view);
            collection_text_view = mView.findViewById(R.id.collection_text_view);
            collectionDueTextView = mView.findViewById(R.id.collection_due_text_view);
            amountPaidTextView = mView.findViewById(R.id.amount_paid_text_view);
            balanceTextView = mView.findViewById(R.id.balance_text_view);
            lastCollectionTextView = mView.findViewById(R.id.last_collection_text_view);
            moreBtn = mView.findViewById(R.id.loan_button);

            fc =  itemView.findViewById(R.id.folding_cell);

            // attach click listener to folding cell
            fc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fc.toggle(false);
                }
            });
        }

        public void setClosedView(LoanDetails loanDetails){
            if(loanDetails.getBorrowersTable() != null){
                borrowerNameTextView.setText(loanDetails.getBorrowersTable().getLastName()+" "+loanDetails.getBorrowersTable().getFirstName());
                expandedBorrowerNameTextView.setText(loanDetails.getBorrowersTable().getLastName()+" "+loanDetails.getBorrowersTable().getFirstName());


                if(loanDetails.getBorrowersTable().getBorrowerImageByteArray() == null) {
                    Glide.with(mView.getContext()).load(loanDetails.getBorrowersTable().getProfileImageUri()).thumbnail(Glide.with(mView.getContext()).load(loanDetails.getBorrowersTable().getProfileImageThumbUri())).into(borrowerImageView);

                    Glide.with(mView.getContext()).load(loanDetails.getBorrowersTable().getProfileImageUri()).thumbnail(Glide.with(mView.getContext()).load(loanDetails.getBorrowersTable().getProfileImageThumbUri())).into(expandedImage);
                }else{
                    borrowerImageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(loanDetails.getBorrowersTable().getBorrowerImageByteArray()));
                    expandedImage.setImageBitmap(BitmapUtil.getBitMapFromBytes(loanDetails.getBorrowersTable().getBorrowerImageByteArray()));
                }

            }else {
                borrowerNameTextView.setText(loanDetails.getGroupBorrowerTable().getGroupName());
                expandedBorrowerNameTextView.setText(loanDetails.getGroupBorrowerTable().getGroupName());
            }

            if(loanDetails.getLoanTypeTable() != null) {
                loanTypeName.setText(loanDetails.getLoanTypeTable().getLoanTypeName());
                expandedLoanTypeName.setText(loanDetails.getLoanTypeTable().getLoanTypeName());
            }else{
                loanTypeName.setText(loanDetails.getOtherLoanTypesTable().getOtherLoanTypeName());
                expandedLoanTypeName.setText(loanDetails.getOtherLoanTypesTable().getOtherLoanTypeName());
            }

            principalTextView.setText(String.valueOf(loanDetails.getLoansTable().getLoanAmount()));
            releaseDateTextView.setText(DateUtil.dateString(loanDetails.getLoansTable().getLoanReleaseDate()));
            collection_text_view.setText(loanDetails.getLoansTable().getRepaymentAmountUnit());
            collectionDueTextView.setText(String.valueOf(loanDetails.getLoansTable().getRepaymentAmount()));

            String maturityDate = getMaturityDate(loanDetails.getLoansTable());
            maturityTextView.setText(maturityDate);

        }

        private String getMaturityDate(LoansTable loansTable) {
            if(loansTable.getLoanDurationUnit().equals("year")){
                Date date = DateUtil.addYear(loansTable.getLoanCreationDate(), loansTable.getLoanDuration());
                return DateUtil.dateString(date);
            }else if(loansTable.getLoanDurationUnit().equals("month")){
                Date date = DateUtil.addMonth(loansTable.getLoanCreationDate(), loansTable.getLoanDuration());
                return DateUtil.dateString(date);
            }else if(loansTable.getLoanDurationUnit().equals("week")){
                Date date = DateUtil.addDay(loansTable.getLoanCreationDate(), loansTable.getLoanDuration()*7);
                return DateUtil.dateString(date);
            }else{
                Date date = DateUtil.addDay(loansTable.getLoanCreationDate(), loansTable.getLoanDuration());
                return DateUtil.dateString(date);
            }
        }
    }
}
