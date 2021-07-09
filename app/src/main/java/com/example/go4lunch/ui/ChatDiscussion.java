package com.example.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Message;
import com.example.go4lunch.model.User;
import com.example.go4lunch.recyclerview.RecyclerViewAdapterMessages;
import com.example.go4lunch.tool.Tool;
import com.example.go4lunch.viewmodel.ChatViewModel;
import com.example.go4lunch.viewmodel.UserViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatDiscussion extends AppCompatActivity implements RecyclerViewAdapterMessages.Listener {

    private RecyclerView recyclerView;
    private EditText editText;
    private RecyclerViewAdapterMessages adapterMessages;
    private TextView textView, fullName;
    private String secondUserId;
    private String idChat;
    private ImageView secondUserPic;
    private ChatViewModel chatViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_discussion);

        initElement();
        this.configureRecyclerView();
    }

    private void configureRecyclerView() {
        this.adapterMessages = new RecyclerViewAdapterMessages(generateOptionsForAdapter
                (chatViewModel.getAllChat(idChat)), this, Objects.requireNonNull(this.getCurrentUser()).getUid());
        adapterMessages.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(adapterMessages.getItemCount());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.adapterMessages);
    }

    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void initElement() {
        UserViewModel userViewModel = new UserViewModel();
        recyclerView = findViewById(R.id.recyclerview_chat);
        editText = findViewById(R.id.edittext_chat);
        Button button = findViewById(R.id.button_send_message_chat);
        textView = findViewById(R.id.noconversation_textview);
        button.setOnClickListener(v -> onClickSendMessage());
        secondUserId = getIntent().getStringExtra("idSecondUser");
        LiveData<User> secondUser = userViewModel.getUserObject(secondUserId);
        secondUserPic = findViewById(R.id.seconduser_pic);
        fullName = findViewById(R.id.fullname_conversation);
        secondUser.observe(this, this:: initPicture);
        idChat = updateListUserSort(Objects.requireNonNull(getCurrentUser()).getUid(), secondUserId);
        chatViewModel = new ChatViewModel();
    }

    private void initPicture(User user) {
        String fullName = user.getFirstName() + " "+ user.getLastName();
        this.fullName.setText(fullName);
        if (user.getUrlPicture() != null){
            FirebaseStorage.getInstance().getReference(user.getUrlPicture()).getDownloadUrl().addOnSuccessListener(uri -> Glide.with(ChatDiscussion.this)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(secondUserPic));
        }
    }

    private void onClickSendMessage() {

        if (!TextUtils.isEmpty(editText.getText().toString())){

            chatViewModel.createMessage(editText.getText().toString(), idChat, Objects.requireNonNull(getCurrentUser()).getUid(), secondUserId);
            this.editText.setText("");
        }
    }

    @Override
    public void onDataChanged() {
        if (this.adapterMessages.getItemCount() == 0)
            textView.setVisibility(View.VISIBLE);
        else textView.setVisibility(View.GONE);
    }

    public String updateListUserSort(String currentUserId, String mSecondUserId) {
        List<String> originalList = new ArrayList<>();
        originalList.add(currentUserId);
        originalList.add(mSecondUserId);
        originalList.sort(new Tool.IdAZComparator());
        return originalList.get(0) + originalList.get(1);
    }


    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}