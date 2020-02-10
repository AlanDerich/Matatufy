package com.derich.matatufy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChattingActivity extends AppCompatActivity implements ChatAdapter.OnChatClickListener{
ImageView send;
EditText etMessage;
RecyclerView rvChats;
FirebaseFirestore mFirestone;
private String emailRidesharer;
private String docName;
private FirebaseUser mUser;
    private List<Chats> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Intent fromRideshare = getIntent();
        docName = fromRideshare.getStringExtra("documentName");
        emailRidesharer = fromRideshare.getStringExtra("RidesharerEmail");
        etMessage=findViewById(R.id.edittext_chat);
        send=findViewById(R.id.button_send);
        rvChats = findViewById(R.id.list_chat);
        mFirestone = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        rvChats.setHasFixedSize(true);
        rvChats.setLayoutManager(new LinearLayoutManager(this));
        final CollectionReference mCollectionRef = mFirestone.collection("messages").document(docName + " by "+ emailRidesharer)
                .collection("comments");
        mCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    populateRecyclerView();
                } else {
                }

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mUser.getEmail();
                String message = etMessage.getText().toString().trim();
                if (message ==null || message.isEmpty()){
                    Toast.makeText(ChattingActivity.this,"The message cannot be empty",Toast.LENGTH_LONG).show();
                }
                else {
                Map<String,Object> timeS=new HashMap<>();
                timeS.put("timestamp",FieldValue.serverTimestamp());
                Chats chats = new Chats(email,message,timeS);
                mFirestone.collection("messages").document(docName + " by "+ emailRidesharer)
                        .collection("comments").document()
                        .set(chats)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChattingActivity.this, "Comment sent successfully", Toast.LENGTH_SHORT).show();
                        etMessage.setText("");
                        //startActivity(new Intent(ChattingActivity.this,MainActivity.class));
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChattingActivity.this, "Sorry..."+ e, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            }
        });
    }

    private void populateRecyclerView() {
        chats=new ArrayList<>();
        mFirestone.collectionGroup("comments").orderBy("timestamp", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                                chats.add(snapshot.toObject(Chats.class));
                            }
                            ChatAdapter mChatsAdapter = new ChatAdapter(chats,ChattingActivity.this);
                            mChatsAdapter.notifyDataSetChanged();
                            rvChats.setAdapter(mChatsAdapter);
                        } else {
                            Toast.makeText(ChattingActivity.this,"No data found.",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChattingActivity.this,"" + e,Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onChatClick(int position) {

    }
}
