package com.icubed.loansticdroid.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icubed.loansticdroid.R;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class SlideUpPanelRecyclerAdapter extends RecyclerView.Adapter<SlideUpPanelRecyclerAdapter.ViewHolder> {

    List<String> collectionList;

    public SlideUpPanelRecyclerAdapter(List collectionList) {
        this.collectionList = collectionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_single_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String collectionName = collectionList.get(position);
        holder.setCollectionName(collectionName, position);
        Log.d("Collections", collectionName);

    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        private TextView jobTextView;
        private CircleImageView collectionImageView;
        private TextView collectionNameTextView;
        private TextView amountTextView;
        private TextView dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setCollectionName(String collections, int position){

            collectionNameTextView = mView.findViewById(R.id.collectionNameTextView);
            dateTextView = mView.findViewById(R.id.dateTextView);

            if(position != 0) {
                dateTextView.setVisibility(View.GONE);
                collectionNameTextView.setText(collections);

                return;
            }

            collectionNameTextView.setText(collections);

        }
    }

}
