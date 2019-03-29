package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.SavingsPlanLifeGoals;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SavingsPlanTypeRecyclerAdapter extends RecyclerView.Adapter<SavingsPlanTypeRecyclerAdapter.ViewHolder> {
    List<SavingsPlanTypeTable> savingsPlanTypeTableList;
    Context context;

    public SavingsPlanTypeRecyclerAdapter(List<SavingsPlanTypeTable> savingsPlanTypeTableList) {
        this.savingsPlanTypeTableList = savingsPlanTypeTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.savings_life_goal_type_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ((SavingsPlanLifeGoals) context).getImage(savingsPlanTypeTableList.get(position));
        holder.setViews(savingsPlanTypeTableList.get(position));

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(holder.selectLoanTypeView.getVisibility() == View.GONE) {
                    if(((SavingsPlanLifeGoals) context).lastCheck != null ){
                        ((SavingsPlanLifeGoals) context).lastCheck.setVisibility(View.GONE);
                    }

                    holder.selectLoanTypeView.setVisibility(View.VISIBLE);
                    holder.selectLoanTypeView.playAnimation();
                    ((SavingsPlanLifeGoals) context).selectedSavingsPlanTypeTable = savingsPlanTypeTableList.get(position);
                    ((SavingsPlanLifeGoals) context).lastCheck = holder.selectLoanTypeView;

                    //to fire up onPrepareOptionMenu
                    ((SavingsPlanLifeGoals) context).invalidateOptionsMenu();

                }else{
                    holder.selectLoanTypeView.setVisibility(View.GONE);
                    ((SavingsPlanLifeGoals) context).selectedSavingsPlanTypeTable = null;
                    ((SavingsPlanLifeGoals) context).lastCheck = null;

                    //to fire up onPrepareOptionMenu
                    ((SavingsPlanLifeGoals) context).invalidateOptionsMenu();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return savingsPlanTypeTableList.size();
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

        public void setViews(SavingsPlanTypeTable loanTypeTable){
            loanTypeName.setText(loanTypeTable.getSavingsTypeName());
            loanTypeDesc.setText(loanTypeTable.getSavingsTypeDescription());

            if(loanTypeTable.getSavingsTypeImageByteArray() == null) {
                Glide.with(mView.getContext()).load(loanTypeTable.getSavingsTypeImageUri()).thumbnail(Glide.with(mView.getContext()).load(loanTypeTable.getSavingsTypeImageThumbUri())).into(loanTypeImageView);
            }else{
                Bitmap bitmap = BitmapUtil.getBitMapFromBytes(loanTypeTable.getSavingsTypeImageByteArray());
                loanTypeImageView.setImageBitmap(bitmap);
            }
        }
    }
}
