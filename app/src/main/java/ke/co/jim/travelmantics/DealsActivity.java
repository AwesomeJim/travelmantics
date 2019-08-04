package ke.co.jim.travelmantics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ke.co.jim.travelmantics.models.TravelDeal;
import ke.co.jim.travelmantics.utils.FirebaseUtils;

public class DealsActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    TravelDeal travelDeal;

    private EditText editText_title, editText_price, editText_des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        FirebaseUtils.opnebFbRerefence("traveldeals");
        mFirebaseDatabase = FirebaseUtils.mFirebaseDatabase;
        mDatabaseReference =FirebaseUtils.mDatabaseReference;
        editText_title = findViewById(R.id.editText_title);
        editText_price = findViewById(R.id.editText_price);
        editText_des = findViewById(R.id.editText_des);
        Intent intent = getIntent();
        travelDeal=(TravelDeal) intent.getSerializableExtra("Deal'");
        if(travelDeal==null){
            travelDeal= new TravelDeal();
        }
        editText_title.setText(travelDeal.getTitle());
        editText_price.setText(travelDeal.getPrice());
        editText_des.setText(travelDeal.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.bar_cancel_btn:
              cleanData();
                break;
            case R.id.bar_save_btn:
              saveDeal();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveDeal() {
        String  title=editText_title.getText().toString();
        String  price= editText_price.getText().toString();
        String  des=editText_des.getText().toString();
        TravelDeal travelDeal= new TravelDeal(title,des,price, "");
        mDatabaseReference.push().setValue(travelDeal);
        Toast.makeText(this, "Deal Saved Successfully", Toast.LENGTH_LONG).show();
        cleanData();
    }

    private void cleanData() {
        editText_title.setText("");
        editText_price.setText("");
        editText_des.setText("");
        editText_title.requestFocus();
    }
}
