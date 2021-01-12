package com.mnyun.chatsocket;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class ChatService extends Service {
    public ChatService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String IMHost() {
        return this.getCustomPropertyFromStringsIfExist("IMHost");
    }
    private String getCustomPropertyFromStringsIfExist(String propertyName) {
        String property;

        String packageName = this.getPackageName();
        int resId = this.getResources().getIdentifier("ChatSocket" + propertyName, "string", packageName);

        if (resId != 0) {
            property = this.getString(resId);
            if (!property.isEmpty()) {
                return property;
            } else {
                ChatSocketUtils.log("Specified " + propertyName + " is empty");
            }
        }
        return null;
    }
}
