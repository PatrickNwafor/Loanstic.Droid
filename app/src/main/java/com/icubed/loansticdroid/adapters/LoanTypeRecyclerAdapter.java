package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.SelectLoanType;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoanTypeRecyclerAdapter extends RecyclerView.Adapter<LoanTypeRecyclerAdapter.ViewHolder> {

    List<LoanTypeTable> loanTypeTableList;
    Context context;

    public LoanTypeRecyclerAdapter(List<LoanTypeTable> loanTypeTableList) {
        this.loanTypeTableList = loanTypeTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_type_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ((SelectLoanType) context).getImage(loanTypeTableList.get(position));
        holder.setViews(loanTypeTableList.get(position));

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(holder.selectLoanTypeView.getVisibility() == View.GONE) {
                    if(((SelectLoanType) context).lastCheck != null ){
                        ((SelectLoanType) context).lastCheck.setVisibility(View.GONE);
                    }

                    holder.selectLoanTypeView.setVisibility(View.VISIBLE);
                    holder.selectLoanTypeView.playAnimation();
                    ((SelectLoanType) context).selectedLoanTypeTable = loanTypeTableList.get(position);
                    ((SelectLoanType) context).lastCheck = holder.selectLoanTypeView;
                    ((SelectLoanType) context).otherLoanCheck.setVisibility(View.GONE);

                    //to fire up onPrepareOptionMenu
                    ((SelectLoanType) context).invalidateOptionsMenu();

                }else{
                    holder.selectLoanTypeView.setVisibility(View.GONE);
                    ((SelectLoanType) context).selectedLoanTypeTable = null;
                    ((SelectLoanType) context).lastCheck = null;

                    ((SelectLoanType) context).otherLoanCheck.setVisibility(View.GONE);

                    //to fire up onPrepareOptionMenu
                    ((SelectLoanType) context).invalidateOptionsMenu();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return loanTypeTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView loanTypeImageView;
       // public ImageView  selectLoanTypeView;
       public LottieAnimationView selectLoanTypeView;
        public TextView loanTypeName, loanTypeDesc;
        public FrameLayout frameLayout;

        View mView;
        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            loanTypeDesc = mView.findViewById(R.id.loan_type_description);
            loanTypeName = mView.findViewById(R.id.loan_type_title);
            loanTypeImageView = mView.findViewById(R.id.loan_type_image);
            selectLoanTypeView = mView.findViewById(R.id.check_loan_type);
            frameLayout = mView.findViewById(R.id.loan_type_frame);
        }

        public void setViews(LoanTypeTable loanTypeTable){
            loanTypeName.setText(loanTypeTable.getLoanTypeName());
            loanTypeDesc.setText(loanTypeTable.getLoanTypeDescription());

            if(loanTypeTable.getLoanTypeImageByteArray() == null) {
                Glide.with(mView.getContext()).load(loanTypeTable.getLoanTypeImageUri()).thumbnail(Glide.with(mView.getContext()).load(loanTypeTable.getLoanTypeImageThumbUri())).into(loanTypeImageView);
            }else{
                Bitmap bitmap = BitmapUtil.getBitMapFromBytes(loanTypeTable.getLoanTypeImageByteArray());
                loanTypeImageView.setImageBitmap(bitmap);
            }
        }
    }
}
