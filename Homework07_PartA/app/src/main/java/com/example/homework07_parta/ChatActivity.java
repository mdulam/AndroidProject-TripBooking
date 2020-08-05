package com.example.homework07_parta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    ImageView addImage, sendMessage;
    RecyclerView recyclerView;
    Integer SELECT_FILE = 0;
    EditText message;
    Integer imagevalue = 0;
    ArrayList<Message> contact1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Date time;
    String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("Chat");
        addImage = findViewById(R.id.imageView2);
        sendMessage = findViewById(R.id.imageView3);
        message = findViewById(R.id.editText6);
        auth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView1);
        final Bundle extrasFromMain = getIntent().getExtras().getBundle("IdBundle");
        tripId = extrasFromMain.getString("TripId");
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        time = new Date();
        mRecyclerView = findViewById(R.id.recyclerView1);

        if (contact1 == null) {
            contact1 = new ArrayList<>();
        } else {
            contact1.clear();
        }

        db.collection("ChatRoom" + tripId).orderBy("Date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                contact1.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Message message = new Message(documentSnapshot.getData());
                    if (message != null) {
                        contact1.add(message);
                    }
                }
                if (contact1.size() > 0) {
                    mAdapter = new MessageAdapter(contact1);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        final Date finalConvertedDate = time;
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Message finalMessage = new Message();
                String msg = message.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(ChatActivity.this, "message not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    finalMessage.msg = msg;
                    finalMessage.name = auth.getCurrentUser().getEmail();
                    finalMessage.time = finalConvertedDate;
                    finalMessage.uid = auth.getCurrentUser().getUid();
                    finalMessage.tripId=tripId;

                    if (imagevalue == 0) {
                        addImage.setImageResource(R.drawable.addimage);
                        finalMessage.url = "null";
                        String id =db.collection("ChatRoom" + tripId).document().getId();
                        finalMessage.id = id;
                        Map<String, Object> messageMap = finalMessage.toUserMap();
                        db.collection("ChatRoom" + tripId).document(id)
                                .set(messageMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChatActivity.this, "Message sent Successfully", Toast.LENGTH_SHORT).show();
                                            message.getText().clear();

                                        } else {
                                            Toast.makeText(ChatActivity.this, "Message didn't send", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    } else {
                        addImage.setDrawingCacheEnabled(true);
                        addImage.buildDrawingCache();
                        Bitmap bitmap = addImage.getDrawingCache();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        final StorageReference storageReference = firebaseStorage.getReference();
                        String ref = "images/Image" + System.currentTimeMillis() + ".png";
                        final StorageReference imageRepository = storageReference.child(ref);
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
                                    finalMessage.url = task.getResult().toString();
                                    String id =db.collection("ChatRoom" + tripId).document().getId();
                                    finalMessage.id = id;
                                    Map<String, Object> messageMap = finalMessage.toUserMap();
                                    db.collection("ChatRoom" + tripId).document(id)
                                            .set(messageMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ChatActivity.this, "Message sent Successfully", Toast.LENGTH_SHORT).show();
                                                        message.getText().clear();
                                                    } else {
                                                        Toast.makeText(ChatActivity.this, "Message didn't send", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    private void SelectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_FILE) {
            Uri selectedImageUri = data.getData();
            addImage.setImageURI(selectedImageUri);
            imagevalue = 1;
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
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
        {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
