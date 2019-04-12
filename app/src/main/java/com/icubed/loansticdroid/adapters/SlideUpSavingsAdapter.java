package com.icubed.loansticdroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.CollectionDetailsActivity;
import com.icubed.loansticdroid.activities.LoanRepayment;
import com.icubed.loansticdroid.activities.SavingsTransactionDepositPayment;
import com.icubed.loansticdroid.fragments.HomeFragments.DashboardFragment;
import com.icubed.loansticdroid.fragments.HomeFragments.MapFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTable;
import com.icubed.loansticdroid.models.DueCollectionDetails;
import com.icubed.loansticdroid.models.SavingsDetails;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.CustomDialogBox.PaymentDialogBox;
import com.icubed.loansticdroid.util.MapInfoWindow.OnInfoWindowElemTouchListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SlideUpSavingsAdapter extends RecyclerView.Adapter<SlideUpSavingsAdapter.ViewHolder> {

    List<SavingsDetails> savingsDetails;
    Context context;
    DashboardFragment fragment;
    private AlertDialog.Builder builder;
    private PaymentDialogBox paymentDialogBox;

    private FragmentActivity fragmentActivity;

    public SlideUpSavingsAdapter(List<SavingsDetails> savingsDetails, FragmentActivity activity) {
        this.savingsDetails = savingsDetails;
        fragmentActivity = activity;
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        fragment = (DashboardFragment) fm.findFragmentByTag("dashboard");
        builder = new AlertDialog.Builder(fragmentActivity);
        paymentDialogBox = new PaymentDialogBox(fragmentActivity);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_single_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final String firstName = savingsDetails.get(position).getBorrowersTable().getFirstName();
        final String lastName = savingsDetails.get(position).getBorrowersTable().getLastName();
        holder.setCollectionName(firstName, lastName);
        holder.setCollectionAmount(savingsDetails.get(position).getSavingsPlanCollectionTable().getSavingsCollectionAmount(), savingsDetails.get(position).getSavingsPlanCollectionTable().getAmountPaid());
        holder.setBusiness(savingsDetails.get(position).getBorrowersTable().getBusinessName());
        holder.setImage(savingsDetails.get(position).getBorrowersTable());

        holder.detailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clicked me", Toast.LENGTH_SHORT).show();
            }
        });

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) fragmentActivity.getSystemService(fragmentActivity.LAYOUT_INFLATER_SERVICE);
                final View view = layoutInflater.inflate(R.layout.custom_marker_layout_collection, null);
                final CircleImageView circleImageView = view.findViewById(R.id.user_dp);

                fragment.mGoogleMap.clear();
                final ArrayList<Marker> markers = new ArrayList<>();

                final MarkerOptions markerOptions = new MarkerOptions();

                LatLng latLng = new LatLng(savingsDetails.get(position).getBorrowersTable().getBorrowerLocationLatitude(),
                        savingsDetails.get(position).getBorrowersTable().getBorrowerLocationLongitude());
                //adding markerOptions properties for driver
                markerOptions.position(latLng);
                markerOptions.anchor(0.5f, 0.5f);

                markerOptions.title(lastName + " " + firstName);
                if(savingsDetails.get(position).getBorrowersTable().getBorrowerImageByteArray() == null){
                    RequestOptions placeholderOption = new RequestOptions();
                    placeholderOption.placeholder(R.drawable.new_borrower);
                    BitmapUtil.getImageAndThumbnailWithRequestOptionsGlide(
                            fragment.getContext(),
                            savingsDetails.get(position).getBorrowersTable().getProfileImageUri(),
                            savingsDetails.get(position).getBorrowersTable().getProfileImageThumbUri(),
                            placeholderOption)
                            .into(circleImageView);
                }
                else circleImageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(savingsDetails.get(position).getBorrowersTable().getBorrowerImageByteArray()));

                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.convertViewsToBitmap(view)));

                fragment.hidePanel();
                Marker mark = fragment.mGoogleMap.addMarker(markerOptions);

                mark.setTag(savingsDetails.get(position));

                customInfoWindowInit(fragment.mGoogleMap);

                //adding marker to map
                markers.add(mark);

                fragment.myMarker = fragment.mGoogleMap.addMarker(fragment.markerOptions);
                markers.add(fragment.myMarker);

                fragment.getRoute(fragment.markerOptions.getPosition(), markerOptions.getPosition(), markers);
                fragment.selectedUserLatLng = latLng;
            }
        });
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private void customInfoWindowInit(GoogleMap map) {
        fragment.mapWrapperLayout.init(map, getPixelsFromDp(fragment.getContext(), 39 + 20));

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        final View infoWindow = fragmentActivity.getLayoutInflater().inflate(R.layout.custom_info_layout, null);
        final TextView infoTitle = infoWindow.findViewById(R.id.title);
        final TextView colTitle = infoWindow.findViewById(R.id.title1);
        final ImageButton navBtn = infoWindow.findViewById(R.id.nav);
        final ImageButton colBtn = infoWindow.findViewById(R.id.col);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        final OnInfoWindowElemTouchListener infoButtonListener = new OnInfoWindowElemTouchListener(navBtn,
                fragment.getResources().getDrawable(R.color.whiteEnd),
                fragment.getResources().getDrawable(R.color.darkGrey))
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+marker.getPosition().latitude+","+marker.getPosition().longitude+""));
                fragment.getActivity().startActivity(intent);
            }
        };
        navBtn.setOnTouchListener(infoButtonListener);

        //custom info window collection button click listener
        final OnInfoWindowElemTouchListener infoButtonListener2 = new OnInfoWindowElemTouchListener(colBtn, fragment.getResources().getDrawable(R.color.whiteEnd), fragment.getResources().getDrawable(R.color.darkGrey)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                SavingsDetails savingsDetails = (SavingsDetails) marker.getTag();
                makePayment(savingsDetails);
            }
        };
        colBtn.setOnTouchListener(infoButtonListener2);


        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                if(marker.getTitle().equals("Your Location")){
                    navBtn.setVisibility(GONE);
                    colBtn.setVisibility(GONE);
                    colTitle.setVisibility(GONE);
                }else {
                    navBtn.setVisibility(VISIBLE);
                    colBtn.setVisibility(VISIBLE);
                    colTitle.setVisibility(VISIBLE);
                }

                infoTitle.setText(marker.getTitle());
                SavingsDetails savingsDetails = (SavingsDetails) marker.getTag();
                colTitle.setText("Collection Number: " + savingsDetails.getSavingsPlanCollectionTable().getSavingsCollectionNumber());
                infoButtonListener.setMarker(marker);
                infoButtonListener2.setMarker(marker);
                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                fragment.mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
    }

    private void makePayment(final SavingsDetails collectionTable) {
        paymentDialogBox.setOnYesClicked(new PaymentDialogBox.OnButtonClick() {
            @Override
            public void onYesButtonClick() {
                Intent intent = new Intent(fragment.getContext(), SavingsTransactionDepositPayment.class);
                intent.putExtra("collection", collectionTable.getSavingsPlanCollectionTable());
                intent.putExtra("savings", collectionTable.getSavingsTable());
                intent.putExtra("home", "home");
                fragment.startActivity(intent);
            }
        });
        paymentDialogBox.show();
    }

    @Override
    public int getItemCount() {
        return savingsDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        private TextView jobTextView;
        private CircleImageView collectionImageView;
        private TextView collectionNameTextView;
        private TextView amountTextView;
        public TextView detailsTextView;
        public FrameLayout frameLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            collectionNameTextView = mView.findViewById(R.id.collectionNameTextView);
            amountTextView = mView.findViewById(R.id.amountTextView);
            jobTextView = mView.findViewById(R.id.jobTextView);
            collectionImageView = mView.findViewById(R.id.circleImageView);
            detailsTextView = mView.findViewById(R.id.detailsTextView);
            frameLayout = mView.findViewById(R.id.frame);
        }

        public void setCollectionName(String firstName, String lastName){
            collectionNameTextView.setText(lastName + " " + firstName);
        }

        public void setCollectionAmount(double collectionAmount, double amountPaid){
            amountTextView.setText(String.valueOf(collectionAmount-amountPaid));
        }

        public void setBusiness(String businessName) {
            jobTextView.setText(businessName);
        }

        public void setImage(BorrowersTable dueCollectionDetails) {

            if (dueCollectionDetails.getBorrowerImageByteArray() == null) {
                BitmapUtil.getImageAndThumbnailWithGlide(mView.getContext(), dueCollectionDetails.getProfileImageUri(), dueCollectionDetails.getProfileImageThumbUri()).into(collectionImageView);
            } else {
                collectionImageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(dueCollectionDetails.getBorrowerImageByteArray()));
            }
        }
    }
    
}
