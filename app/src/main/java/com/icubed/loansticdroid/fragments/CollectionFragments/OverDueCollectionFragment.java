package com.icubed.loansticdroid.fragments.CollectionFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.SlideUpPanelRecyclerAdapter;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.models.DueCollection;
import com.icubed.loansticdroid.models.DueCollectionDetails;
import com.icubed.loansticdroid.models.OverDueCollection;
import com.icubed.loansticdroid.util.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverDueCollectionFragment extends Fragment {

    RecyclerView recyclerView;
    public SlideUpPanelRecyclerAdapter slideUpPanelRecyclerAdapter;
    public List<DueCollectionDetails> overDueCollectionList;
    public List<CollectionTable> collectionTableList;
    public ProgressBar progressBar;
    private OverDueCollection overDueCollection;
    public SwipeRefreshLayout swipeRefreshLayout;
    public LinearLayout emptyCollection;

    public OverDueCollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_over_due_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.collection_list);
        progressBar = view.findViewById(R.id.progressBar);
        emptyCollection = view.findViewById(R.id.search_empty_layout);

        overDueCollection = new OverDueCollection(getActivity().getApplication(), getActivity());

        overDueCollectionList = new ArrayList<>();
        collectionTableList = new ArrayList<>();
        slideUpPanelRecyclerAdapter = new SlideUpPanelRecyclerAdapter(overDueCollectionList, getActivity(), collectionTableList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(slideUpPanelRecyclerAdapter);

        //Swipe down refresher initialization
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        getDueCollections();
    }

    //Swipe down refresh lstener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AndroidUtils.isMobileDataEnabled(getContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    overDueCollection.retrieveDueCollectionToLocalStorageAndCompareToCloud();
                }else {
                    Toast.makeText(getContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDueCollections() {
        if (!overDueCollection.doesCollectionExistInLocalStorage()) {
            overDueCollection.retrieveNewDueCollectionData();
        } else {
            hideProgressBar();
            swipeRefreshLayout.setRefreshing(true);
            overDueCollection.getDueCollectionData();
            overDueCollection.retrieveDueCollectionToLocalStorageAndCompareToCloud();
        }
    }

    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }
}
