package com.example.homework07_parta;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import org.ocpsoft.prettytime.PrettyTime;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    ArrayList<Message> mData;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Message m;
    LinearLayout.LayoutParams lp2;

    public MessageAdapter(ArrayList<Message> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messageitem, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder((LinearLayout) view);
        return viewHolder;



    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder viewHolder, final int pos) {
        Log.d("mDtaa", mData.size() + "");
        PrettyTime p = new PrettyTime();
        m = mData.get(pos);
        LinearLayout linearLayout = (LinearLayout) viewHolder.itemView;
        ConstraintLayout constraintLayout = viewHolder.itemView.findViewById(R.id.constraint_rv);

        if(auth.getCurrentUser().getEmail().equals(m.name)){
            linearLayout.setGravity(Gravity.RIGHT);
            constraintLayout.setBackgroundResource(R.drawable.messgaeitemborder);
        }
        else{
            linearLayout.setGravity(Gravity.LEFT);
            constraintLayout.setBackgroundResource(R.drawable.messageitemleft);
            //constraintLayout.setBackgroundColor(Color.parseColor("#277C7676"));
        }

        if (m.id != null) {
            viewHolder.tv1.setText(m.msg);
            viewHolder.tv2.setText(m.name);
            viewHolder.objMessage=m;
            viewHolder.tv3.setText(p.format(m.time));
            if (m.url == "null") {
                viewHolder.pic.setVisibility(View.INVISIBLE);
            } else {
                Picasso.get().load(m.url).into(viewHolder.pic);
            }
            FirebaseUser user = auth.getCurrentUser();
            if (m.getUid().equals(user.getUid())) {
                viewHolder.tv2.setText("You");
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("ChatRoom" + m.tripId).document(m.id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("", "Message successfully deleted!");
                                        notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("", "Error deleting Message", e);
                                    }
                                });
                    }
                });
            } else {
                viewHolder.tv2.setText(m.name);
                viewHolder.delete.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv1, tv2, tv3;
        ImageView pic, delete;
        Message objMessage;


        public ViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.textView2);
            tv2 = itemView.findViewById(R.id.textView3);
            tv3 = itemView.findViewById(R.id.textView4);
            pic = itemView.findViewById(R.id.imageView4);
            delete = itemView.findViewById(R.id.imageView5);
        }
    }
}
