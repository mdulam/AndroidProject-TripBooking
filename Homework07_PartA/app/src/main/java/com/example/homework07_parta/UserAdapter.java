package com.example.homework07_parta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    ArrayList<String> users = new ArrayList<String>();
    RecyclerView recyclerViewToUpdate;
    int position;

    public UserAdapter(ArrayList<String> users, RecyclerView recyclerViewToUpdate) {
        this.users = users;
        this.recyclerViewToUpdate = recyclerViewToUpdate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.useritem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        this.position = position;
        final String  user = users.get(position);
        holder.user.setText(user);
        holder.recyclerViewToUpdate = recyclerViewToUpdate;
        holder.position=position;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView user;
        RecyclerView recyclerViewToUpdate;
        int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.txt_user_item);
        }
    }
}
