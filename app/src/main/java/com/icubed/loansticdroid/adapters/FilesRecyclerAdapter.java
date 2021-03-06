package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.ReactivateBorrowerActivity;
import com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerFilesFragment;

import java.util.ArrayList;

public class FilesRecyclerAdapter extends RecyclerView.Adapter<FilesRecyclerAdapter.ViewHolder> {

    ArrayList<String> fileDescription;
    BorrowerFilesFragment fragment;
    Context context;

    public FilesRecyclerAdapter(ArrayList<String> fileDescription, FragmentActivity activity) {
        this.fileDescription = fileDescription;
        FragmentManager fm = activity.getSupportFragmentManager();
        fragment = (BorrowerFilesFragment) fm.findFragmentByTag("borrower_files");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.fileDesc.setText(fileDescription.get(position));

        if(fragment != null) {
            if (!fileDescription.isEmpty()) {
                fragment.addFileTextView.setVisibility(View.VISIBLE);
            } else {
                fragment.addFileTextView.setVisibility(View.GONE);
            }

            holder.removeFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fileDescription.get(position).equals("Identification Card")) {
                        fileDescription.remove(position);
                        fragment.frontId = null;
                        fragment.backId = null;
                        FilesRecyclerAdapter.this.notifyDataSetChanged();
                    } else if (fileDescription.get(position).equals("Drivers license")) {
                        fileDescription.remove(position);
                        fragment.driverLicense = null;
                        FilesRecyclerAdapter.this.notifyDataSetChanged();
                    } else if (fileDescription.get(position).equals("Passport")) {
                        fileDescription.remove(position);
                        fragment.passport = null;
                        FilesRecyclerAdapter.this.notifyDataSetChanged();
                    } else {
                        int count = 0;

                        if (fragment.otherFileDesc != null) {
                            for (String desc : fragment.otherFileDesc) {
                                if (fileDescription.get(position).equals(desc)) {
                                    fileDescription.remove(position);
                                    fragment.otherFileDesc.remove(desc);
                                    fragment.otherFile.remove(count);
                                    FilesRecyclerAdapter.this.notifyDataSetChanged();
                                    break;
                                }
                                count++;
                            }
                        }
                    }
                }
            });
        }else {
            if (!fileDescription.isEmpty()) {
                ((ReactivateBorrowerActivity) context).addFileTextView.setVisibility(View.VISIBLE);
            } else {
                ((ReactivateBorrowerActivity) context).addFileTextView.setVisibility(View.GONE);
            }

            holder.removeFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fileDescription.get(position).equals("Identification Card")) {
                        fileDescription.remove(position);
                        ((ReactivateBorrowerActivity) context).frontId = null;
                        ((ReactivateBorrowerActivity) context).backId = null;
                        FilesRecyclerAdapter.this.notifyDataSetChanged();
                    } else if (fileDescription.get(position).equals("Drivers license")) {
                        fileDescription.remove(position);
                        ((ReactivateBorrowerActivity) context).driverLicense = null;
                        FilesRecyclerAdapter.this.notifyDataSetChanged();
                    } else if (fileDescription.get(position).equals("Passport")) {
                        fileDescription.remove(position);
                        ((ReactivateBorrowerActivity) context).passport = null;
                        FilesRecyclerAdapter.this.notifyDataSetChanged();
                    } else {
                        int count = 0;

                        if (((ReactivateBorrowerActivity) context).otherFileDesc != null) {
                            for (String desc : ((ReactivateBorrowerActivity) context).otherFileDesc) {
                                if (fileDescription.get(position).equals(desc)) {
                                    fileDescription.remove(position);
                                    ((ReactivateBorrowerActivity) context).otherFileDesc.remove(desc);
                                    ((ReactivateBorrowerActivity) context).otherFile.remove(count);
                                    FilesRecyclerAdapter.this.notifyDataSetChanged();
                                    break;
                                }
                                count++;
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return fileDescription.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public ImageView removeFile;
        public TextView fileDesc;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            removeFile = mView.findViewById(R.id.removeImage);
            fileDesc = mView.findViewById(R.id.file_desc);
        }
    }

}
