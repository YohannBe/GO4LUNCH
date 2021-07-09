package com.example.go4lunch.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Message;
import com.example.go4lunch.tool.Tool;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class RecyclerViewAdapterMessages extends FirestoreRecyclerAdapter<Message, RecyclerViewAdapterMessages.ViewHolder> {


    public interface Listener {
        void onDataChanged();
    }

    private Listener callback;
    private String currentUserUid;
    private String dateVisibility ="";

    public RecyclerViewAdapterMessages(FirestoreRecyclerOptions<Message> options, Listener callback, String currentUserUid) {
        super(options);
        this.callback = callback;
        this.currentUserUid = currentUserUid;
    }

    public void updateChat(String currentUserUid, Message message) {
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerViewAdapterMessages.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewAdapterMessages.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_layout, parent, false));
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterMessages.ViewHolder holder, int position, @NonNull Message model) {
        holder.itemView.invalidate();
        holder.updateUiMessage(this.currentUserUid, model);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView messageBody, hour, dateTextView;
        ConstraintLayout parentLayout;
        CardView messageHolder, dateHolder;
        LinearLayout messageLinearLayout;
        private final int colorCurrentUser;
        private final int colorRemoteUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageBody = itemView.findViewById(R.id.textview_chat_message);
            hour = itemView.findViewById(R.id.textview_hour_chat);
            parentLayout = itemView.findViewById(R.id.chat_message_layout_parent);
            messageHolder = itemView.findViewById(R.id.cardview_chat_message);
            messageLinearLayout = itemView.findViewById(R.id.messagebody_linearlayout);
            dateHolder = itemView.findViewById(R.id.cardview_date_message);
            dateTextView = itemView.findViewById(R.id.textview_date_message);


            colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryLight);
            colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.colorAccentLighter);
        }

        public void updateUiMessage(String currentUserUid, Message message) {

            messageBody.setText(message.getTextBody());
            if (message.getTimeStamp() != null) {
                Date date = new Date();
                if (!Tool.checkDateMessages(dateVisibility,message.getTimeStamp().getTime())){
                    dateVisibility = Tool.transformDateToString(message.getTimeStamp().getTime());
                    dateHolder.setVisibility(View.VISIBLE);
                    dateTextView.setText(dateVisibility);
                } else dateHolder.setVisibility(View.GONE);

                date.setTime(message.getTimeStamp().getTime());
                String formattedDate = Tool.getActualHour(date);
                hour.setText(formattedDate);
            } else {
                String dateString = Tool.getActualHour(null);
                hour.setText(dateString);
            }
            ConstraintLayout.LayoutParams messageContainerLayoutParams = (ConstraintLayout.LayoutParams) messageHolder.getLayoutParams();

            if (!currentUserUid.equals(message.getSender()) && message.getSender() != null) {
                messageContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                messageContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
                messageLinearLayout.setBackgroundColor(colorRemoteUser);
            } else {
                messageContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
                messageContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                messageLinearLayout.setBackgroundColor(colorCurrentUser);
            }
            messageContainerLayoutParams.horizontalBias = 0.0f;
            messageHolder.requestLayout();

        }
    }

}
