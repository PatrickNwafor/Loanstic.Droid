package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.BorrowerDetailsSingle;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMembersRecyclerAdapter extends RecyclerView.Adapter<GroupMembersRecyclerAdapter.ViewHolder> {
    
    List<BorrowersTable> borrowersTableList;
    Context context;

    public GroupMembersRecyclerAdapter(List<BorrowersTable> borrowersTableList) {
        this.borrowersTableList = borrowersTableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_file_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setViews(borrowersTableList.get(position));

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(context, BorrowerDetailsSingle.class);
                pictureIntent.putExtra("borrower", borrowersTableList.get(position));
                pictureIntent.putExtra("borrowerImageByteArray", borrowersTableList.get(position).getBorrowerImageByteArray());
                context.startActivity(pictureIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return borrowersTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView memberNameTextView;
        public CircleImageView memberImageView;
        public FrameLayout frameLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            memberNameTextView = mView.findViewById(R.id.file_desc);
            memberImageView = mView.findViewById(R.id.file_image);
            frameLayout = mView.findViewById(R.id.file_frame);
        }

        public void setViews(BorrowersTable borrowersTable){

            memberNameTextView.setText(borrowersTable.getLastName() + " "+ borrowersTable.getFirstName());

            if(borrowersTable.getBorrowerImageByteArray() == null) {
                BitmapUtil.getImageAndThumbnailWithGlide(mView.getContext(), borrowersTable.getProfileImageUri(), borrowersTable.getProfileImageThumbUri()).into(memberImageView);
            }else{
                memberImageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(borrowersTable.getBorrowerImageByteArray()));
            }

        }
    }
    
}
