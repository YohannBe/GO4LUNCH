package com.example.go4lunch.ui;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class RecyclerVIewAdapter extends RecyclerView.Adapter<RecyclerVIewAdapter.ViewHolder> {

    private ArrayList<User> workerList;
    private Context context;

    public RecyclerVIewAdapter(ArrayList<User> workerList, Context context) {
        this.workerList = workerList;
        this.context = context;
    }

    /**
     * method responsible for inflating the view
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("bindview", "bindview called");

        if ( workerList.get(position).getUrlPicture() != null) {
            FirebaseStorage.getInstance().getReference(workerList.get(position).getUrlPicture()).getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(context)
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.imageView));
        }
        holder.textView.setText(workerList.get(position).getFirstName() + " " + workerList.get(position).getLastName());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, workerList.get(position).getFirstName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (workerList == null)
            return 0;
        else
            return workerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_workmates);
            textView = itemView.findViewById(R.id.textview_destination_users);
            parentLayout = itemView.findViewById(R.id.user_recyclerview_layout);


        }
    }
}
