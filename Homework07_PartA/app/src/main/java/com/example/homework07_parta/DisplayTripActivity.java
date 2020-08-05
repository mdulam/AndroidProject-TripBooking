package com.example.homework07_parta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Empty;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayTripActivity extends AppCompatActivity {

    TextView txt_title_DT, txt_place_DT, txt_CreatedBy_DT;
    ImageView img_tripPhoto_DT;
    Button btn_Cancel_DT, btn_goToChat, btn_Join_DT, btn_remove_DT;
    private FirebaseAuth mAuth;
    Trip objTrip;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> users;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<String> usersList= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip);
        setTitle("Trip Details");
        txt_title_DT = findViewById(R.id.txt_title_DT);
        txt_place_DT = findViewById(R.id.txt_place_DT);
        txt_CreatedBy_DT = findViewById(R.id.txt_CreatedBy_DT);
        img_tripPhoto_DT = findViewById(R.id.img_tripPhoto_DT);
        btn_Join_DT = findViewById(R.id.btn_Join_DT);
        btn_remove_DT = findViewById(R.id.btn_remove_DT);
        btn_Cancel_DT = findViewById(R.id.btn_Cancel_DT);
        btn_goToChat = findViewById(R.id.btn_goToChat);
        mRecyclerView=findViewById(R.id.rv_users_DT);
        btn_Join_DT.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        final Bundle extrasFromMain = getIntent().getExtras().getBundle("OnClickTripBundle");
        objTrip = (Trip) extrasFromMain.getSerializable("OnClickTrip");
        if (objTrip != null) {
            users = new ArrayList<>();
            users = objTrip.getUsers();
            txt_title_DT.setText(objTrip.getTitle());
            txt_place_DT.setText(objTrip.getPlace());
            txt_CreatedBy_DT.setText(objTrip.getCreatedBy());
            Picasso.get().load(objTrip.getPhotoUrl()).into(img_tripPhoto_DT);
             if (objTrip.users != null) {
                 usersList = new ArrayList(objTrip.getUsers());
                 FirebaseUser user = mAuth.getCurrentUser();
                 mLayoutManager = new LinearLayoutManager(DisplayTripActivity.this);
                 mRecyclerView.setLayoutManager(mLayoutManager);
                 mAdapter = new UserAdapter(usersList, mRecyclerView);
                 mRecyclerView.setAdapter(mAdapter);
                 if(user.getEmail().equals(objTrip.getCreatedBy()) || objTrip.getUsers().contains(user.getEmail())){
                     btn_Join_DT.setVisibility(View.INVISIBLE);
                     btn_remove_DT.setVisibility(View.VISIBLE);
                     btn_goToChat.setVisibility(View.VISIBLE);
                 }
                 else
                 {
                     btn_Join_DT.setVisibility(View.VISIBLE);
                     btn_remove_DT.setVisibility(View.INVISIBLE);
                     btn_goToChat.setVisibility(View.INVISIBLE);
                 }

            }
        }

        btn_goToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayTripActivity.this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("TripId", objTrip.tripId);
                intent.putExtra("IdBundle", bundle);
                startActivity(intent);

            }
        });

        btn_Join_DT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                users.add(user.getEmail());
                DocumentReference washingtonRef = db.collection("trips").document(objTrip.tripId);
                washingtonRef
                        .update("users", users)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                usersList.add(user.getEmail());
                                Log.d("", "DocumentSnapshot successfully updated!");
                                btn_Join_DT.setVisibility(View.INVISIBLE);
                                btn_remove_DT.setVisibility(View.VISIBLE);
                                btn_goToChat.setVisibility(View.VISIBLE);
                                mLayoutManager = new LinearLayoutManager(DisplayTripActivity.this);
                                mRecyclerView.setLayoutManager(mLayoutManager);
                                mAdapter = new UserAdapter(usersList, mRecyclerView);
                                mRecyclerView.setAdapter(mAdapter);
                                Toast.makeText(DisplayTripActivity.this, "Trip Joined Successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("", "Error updating document", e);
                                Toast.makeText(DisplayTripActivity.this, "Cannot able to add to trip"+ e, Toast.LENGTH_SHORT).show();
                                btn_Join_DT.setVisibility(View.VISIBLE);
                                btn_remove_DT.setVisibility(View.INVISIBLE);
                                btn_goToChat.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        });

        btn_Cancel_DT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_remove_DT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user.getEmail().equals(objTrip.getCreatedBy())) {
                    db.collection("trips").document(objTrip.getTripId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(DisplayTripActivity.this, "Successfully removed from Trip", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DisplayTripActivity.this, "Error removing from trip. Please try again", Toast.LENGTH_SHORT).show();
                                    Log.w("", "Error deleting trip", e);
                                }
                            });
                } else {
                    users.remove(user.getEmail());
                    db.collection("trips").document(objTrip.getTripId())
                            .update("users", users)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("", "Trip successfully deleted!");
                                    btn_Join_DT.setVisibility(View.VISIBLE);
                                    btn_remove_DT.setVisibility(View.INVISIBLE);
                                    btn_goToChat.setVisibility(View.INVISIBLE);
                                    usersList.remove(user.getEmail());
                                    mLayoutManager = new LinearLayoutManager(DisplayTripActivity.this);
                                    mRecyclerView.setLayoutManager(mLayoutManager);
                                    mAdapter = new UserAdapter(usersList, mRecyclerView);
                                    mRecyclerView.setAdapter(mAdapter);
                                    Toast.makeText(DisplayTripActivity.this, "Removed from trip successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DisplayTripActivity.this, "Error removing from trip. Please try after some time", Toast.LENGTH_SHORT).show();
                                    Log.w("", "Error deleting trip", e);
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null)
        {
            Intent intent = new Intent(DisplayTripActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DisplayTripActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
