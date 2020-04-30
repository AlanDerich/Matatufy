package com.derich.matatufy.Fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.derich.matatufy.FirebaseUI;
import com.derich.matatufy.MainActivity;
import com.derich.matatufy.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {
    private FirebaseUser mUser;
    private TextView tvName,tvEmail,UID,Verify,tvPassword;
    private ImageView imgProfile;
    private String m_text;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private static int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        tvName = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        Verify = view.findViewById(R.id.tvVerify);
        imgProfile = view.findViewById(R.id.imageView_prof_pic);
        tvPassword = view.findViewById(R.id.tvPassword);
        // Inflate the layout for this fragment
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        refresh();
        return view;
    }

    private void refresh() {
        if (mUser!=null){
            String name = mUser.getDisplayName();
            String email = mUser.getEmail();
            Uri photoUrl = mUser.getPhotoUrl();
            boolean emailVerified = mUser.isEmailVerified();
            if (emailVerified){
                Verify.setText(R.string.verified);
            }
            else {

                Verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mUser.getEmail().isEmpty()){
                            Toast.makeText(getContext(),"Please add an email address to continue",Toast.LENGTH_LONG).show();
                        }
                        else {
                        mUser.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Verification Email sent. Please verify and login again to continue",Toast.LENGTH_SHORT).show();
                                            logOut();

                                        }
                                        else {
                                            Toast.makeText(getContext(), "Email not sent. Try again later.",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });}
                    }
                });
            }
            //String uid = mUser.getUid();
            if (!name.equals("")){
                tvName.setText(name);}
            if (email!=null){
                if (!email.equals(""))
                {
                tvEmail.setText(email);}
            }
           // UID.setText(uid);
            if (photoUrl != null && !Uri.EMPTY.equals(photoUrl)){
                Glide.with(this).load(photoUrl).into(imgProfile);
            }
            imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
                    builder.setTitle("Alert!");
                    final TextView tvChange = new TextView(getContext());
                    tvChange.setTextSize(22);
                    tvChange.setText(R.string.change_prof_pic);
                    tvChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chooseImage();
                        }
                    });
                    builder.setView(tvChange);
                    builder.show();

                }
            });
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditNameInfo();
                }
            });
            tvEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditEmail();
                }
            });
            tvPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_text = "";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
                    builder.setTitle("Email Address");

// Set up the input
                    final EditText input = new EditText(getContext());
                    input.setTextColor(Color.parseColor("#0BF5AB"));
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_text = input.getText().toString();
                            mUser = FirebaseAuth.getInstance().getCurrentUser();
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            if (!(m_text.isEmpty())){
                            auth.sendPasswordResetEmail(m_text)


                                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(),"Reset password email sent. Tap on the email to verify and then login",Toast.LENGTH_SHORT).show();
                                                signOut();
                                            }
                                            else {
                                                Toast.makeText(getContext(),"Sorry an error occured.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                            else {
                                Toast.makeText(getContext(),"No email was inserted",Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });

        }
    }

    private void logOut() {
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getContext(), FirebaseUI.class));
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                imgProfile.setImageBitmap(bitmap);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(filePath)
                        .build();

                mUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(),"Profile pic updated successfully",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(),"Sorry an error occured",Toast.LENGTH_SHORT).show();
                                }
                                refresh();
                            }
                        });
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void EditEmail() {
        m_text = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
        builder.setTitle("New Email");

// Set up the input
        final EditText input = new EditText(getContext());
        input.setTextColor(Color.parseColor("#0BF5AB"));
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_text = input.getText().toString();
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                if (!(m_text.isEmpty())) {
                    mUser.updateEmail(m_text)

                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Session expired. Please log out and login again to change email.", Toast.LENGTH_SHORT).show();
                                    }
                                    refresh();
                                }
                            });
                }
                else {
                    Toast.makeText(getContext(), "No email inserted.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void EditNameInfo(){
    m_text = "";
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
    builder.setTitle("New Username");

// Set up the input
    final EditText input = new EditText(getContext());
        input.setTextColor(Color.parseColor("#0BF5AB"));
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
    builder.setView(input);

// Set up the buttons
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            m_text = input.getText().toString();
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (!(m_text.isEmpty())) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(m_text)
                        .build();
                mUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    refresh();
                                    Toast.makeText(getContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Session expired. Please log out and login again to change username.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else {
                Toast.makeText(getContext(),"Username cannot be empty",Toast.LENGTH_SHORT).show();
            }

        }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    });

    builder.show();
}
    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            getProfPic();

        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(getContext(),"The permission only allows for image uploading",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION );
        }
        else  if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            Toast.makeText(getContext(),"Oops! The required permission was denied.Go to settings and enable it to upload your profile picture",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION );

        }
        else {
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION );
        }
         }

    private void getProfPic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION){
            if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getProfPic();
            }
        }
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent= new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
