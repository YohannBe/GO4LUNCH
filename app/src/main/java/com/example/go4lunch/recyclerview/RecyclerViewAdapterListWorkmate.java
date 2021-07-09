package com.example.go4lunch.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Message;
import com.example.go4lunch.model.User;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.ui.ChatDiscussion;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecyclerViewAdapterListWorkmate extends FirestoreRecyclerAdapter<User, RecyclerViewAdapterListWorkmate.ViewHolder> {


    public interface Listener {
        void onDataChanged();
    }

    private final RecyclerViewAdapterListWorkmate.Listener callback;
    private final Context context;

    public RecyclerViewAdapterListWorkmate(FirestoreRecyclerOptions<User> options,
                                           RecyclerViewAdapterListWorkmate.Listener callback,
                                           Context context) {
        super(options);
        this.callback = callback;
        this.context = context;
    }

    public void updateChat(Message message) {
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerViewAdapterListWorkmate.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewAdapterListWorkmate.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_layout, parent, false));
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterListWorkmate.ViewHolder holder, int position, @NonNull User model) {
        holder.itemView.invalidate();
        holder.updateUi(model);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        LinearLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_workmates);
            textView = itemView.findViewById(R.id.textview_destination_users);
            parentLayout = itemView.findViewById(R.id.user_recyclerview_layout);


        }

        public void updateUi(User user) {
                if (user.getUrlPicture() != null) {
                    String emplacement = user.getUrlPicture();
                    Tool.updatePictureGlide(imageView, emplacement, context);
                }

                String sentence = user.getFirstName() + " has not decided yet";
                String date = Tool.giveDependingDate();
                if (Tool.checkIfDateExist(user)) {
                            sentence = user.getFirstName() +
                                    " is going to " + user.getDateLunch().get(date).getRestaurantName();
                            textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                } else {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorLightGray));
                }
                textView.setText(sentence);

                parentLayout.setOnClickListener(v ->
                        {
                            Intent intent = new Intent(context, ChatDiscussion.class);
                            intent.putExtra("idSecondUser", user.getUid());
                            context.startActivity(intent);
                        }
                );
        }
    }
}
