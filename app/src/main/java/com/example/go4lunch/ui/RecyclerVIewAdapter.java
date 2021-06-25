package com.example.go4lunch.ui;

import android.content.Context;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerVIewAdapter extends RecyclerView.Adapter<RecyclerVIewAdapter.ViewHolder> {

    private Context context;
    private List<User> userList = new ArrayList<>();
    private String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @NonNull
    private final UpdateWorkmatesListener updateWorkmatesListener;

    public RecyclerVIewAdapter(Context context, final UpdateWorkmatesListener updateWorkmatesListener) {
        this.context = context;
        this.updateWorkmatesListener = updateWorkmatesListener;
    }

    public void updateWorkmateList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    /**
     * method responsible for inflating the view
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false);
        ViewHolder holder = new ViewHolder(view, updateWorkmatesListener);
        return holder;
    }

    public interface UpdateWorkmatesListener {
        void onUpdateWorkmate(User user);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (context != null) {
            if (userList.get(position).getUrlPicture() != null) {
                String emplacement = userList.get(position).getUrlPicture();
                FirebaseStorage.getInstance().getReference(emplacement).getDownloadUrl()
                        .addOnSuccessListener(uri -> Glide.with(context)
                                .load(uri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(holder.imageView));
            } else {
                Glide.with(context)
                        .load(R.drawable.person_icons)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.imageView);
            }
        }
        String sentence = userList.get(position).getFirstName();

        if(userList.get(position).getDateLunch() != null){
            if (userList.get(position).getDateLunch().get(date) != null){
                if (userList.get(position).getDateLunch().get(date).getRestaurantName() != null)
                    sentence = sentence +
                            " is going to "+ userList.get(position).getDateLunch().get(date).getRestaurantName();
            }
        } else {
            sentence = sentence + " has not decided yet";
            holder.textView.setTextColor(Color.parseColor("#808080"));
        }
        holder.textView.setText(sentence);

        holder.parentLayout.setOnClickListener(v ->
                {
                    Toast.makeText(context, userList.get(position).getFirstName(), Toast.LENGTH_SHORT).show();
                }
        );

    }

    @Override
    public int getItemCount() {
        if (userList == null)
            return 0;
        else
            return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        LinearLayout parentLayout;

        public ViewHolder(@NonNull View itemView, UpdateWorkmatesListener updateWorkmatesListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_workmates);
            textView = itemView.findViewById(R.id.textview_destination_users);
            parentLayout = itemView.findViewById(R.id.user_recyclerview_layout);


        }
    }
}
