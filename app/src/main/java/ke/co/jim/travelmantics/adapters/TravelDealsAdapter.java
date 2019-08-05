package ke.co.jim.travelmantics.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ke.co.jim.travelmantics.DealsActivity;
import ke.co.jim.travelmantics.LoginActivity;
import ke.co.jim.travelmantics.R;
import ke.co.jim.travelmantics.SplashActivity;
import ke.co.jim.travelmantics.models.TravelDeal;
import ke.co.jim.travelmantics.utils.FirebaseUtils;

public class TravelDealsAdapter extends RecyclerView.Adapter<TravelDealsAdapter.TravelDealsViewHolder> {

    private final static String TAG=TravelDealsAdapter.class.getSimpleName();
    private ArrayList<TravelDeal> travelDeals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    public TravelDealsAdapter(Activity context) {
        FirebaseUtils.opnebFbRerefence("traveldeals", context);
        mFirebaseDatabase = FirebaseUtils.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtils.mDatabaseReference;
        travelDeals = FirebaseUtils.mTravelDeals;
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                td.setId(dataSnapshot.getKey());
                travelDeals.add(td);
                notifyItemInserted(travelDeals.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

// Member variable to handle item clicks
    //final private ItemClickListener mItemClickListener;

    // Class variables for the List that holds task data and the Context
    private Context mContext;

    @NonNull
    @Override
    public TravelDealsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_deal, parent, false);

        return new TravelDealsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelDealsViewHolder holder, int position) {
        TravelDeal mTravelDeal = travelDeals.get(position);
        holder.bind(mTravelDeal);
    }

    @Override
    public int getItemCount() {
        if (travelDeals == null) {
            return 0;
        }
        return travelDeals.size();
    }


    // Inner class for creating ViewHolders
    public class TravelDealsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView TitleView;
        TextView DescView;
        ImageView imageView;
        TextView price;


        /**
         * Constructor for the DealsViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        TravelDealsViewHolder(View itemView) {
            super(itemView);
            TitleView = itemView.findViewById(R.id.titleText);
            DescView = itemView.findViewById(R.id.descriptionText);
            imageView = itemView.findViewById(R.id.coverPhoto);
            price = itemView.findViewById(R.id.tv_price);
            itemView.setOnClickListener(this);

        }

        public void bind(TravelDeal deal) {
            TitleView.setText(deal.getTitle());
            DescView.setText(deal.getDescription());
            if(!deal.getImageUrl().equalsIgnoreCase("")) {
                Picasso.get()
                        .load(deal.getImageUrl())
                        .into(imageView);
            }
            price.setText(deal.getPrice());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.e(TAG, "Clicked Position - :" + position);
            TravelDeal selectedTravelDeal = travelDeals.get(position);
            Log.e(TAG, "Clicked Position - :" + selectedTravelDeal.getDescription());
            Intent intent = new Intent(view.getContext(), DealsActivity.class);
            intent.putExtra("Deal", selectedTravelDeal);
            view.getContext().startActivity(intent);
        }

    }
}
