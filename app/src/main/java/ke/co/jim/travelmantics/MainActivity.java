package ke.co.jim.travelmantics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ke.co.jim.travelmantics.adapters.TravelDealsAdapter;
import ke.co.jim.travelmantics.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    TravelDealsAdapter mTravelDealsAdapter;

    String loginMode;
    String loginDetails;
    FirebaseUser user;
    FirebaseAuth mAuth;
    CircleImageView photo;
    Uri photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view ->
        {
            Intent intent = new Intent(MainActivity.this, DealsActivity.class);
            startActivity(intent);
        });
        if(FirebaseUtils.isAdmin){
            fab.setVisibility(View.VISIBLE);
        }else {
            fab.setVisibility(View.GONE);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        photo = headerLayout.findViewById(R.id.profile_image);

        //Initializes the Firebase instance
        mAuth=FirebaseAuth.getInstance();

        //check if the user is login
        user=mAuth.getCurrentUser();
        if(user!=null) {
            //User is logged in get their details and initialize the views and Load Journals
            loadGoogleUserDetails();
        }
        TextView loginM = headerLayout.findViewById(R.id.login_method);
        loginM.setText(loginMode);
        TextView details = headerLayout.findViewById(R.id.user_details);
        details.setText(loginDetails);
        if (photoUrl != null) {
            Picasso.get()
                    .load(photoUrl)
                    .into(photo);
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTravelDealsAdapter = new TravelDealsAdapter(this);
        showMenu();
        mRecyclerView.setAdapter(mTravelDealsAdapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        }else if (id == R.id.nav_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(task -> {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        mTravelDealsAdapter = new TravelDealsAdapter(this);
        FirebaseUtils.attachListener();
        showMenu();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtils.detachListener();
    }

    /**
            * Get the details of the Logged in User
   */
    public void loadGoogleUserDetails(){
        /**
         * Check which method/Provider a user Used to login
         */
        for (UserInfo profile : user.getProviderData()) {
            switch (profile.getProviderId()) {
                case "google.com": {

                    // Name, email address, and profile photo Url
                    loginMode = profile.getDisplayName();
                    loginDetails = profile.getEmail();
                    photoUrl = profile.getPhotoUrl();

                    break;
                }
                case "firebase": {
                    // Name, email address, and profile photo Url if available
                    loginDetails = profile.getEmail();
                    loginMode = profile.getDisplayName();
                    break;
                }
                case "phone": {
                    // Name, email address, and profile photo Url if its available
                    loginDetails = profile.getPhoneNumber();
                    loginMode = profile.getProviderId();

                    break;
                }
            }
        }


    }

    public void showMenu(){
        invalidateOptionsMenu();
    }
}
