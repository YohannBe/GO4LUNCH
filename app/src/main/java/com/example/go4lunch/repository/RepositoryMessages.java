package com.example.go4lunch.repository;

import com.example.go4lunch.api.MessageHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

public class RepositoryMessages {

    private static volatile RepositoryMessages instance;
    private final MessageHelper messageHelper;

    public RepositoryMessages() {
        messageHelper = MessageHelper.getInstance();
    }

    public static RepositoryMessages getInstance() {
        RepositoryMessages result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RepositoryMessages.class) {
            if (instance == null) {
                instance = new RepositoryMessages();
            }
            return instance;
        }
    }

    public Query getAllMessagesForChat(String chat) {
        return messageHelper.getAllMessagesForChat(chat);
    }

    public Task<DocumentReference> createMessage(String textMessage, String chat, String sender, String receiver) {
        return messageHelper.createMessage(textMessage, chat, sender, receiver);
    }
}
