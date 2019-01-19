package com.icubed.loansticdroid.fragments.BorrowersFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.models.Borrowers;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleBorrowerFragment extends Fragment {

    public RecyclerView borrowerRecyclerView;
    public BorrowerRecyclerAdapter borrowerRecyclerAdapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public Borrowers borrowers;

    public SingleBorrowerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_borrower, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        borrowers = new Borrowers(getActivity());

        borrowerRecyclerView = view.findViewById(R.id.borrower_list);


        //Swipe down refresher initialization
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        getAllBorrowers();
    }

    //Swipe down refresh lstener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                borrowers.loadAllBorrowersAndCompareToLocal();
            }
        });
    }

    private void getAllBorrowers() {
        if(!borrowers.doesBorrowersTableExistInLocalStorage()){
            borrowers.loadAllBorrowers();
        }else{
            swipeRefreshLayout.setRefreshing(true);
            borrowers.loadBorrowersToUI();
            borrowers.loadAllBorrowersAndCompareToLocal();
        }
    }

}
