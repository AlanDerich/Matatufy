package com.derich.matatufy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.derich.matatufy.Fragments.RideShare;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddRideShare extends AppCompatActivity {
    private EditText et_name,et_model,et_phone,et_from,et_to,et_cash;
    private TextView et_date,et_time;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride_share);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        et_name = findViewById(R.id.editText_add_name);
        et_model = findViewById(R.id.editText_add_model);
        et_phone = findViewById(R.id.editText_add_phone);
        et_date = findViewById(R.id.editText_add_date);
        et_from = findViewById(R.id.editText_add_from);
        et_to = findViewById(R.id.editText_add_to);
        et_time = findViewById(R.id.editText_add_time);
        et_cash = findViewById(R.id.editText_add_cash);
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(AddRideShare.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et_time.setText(selectedHour + " : " +selectedMinute);
                    }
                },hour,minute,true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar mDate = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mDate.set(Calendar.YEAR,year);
                        mDate.set(Calendar.MONTH,month);
                        mDate.set(Calendar.DAY_OF_MONTH,day);
                        String format = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(format);et_date.setText(sdf.format(mDate.getTime()));
                    }
                };
                new DatePickerDialog(AddRideShare.this,date,mDate.get(Calendar.YEAR),mDate.get(Calendar.MONTH),mDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
                
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        MenuItem item = menu.findItem(R.id.login);
        item.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.saveStage);
        item1.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.saveRideShare:
                saveRideShare();

                default:
                    return super.onOptionsItemSelected(item);
        }

    }
    public static String encode(String coOrdns){
        return coOrdns.replace("/",",");
    }
    private void saveRideShare(){
        String driverName = et_name.getText().toString().trim();
        String carModel = et_model.getText().toString().trim();
        String driverPhone= et_phone.getText().toString().trim();
        String date = et_date.getText().toString().trim();
        String from = et_from.getText().toString().trim();
        String destination= et_to.getText().toString().trim();
        String time = et_time.getText().toString().trim();
        String amount= et_cash.getText().toString().trim();

        FirebaseFirestore mFirestone = FirebaseFirestore.getInstance();
            String email = mUser.getEmail();
            Boolean verified = mUser.isEmailVerified();
            if (email.equals("")){
                Toast.makeText(this,"You don't have an email address. Please click on your profile and add an email to continue.",Toast.LENGTH_LONG).show();
            }
            else if (!verified){
                Toast.makeText(this,"Email not verified. Please click on your profile and verify your email to continue.",Toast.LENGTH_LONG).show();
            }
            else {
                if (driverName.equals("") || carModel.equals("") || driverPhone.equals("") || date.equals("Click to select Date") || from.equals("") || destination.equals("") || time.equals("Click to select time") || amount.equals("")){
                    Toast.makeText(this,"All fields are required. Please fill to continue",Toast.LENGTH_SHORT).show();

                }
                else {
                    try {
                        if (new SimpleDateFormat("dd/MM/yyyy").parse(date).before(new Date())) {
                            Toast.makeText(AddRideShare.this,"Please select a valid date",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            RideShareInfo mRideShareInfo = new RideShareInfo(driverName, carModel, driverPhone, date, from, destination, time, amount);
                            mFirestone.collection("RideShares").document(email).collection("all rideshares")
                                    .document(encode(date)).set(mRideShareInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(AddRideShare.this, MainActivity.class));
                                            Toast.makeText(AddRideShare.this,"RideShare started successfully",Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddRideShare.this,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
        }
    }
}
