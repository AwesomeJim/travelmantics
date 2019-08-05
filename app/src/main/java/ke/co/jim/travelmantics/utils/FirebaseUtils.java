package ke.co.jim.travelmantics.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ke.co.jim.travelmantics.LoginActivity;
import ke.co.jim.travelmantics.SplashActivity;
import ke.co.jim.travelmantics.models.TravelDeal;

public class FirebaseUtils {
    private static final String TAG=FirebaseUtils.class.getSimpleName();
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseUser user;
    public static  FirebaseAuth mAuth;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;
    public static FirebaseAuth.AuthStateListener  mAuthStateListener;
    private static FirebaseUtils firebaseUtils;
    public static ArrayList<TravelDeal> mTravelDeals;

    private static Activity caller;
    public static boolean isAdmin;

    public FirebaseUtils() {
    }

    public static void opnebFbRerefence(String ref, final Activity callerActivity){
        if(firebaseUtils==null){
            firebaseUtils= new FirebaseUtils();
            mFirebaseDatabase= FirebaseDatabase.getInstance();
            //Initializes the Firebase instance
            mAuth=FirebaseAuth.getInstance();
            //check if the user is login
            ///user=mAuth.getCurrentUser();
            caller=callerActivity;
            mAuthStateListener= firebaseAuth -> {
                user=firebaseAuth.getCurrentUser();
                if(user==null){
                    //User Not Logged In
                    Intent intent = new Intent(callerActivity, LoginActivity.class);
                    callerActivity.startActivity(intent);
                }else {
                    String userId=user.getUid();
                    checkAdmin(userId);
                }
            };
            connectStorage();
        }

        mTravelDeals=new ArrayList<>();
        mDatabaseReference=mFirebaseDatabase.getReference().child(ref);
    }

    private static void checkAdmin(String userId) {
        isAdmin=false;
        DatabaseReference ref= mFirebaseDatabase.getReference().child("administrators").child(userId);
        Log.e(TAG, "UserID - :" + userId);
        ChildEventListener listener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                isAdmin=true;

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
        ref.addChildEventListener(listener);
    }

    public static void attachListener(){
        mAuth.addAuthStateListener(mAuthStateListener);
    }
    public static void detachListener(){
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    public static void connectStorage(){
        mFirebaseStorage=FirebaseStorage.getInstance();
        mStorageReference=mFirebaseStorage.getReference().child("deals_pitures");
    }
}
