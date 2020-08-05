package com.example.homework07_parta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    EditText et_email;
    EditText et_password;
    Button btn_login, btn_signUp;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    public static final int REQ_CODE = 1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressBar pb_loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_signUp = findViewById(R.id.btn_signUp);
        pb_loginProgress=findViewById(R.id.pb_loginProgress);
        mAuth = FirebaseAuth.getInstance();
        Context ctx = getApplicationContext();
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("ProfileData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                if (validateForm()) {
                    pb_loginProgress.setVisibility(View.VISIBLE);
                    signIn(email, password);
                }
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivityForResult(intent, REQ_CODE);

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            DocumentReference docRef = db.collection("users").document(user.getEmail());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            User user = new User(document.getData());
                                            Gson gson = new Gson();
                                            String jsonString = gson.toJson(user);
                                            Context ctx = getApplicationContext();
                                            SharedPreferences sharedPreferences = ctx.getSharedPreferences("ProfileData", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("Key", jsonString);
                                            editor.commit();
                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            pb_loginProgress.setVisibility(View.INVISIBLE);
                                            Intent intent = new Intent(MainActivity.this, TripsActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Log.d(TAG, "No such document");
                                            pb_loginProgress.setVisibility(View.INVISIBLE);
                                            Toast.makeText(MainActivity.this, "Login Failed. Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                        pb_loginProgress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(MainActivity.this, "Login Failed. Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            pb_loginProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Authentication failed." +" "+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = et_email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            et_email.setError("Required.");
            valid = false;
        } else {
            et_email.setError(null);
        }

        String password = et_password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            et_password.setError("Required.");
            valid = false;
        } else {
            et_password.setError(null);
        }

        return valid;
    }
}
