package com.example.homework07_parta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CreateTripActivity extends AppCompatActivity {

    EditText et_Title_CT, et_location_CT;
    Button btn_check_CT, btn_add_trip_CT, btn_addUsers_CT;
    TextView txt_LatLong_CT;
    ImageView img_trip_CT;
    Location objLocation = null;
    boolean checkedLocation = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    ArrayList<String> userIds = new ArrayList<>();
    ArrayList<String> remainingUsers;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> tripUsersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        setTitle("Create a Trip");
        et_Title_CT = findViewById(R.id.et_Title_CT);
        et_location_CT = findViewById(R.id.et_location_CT);
        btn_check_CT = findViewById(R.id.btn_check_CT);
        btn_add_trip_CT = findViewById(R.id.btn_add_trip_CT);
        txt_LatLong_CT = findViewById(R.id.txt_LatLong_CT);
        img_trip_CT = findViewById(R.id.img_trip_CT);
        btn_addUsers_CT = findViewById(R.id.btn_addUsers_CT);
        mAuth = FirebaseAuth.getInstance();
        mRecyclerView=findViewById(R.id.rv_users_CT);

        btn_addUsers_CT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remainingUsers = new ArrayList<>();
                remainingUsers.clear();
                remainingUsers = userIds;
                remainingUsers.remove(mAuth.getCurrentUser().getEmail());
                if(remainingUsers.size()!=0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateTripActivity.this);
                    builder.setTitle("Select Users")
                            .setItems(remainingUsers.toArray(new String[0]), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    tripUsersList.add(remainingUsers.get(which));
                                    userIds.remove(remainingUsers.get(which));
                                    mLayoutManager = new LinearLayoutManager(CreateTripActivity.this);
                                    mRecyclerView.setLayoutManager(mLayoutManager);
                                    mAdapter = new UserAdapter(tripUsersList, mRecyclerView);
                                    mRecyclerView.setAdapter(mAdapter);

                                }
                            });
                    builder.create();
                    builder.show();
                }
                else{
                    Toast.makeText(CreateTripActivity.this, "No users found to add", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btn_check_CT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Location = et_location_CT.getText().toString();
                if (Location.length() != 0) {
                    String Url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?inputtype=textquery&fields=photos,formatted_address,name,geometry&key=AIzaSyAkU_HY5mNwpjqcHmd5Zei04afiy-Fpep4" + "&input=" + Location;
                    LocationAsync async = new LocationAsync(CreateTripActivity.this);
                    async.execute(Url);
                }
                else{
                    et_location_CT.setError("Please enter location to search");
                }
            }
        });

        btn_add_trip_CT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trip_title = et_Title_CT.getText().toString();
                if (checkedLocation = true && trip_title.length() != 0 && objLocation!=null) {
                    String Place = objLocation.place;
                    String imageUrl = objLocation.photoUrl;
                    String Latitude = objLocation.latitude;
                    String Longitude = objLocation.longitude;
                    FirebaseUser user = mAuth.getCurrentUser();
                    List<String> userStringList = tripUsersList;
                    String tripId = db.collection("trips").document().getId();
                    final Trip tripObj = new Trip(Latitude, Longitude, Place, imageUrl, user.getEmail(), trip_title, userStringList, tripId);
                    Map<String, Object> userMap = tripObj.toUserMap();
                    db.collection("trips").document(tripId)
                            .set(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CreateTripActivity.this, "Trip created successfully", Toast.LENGTH_SHORT).show();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("tripData", tripObj);
                                        Intent intent = new Intent(CreateTripActivity.this, TripsActivity.class);
                                        intent.putExtra("tripBundle", bundle);
                                        setResult(CreateTripActivity.RESULT_OK, intent);
                                        finish();
                                    } else {
                                        Toast.makeText(CreateTripActivity.this, "Trip creation Unsuccessful", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                } else {
                    if (checkedLocation = false) {
                        if (et_location_CT.getText().toString().length() != 0) {
                            et_location_CT.setError("Please click on check to set Location");
                        } else {
                            et_location_CT.setError("Please enter location and click check");
                        }
                    }

                    if (trip_title.length() == 0) {
                        et_Title_CT.setError("Please enter title");
                    }
                }
            }
        });
    }

    public void setLocationDetails(Location obj) {

        checkedLocation = true;
        objLocation = obj;
        txt_LatLong_CT.setText("Latitude: " + obj.getLatitude() + "; Longitude: " + obj.getLongitude() + "; place: " + obj.getPlace());
        Picasso.get().load(obj.getPhotoUrl()).into(img_trip_CT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null)
        {
            Intent intent = new Intent(CreateTripActivity.this, MainActivity.class);
            startActivity(intent);
        }
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userIds.add(document.getId());
                            }
                        }
                    }
                });

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
                Intent intent = new Intent(CreateTripActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
