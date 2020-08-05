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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText et_email_signUp, et_password_signUp, et_firstName_signUp, et_lastName_signUp;
    Button btn_signUp;
    RadioGroup rg_gender_signUp;
    RadioButton rb_male_signUp, rb_female_signUp;
    ImageView img_user_signUp;
    String gender = null;
    final static int GALLERY = 3;
    final static int CAMERA = 4;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/homework-parta.appspot.com/o/user_image.png?alt=media&token=000e5673-a3bd-47a8-b11b-ec7cdb4779f4";
    String imageUrl = null;
    Bitmap imageReceived;
    String email;
    String password;
    String firstName;
    String lastName;
    ProgressBar pb_signUp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("SignUp");
        et_email_signUp = findViewById(R.id.et_email_signUp);
        et_password_signUp = findViewById(R.id.et_password_signUp);
        et_firstName_signUp = findViewById(R.id.et_firstName_signUp);
        et_lastName_signUp = findViewById(R.id.et_lastName_signUp);
        btn_signUp = findViewById(R.id.btn_signUp);
        rg_gender_signUp = findViewById(R.id.rg_gender_signUp);
        rb_male_signUp = findViewById(R.id.rb_male_signUp);
        rb_female_signUp = findViewById(R.id.rb_female_signUp);
        img_user_signUp = findViewById(R.id.img_user_signUp);
        pb_signUp= findViewById(R.id.pb_signUp);

        mAuth = FirebaseAuth.getInstance();

        img_user_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        rg_gender_signUp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_male_signUp) {
                    gender = "Male";
                }
                if (checkedId == R.id.rb_female_signUp) {
                    gender = "Female";
                }
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 email = et_email_signUp.getText().toString();
                 password = et_password_signUp.getText().toString();
                 firstName = et_firstName_signUp.getText().toString();
                 lastName = et_lastName_signUp.getText().toString();
                if (email.length() != 0 && password.length() >=6 && firstName.length() != 0 & gender != null) {
                    pb_signUp.setVisibility(View.VISIBLE);
                    if (imageReceived == null) {
                        String emailStr = et_email_signUp.getText().toString();
                        final User userObj = new User(firstName, lastName, defaultImageUrl, gender);
                        Map<String, Object> userMap = userObj.toUserMap();
                        db.collection("users").document(emailStr)
                                .set(userMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mAuth.createUserWithEmailAndPassword(email, password)
                                                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                FirebaseUser user = mAuth.getCurrentUser();
                                                                Toast.makeText(SignUpActivity.this, "New User Created",
                                                                        Toast.LENGTH_SHORT).show();
                                                                Gson gson = new Gson();
                                                                String jsonString = gson.toJson(userObj);
                                                                Context ctx = getApplicationContext();
                                                                SharedPreferences sharedPreferences = ctx.getSharedPreferences("ProfileData", MODE_PRIVATE);
                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                editor.putString("Key", jsonString);
                                                                editor.commit();
                                                                pb_signUp.setVisibility(View.INVISIBLE);
                                                                Intent intent = new Intent(SignUpActivity.this, TripsActivity.class);
                                                                startActivity(intent);


                                                            } else {
                                                                pb_signUp.setVisibility(View.INVISIBLE);
                                                                Log.w("", "createUserWithEmail:failure", task.getException());
                                                                Toast.makeText(SignUpActivity.this, "Failed to create user"+task.getException(),
                                                                        Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });
                                        } else {
                                            pb_signUp.setVisibility(View.INVISIBLE);
                                            Toast.makeText(SignUpActivity.this, "User profile creation UnSuccessful", Toast.LENGTH_SHORT).show();

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
                                    String emailStr = et_email_signUp.getText().toString();
                                    db.collection("users").document(emailStr)
                                            .set(userMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mAuth.createUserWithEmailAndPassword(email, password)
                                                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                                        if (task.isSuccessful()) {
                                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                                            Toast.makeText(SignUpActivity.this, "New User Created",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                            Gson gson = new Gson();
                                                                            String jsonString = gson.toJson(userObj);
                                                                            Context ctx = getApplicationContext();
                                                                            SharedPreferences sharedPreferences = ctx.getSharedPreferences("ProfileData", MODE_PRIVATE);
                                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                            editor.putString("Key", jsonString);
                                                                            editor.commit();
                                                                            pb_signUp.setVisibility(View.INVISIBLE);
                                                                            Intent intent = new Intent(SignUpActivity.this, TripsActivity.class);
                                                                            startActivity(intent);

                                                                        } else {
                                                                            pb_signUp.setVisibility(View.INVISIBLE);
                                                                            Log.w("", "createUserWithEmail:failure", task.getException());
                                                                            Toast.makeText(SignUpActivity.this, "Failed to create user"+task.getException(),
                                                                                    Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        pb_signUp.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(SignUpActivity.this, "User profile creation UnSuccessful", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                }
                else{
                    if(email.length()==0)
                    {
                        et_email_signUp.setError("Please enter email");
                    }
                    if(password.length()==0)
                    {
                        et_password_signUp.setError("Please enter password");
                    }
                    if(password.length()>0 &&password.length()<6)
                    {
                        et_password_signUp.setError("Password must be at least six characters");
                    }

                    if(firstName.length()==0){
                        et_firstName_signUp.setError("Please enter First Name");
                    }

                    if(gender==null){
                        Toast.makeText(SignUpActivity.this, "Please select gender", Toast.LENGTH_SHORT).show();
                    }
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
                    img_user_signUp.setImageBitmap(bitmap);
                    imageReceived = bitmap;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            img_user_signUp.setImageBitmap(thumbnail);
            imageReceived = thumbnail;
        }
    }
}
