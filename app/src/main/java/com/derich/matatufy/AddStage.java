package com.derich.matatufy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddStage extends AppCompatActivity {
    private static final String TAG = "";
    private TextView coOrd;
private EditText etFare,etDestination,etName;
private Spinner spDays,spOpening,spClosing;
private String mLats;
private String mLongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stage);
        coOrd = findViewById(R.id.textViewCoord);
        etFare = findViewById(R.id.editTextFare);
        etDestination = findViewById(R.id.editTextDestination);
        etName = findViewById(R.id.editTextName);
        spDays = findViewById(R.id.spinnerDays);
        spOpening = findViewById(R.id.spinnerOpening);
        spClosing = findViewById(R.id.spinnerClosing);
        Intent intent = getIntent();
        mLats = intent.getStringExtra("latitude");
        mLongs = intent.getStringExtra("longitude");
        coOrd.setText(mLats + " : " + mLongs);
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("0600hrs");spinnerArray.add("0700hrs");spinnerArray.add("0800hrs");spinnerArray.add("0900hrs");spinnerArray.add("1000hrs");spinnerArray.add("1100hrs");spinnerArray.add("1500hrs");spinnerArray.add("1600hrs");spinnerArray.add("1700hrs");spinnerArray.add("1800hrs");spinnerArray.add("1900hrs");spinnerArray.add("2000hrs");spinnerArray.add("2100hrs");spinnerArray.add("2200hrs");spinnerArray.add("2300hrs");spinnerArray.add("0000hrs");spinnerArray.add("0100hrs");spinnerArray.add("0200hrs");spinnerArray.add("0300hrs");spinnerArray.add("0400hrs");spinnerArray.add("0500hrs");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spOpening.setAdapter(adapter);
        spClosing.setAdapter(adapter);
        List<String> spinnerDays = new ArrayList<String>();
        spinnerDays.add("Monday-Sunday");
        spinnerDays.add("Monday-Saturday");
        spinnerDays.add("Monday-Friday");
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerDays);
        spDays.setAdapter(daysAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        MenuItem item = menu.findItem(R.id.login);
        item.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.saveRideShare);
        item1.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.saveStage :
                saveStage();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }
    private void saveStage(){
         String longitude = mLongs;
         String latitude = mLats;
         String sName = etName.getText().toString().trim();
         String destination = etDestination.getText().toString().trim();
         String price = etFare.getText().toString().trim();
         String openingT= spOpening.getSelectedItem().toString();
         String closingT = spClosing.getSelectedItem().toString();
         String days = spDays.getSelectedItem().toString();
        MarkerInfo markerInfo = new MarkerInfo(longitude,latitude,sName,destination,price,openingT,closingT,days);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("stages").document(encode(latitude)+ ":" + encode(longitude)).collection("allstages").document(destination)
                .set(markerInfo)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        startActivity(new Intent(AddStage.this,MainActivity.class));
                        Toast.makeText(AddStage.this,"Stage saved successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(AddStage.this,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                    }
                });

    }
    public static String encode(String coOrdns){
        return coOrdns.replace(".",",");
    }
    public static String decode(String coOrdns){
        return coOrdns.replace(",",".");
    }
}
