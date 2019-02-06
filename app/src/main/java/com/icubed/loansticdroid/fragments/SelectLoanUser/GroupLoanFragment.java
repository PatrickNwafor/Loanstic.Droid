package com.icubed.loansticdroid.fragments.SelectLoanUser;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.BorrowerActivity;
import com.icubed.loansticdroid.activities.NewLoanWizard;
import com.icubed.loansticdroid.adapters.GroupLoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.GroupRecyclerAdapter;
import com.icubed.loansticdroid.models.GroupLoan;
import com.icubed.loansticdroid.models.Groups;
import com.icubed.loansticdroid.util.AndroidUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupLoanFragment extends Fragment {

    public RecyclerView groupRecyclerView;
    public GroupLoanRecyclerAdapter groupLoanRecyclerAdapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public GroupLoan groups;

    public GroupLoanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_loan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groups = new GroupLoan(getActivity());

        groupRecyclerView = view.findViewById(R.id.group_list);

        //Swipe down refresher initialization
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();
        ((NewLoanWizard) getContext()).borrowerProgressBar.setVisibility(View.VISIBLE);

        getAllGroups();
    }

    //Swipe down refresh lstener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AndroidUtils.isMobileDataEnabled(getContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    groups.loadAllGroupsAndCompareToLocal();
                }else{
                    Toast.makeText(getContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAllGroups() {
        if(!groups.doesGroupTableExistInLocalStorage()){
            groups.loadAllGroups();
        }else{
            swipeRefreshLayout.setRefreshing(true);
            groups.loadGroupsToUI();
            groups.loadAllGroupsAndCompareToLocal();
        }
    }
}
