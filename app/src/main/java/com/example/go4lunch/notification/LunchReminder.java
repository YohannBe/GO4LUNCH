package com.example.go4lunch.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.DetailRestaurant;
import com.example.go4lunch.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LunchReminder extends BroadcastReceiver {
    private Context mContext;
    private String response, name;


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        response = intent.getStringExtra("restaurantId");
        name = intent.getStringExtra("restaurantName");

        buildNotification();


    }

    private void buildNotification() {
        String notificationString = "You have chosen "+name;
        Intent intent = new Intent(mContext, DetailRestaurant.class);
        intent.putExtra("restaurantId", response);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("GO4LUNCH notification");
        inboxStyle.addLine(notificationString);

        String channelId = "channel";

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mContext, channelId)
                        .setSmallIcon(R.drawable.food_icons)
                        .setContentTitle("GO4LUNCH")
                        .setContentText("Don't forget your reservation !")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setStyle(inboxStyle);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationManagerCompat.notify(8, notificationBuilder.build());

    }

}
