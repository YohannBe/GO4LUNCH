package com.example.go4lunch.api;

import com.example.go4lunch.model.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

public final class MessageHelper {

    private static final String Document_NAME = "messages";
    private static volatile MessageHelper instance;

    private MessageHelper(){}

    public static MessageHelper getInstance(){
        MessageHelper result = instance;
        if (result!=null){
            return result;
        }
        synchronized (MessageHelper.class){
            if (instance == null){
                instance = new MessageHelper();
            }
            return instance;
        }
    }


    public  Query getAllMessagesForChat(String chat){
        return ChatHelper.getChatCollection()
                .document(Document_NAME)
                .collection(chat)
                .orderBy("timeStamp")
                .limit(20);
    }

    public  Task<DocumentReference> createMessage(String textMessage, String chat, String sender, String receiver){
        Message message = new Message(sender, receiver, textMessage );

        return ChatHelper.getChatCollection()
                .document(Document_NAME)
                .collection(chat)
                .add(message);
    }

}
