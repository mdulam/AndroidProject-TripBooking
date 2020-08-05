package com.example.homework07_parta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    ArrayList<Trip> tripAdapterArrayList = new ArrayList<Trip>();
    Context context;
    RecyclerView recyclerViewToUpdate;
    int position;

    public TripAdapter(ArrayList<Trip> tripAdapterArrayList, Context context, RecyclerView recyclerViewToUpdate) {
        this.tripAdapterArrayList = tripAdapterArrayList;
        this.context = context;
        this.recyclerViewToUpdate = recyclerViewToUpdate;
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tripitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        this.position = position;
        final Trip TripItem = tripAdapterArrayList.get(position);
        holder.trip=TripItem;

        holder.title.setText(TripItem.getTitle());
        holder.place.setText(TripItem.getPlace());
        holder.context = context;
        holder.position = position;
        holder.createdBy.setText(TripItem.getCreatedBy());
        Picasso.get().load(TripItem.getPhotoUrl()).into(holder.photo);
        holder.recyclerViewToUpdate = recyclerViewToUpdate;

       /* holder.deleteEmailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("demo", "Item to be deleted...");
                Log.d("demo", "Email Details : " + emailItem.toString());
                Toast.makeText(context, "ITEM TO BE DELETED", Toast.LENGTH_SHORT).show();
                new DeleteEmailAsyncTask(context, emailItem, recyclerViewToUpdate, position).execute(DELETE_EMAIL_URL);
                emailItemArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount() - position);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return tripAdapterArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView createdBy;
        ImageView photo;
        TextView place;
        Context context;
        RecyclerView recyclerViewToUpdate;
        int position;
        Trip trip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tripTitle);
            place = itemView.findViewById(R.id.place);
            createdBy = itemView.findViewById(R.id.createdBy);
            photo=itemView.findViewById(R.id.tripImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent (context, DisplayTripActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("OnClickTrip", trip);
                    intent.putExtra("OnClickTripBundle", bundle);
                    context.startActivity(intent);
                }
            });
        }
    }
}
