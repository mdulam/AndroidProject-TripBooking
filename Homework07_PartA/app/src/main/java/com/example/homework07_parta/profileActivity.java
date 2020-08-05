package com.example.homework07_parta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class profileActivity extends AppCompatActivity {

    TextView txt_email_Dp;
    EditText et_firstName_Dp, et_lastName_Dp;
    ImageView img_user_Dp;
    RadioGroup rb_gender_Dp;
    RadioButton rb_male_Dp, rb_female_Dp;
    Button btn_save_Dp;
    private FirebaseAuth mAuth;
    String gender;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String imageUrl;
    final static int GALLERY = 1;
    final static int CAMERA = 2;
    Bitmap imageReceived = null;
    String firstName;
    String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("User Profile");
        txt_email_Dp = findViewById(R.id.txt_email_Dp);
        et_firstName_Dp = findViewById(R.id.et_firstName_Dp);
        et_lastName_Dp = findViewById(R.id.et_lastName_Dp);
        img_user_Dp = findViewById(R.id.img_user_Dp);
        rb_gender_Dp = findViewById(R.id.rb_gender_Dp);
        rb_male_Dp = findViewById(R.id.rb_male_Dp);
        rb_female_Dp = findViewById(R.id.rb_female_Dp);
        btn_save_Dp = findViewById(R.id.btn_save_Dp);
        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();
        txt_email_Dp.setText(user.getEmail());

        Context ctx = getApplicationContext();
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("ProfileData", MODE_PRIVATE);
        String jsonString = sharedPreferences.getString("Key", "");
        Gson gson = new Gson();
        User userProfile = gson.fromJson(jsonString, User.class);

        if (userProfile != null) {
            et_firstName_Dp.setText(userProfile.firstName);
            et_lastName_Dp.setText(userProfile.lastName);
            if (userProfile.gender.equals("Male")) {
                rb_male_Dp.setChecked(true);
                gender = "Male";
            } else {
                rb_female_Dp.setChecked(true);
                gender = "Female";
            }
            Picasso.get().load(userProfile.photoURL).into(img_user_Dp);
            imageUrl = userProfile.photoURL;
        }

        rb_gender_Dp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_male_Dp) {
                    gender = "Male";
                }
                if (checkedId == R.id.rb_female_Dp) {
                    gender = "Female";
                }
            }
        });


        img_user_Dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        btn_save_Dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firstName = et_firstName_Dp.getText().toString();
                lastName = et_lastName_Dp.getText().toString();

                if (imageReceived == null) {
                    final User userObj = new User(firstName, lastName, imageUrl, gender);
                    Map<String, Object> userMap = userObj.toUserMap();
                    db.collection("users").document(user.getEmail())
                            .set(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Gson gson = new Gson();
                                        String jsonString = gson.toJson(userObj);
                                        Context ctx = getApplicationContext();
                                        SharedPreferences sharedPreferences = ctx.getSharedPreferences("ProfileData", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.commit();
                                        editor.putString("Key", jsonString);
                                        editor.commit();
                                        finish();

                                    } else {
                                        Toast.makeText(profileActivity.this, "User profile update UnSuccessful", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                } else {
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    final StorageReference storageReference = firebaseStorage.getReference();
                    String ref = "images/Image" + System.currentTimeMillis() + ".png";
                    final StorageReference imageRepository = storageReference.child(ref);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    imageReceived.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] data = byteArrayOutputStream.toByteArray();
                    UploadTask uploadTask = imageRepository.putBytes(data);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("demo", "onFailure: " + e.getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("demo", "onSuccess: Image upload Successful");
                        }
                    });

                    Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return imageRepository.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.d("demo", "Image" + task.getResult());
                                imageUrl = task.getResult().toString();
                                final User userObj = new User(firstName, lastName, imageUrl, gender);
                                Map<String, Object> userMap = userObj.toUserMap();
                                db.collection("users").document(user.getEmail())
                                        .set(userMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Gson gson = new Gson();
                                                    String jsonString = gson.toJson(userObj);
                                                    Context ctx = getApplicationContext();
                                                    SharedPreferences sharedPreferences = ctx.getSharedPreferences("ProfileData", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.clear();
                                                    editor.commit();
                                                    editor.putString("Key", jsonString);
                                                    editor.commit();
                                                    finish();
                                                    Toast.makeText(profileActivity.this, "User profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(profileActivity.this, "User profile update UnSuccessful", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });


    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    img_user_Dp.setImageBitmap(bitmap);
                    imageReceived = bitmap;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(profileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            img_user_Dp.setImageBitmap(thumbnail);
            imageReceived = thumbnail;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null)
        {
            Intent intent = new Intent(profileActivity.this, MainActivity.class);
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
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
