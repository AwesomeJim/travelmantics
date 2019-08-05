package ke.co.jim.travelmantics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import ke.co.jim.travelmantics.models.TravelDeal;
import ke.co.jim.travelmantics.utils.FirebaseUtils;

public class DealsActivity extends AppCompatActivity {

    private final static String TAG = DealsActivity.class.getSimpleName();
    private final static int PICTURE_RESULTS=42;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private TravelDeal travelDeal;

    private EditText editText_title, editText_price, editText_des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        FirebaseUtils.opnebFbRerefence("traveldeals", this);
        showMenu();
        mFirebaseDatabase = FirebaseUtils.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtils.mDatabaseReference;
        editText_title = findViewById(R.id.editText_title);
        editText_price = findViewById(R.id.editText_price);
        editText_des = findViewById(R.id.editText_des);
        Intent intent = this.getIntent();
        travelDeal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (travelDeal == null) {
            travelDeal = new TravelDeal();
        } else {
            editText_title.setText(travelDeal.getTitle());
            editText_price.setText(travelDeal.getPrice());
            editText_des.setText(travelDeal.getDescription());
        }
        Button btnImage=findViewById(R.id.btn_image);
        btnImage.setOnClickListener(view ->{
                Intent intentt = new Intent(Intent.ACTION_GET_CONTENT);
               intentt.setType("image/jpeg");
               intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
               startActivityForResult(intent.createChooser(intent, "Insert Picture"),PICTURE_RESULTS);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu, menu);
        MenuItem delete = menu.findItem(R.id.bar_cancel_delete);
        MenuItem insertMenu = menu.findItem(R.id.bar_save_btn);
        if (FirebaseUtils.isAdmin) {
            insertMenu.setVisible(true);
            delete.setVisible(true);
            disableEdittext(true);
        } else {
            insertMenu.setVisible(false);
            delete.setVisible(false);
            disableEdittext(false);
        }
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
            case R.id.bar_cancel_delete:
                deleteDelete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveDeal() {
        travelDeal.setTitle(editText_title.getText().toString());
        travelDeal.setPrice(editText_price.getText().toString());
        travelDeal.setDescription(editText_des.getText().toString());
        if (travelDeal.getId() == null) {
            mDatabaseReference.push().setValue(travelDeal);
        } else {
            mDatabaseReference.child(travelDeal.getId()).setValue(travelDeal);
        }
        Toast.makeText(this, "Deal Saved Successfully", Toast.LENGTH_LONG).show();
        cleanData();
    }


    private void deleteDelete() {
        if (travelDeal.getId() == null) {
            Toast.makeText(DealsActivity.this, "Please the Deal first", Toast.LENGTH_LONG).show();
            return;
        }
        mDatabaseReference.child(travelDeal.getId()).removeValue();
        onBackPressed();
    }

    private void cleanData() {
        travelDeal = new TravelDeal();
        editText_title.setText("");
        editText_price.setText("");
        editText_des.setText("");
        editText_title.requestFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void disableEdittext(boolean enable) {
        editText_title.setEnabled(enable);
        editText_price.setEnabled(enable);
        editText_des.setEnabled(enable);

    }

    public void showMenu() {
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICTURE_RESULTS && resultCode==RESULT_OK){
            Uri imageUri=data.getData();
            StorageReference reference=FirebaseUtils.mStorageReference.child(imageUri.getLastPathSegment());
            final UploadTask uploadTask = reference.putFile(imageUri);
            StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = uploadTask.addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();

                }
                // Continue with the task to get the download URL
                return reference.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                 String   thumb_download_url = task.getResult().toString();
                    travelDeal.setImageUrl(thumb_download_url);

                }
            })).addOnFailureListener(e -> {
            });
        }
    }


}
