package ke.co.jim.travelmantics.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ke.co.jim.travelmantics.models.TravelDeal;

public class FirebaseUtils {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtils firebaseUtils;
    public static ArrayList<TravelDeal> mTravelDeals;

    public FirebaseUtils() {
    }

    public static void opnebFbRerefence(String ref){
        if(firebaseUtils==null){
            firebaseUtils= new FirebaseUtils();
            mFirebaseDatabase= FirebaseDatabase.getInstance();
        }
        mTravelDeals=new ArrayList<>();
        mDatabaseReference=mFirebaseDatabase.getReference().child(ref);
    }
}
