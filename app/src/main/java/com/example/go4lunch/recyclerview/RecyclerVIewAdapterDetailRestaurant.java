package com.example.go4lunch.recyclerview;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.ui.ChatDiscussion;

import java.util.ArrayList;
import java.util.List;

public class RecyclerVIewAdapterDetailRestaurant extends RecyclerView.Adapter<RecyclerVIewAdapterDetailRestaurant.ViewHolder> {

    private final Context context;
    private List<User> userList = new ArrayList<>();
    private final String date = Tool.giveDependingDate();
    private String userId;

    @NonNull
    private final UpdateWorkmatesListener updateWorkmatesListener;

    public RecyclerVIewAdapterDetailRestaurant(Context context, @NonNull final UpdateWorkmatesListener updateWorkmatesListener, String userId) {
        this.context = context;
        this.updateWorkmatesListener = updateWorkmatesListener;
        this.userId = userId;
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
        return new ViewHolder(view, updateWorkmatesListener);
    }

    public interface UpdateWorkmatesListener {
        void onUpdateWorkmate(User user);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (context != null) {
            if (userList.get(position).getUrlPicture() != null) {
                Tool.updatePictureGlide(holder.imageView, userList.get(position).getUrlPicture(), context);
            }
        }
        assert context != null;
        String sentence = userList.get(position).getFirstName() + " " + context.getString(R.string.text_choice_done) + " " + userList.get(position).getDateLunch().get(date).getRestaurantName();
        holder.textView.setTextColor(ContextCompat.getColor(context, R.color.black));


        holder.textView.setText(sentence);

        holder.parentLayout.setOnClickListener(v ->
                {
                    if (!userList.get(position).getUid().equals(userId)) {
                        Intent intent = new Intent(context, ChatDiscussion.class);
                        intent.putExtra("idSecondUser", userList.get(position).getUid());
                        context.startActivity(intent);
                    } else Toast.makeText(context, "That is your own profile", Toast.LENGTH_SHORT).show();
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
