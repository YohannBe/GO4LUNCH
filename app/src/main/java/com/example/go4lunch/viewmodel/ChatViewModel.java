package com.example.go4lunch.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.go4lunch.repository.RepositoryMessages;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

public class ChatViewModel extends ViewModel {

    private final RepositoryMessages repositoryMessages;

    public ChatViewModel() {
        this.repositoryMessages = RepositoryMessages.getInstance();
    }

    public Query getAllChat(String chat) {
        return repositoryMessages.getAllMessagesForChat(chat);
    }

    public Task<DocumentReference> createMessage(String textMessage, String chat, String sender, String receiver) {
        return repositoryMessages.createMessage(textMessage, chat, sender, receiver);
    }
}
