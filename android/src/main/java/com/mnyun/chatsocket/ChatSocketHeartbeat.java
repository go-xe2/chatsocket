package com.mnyun.chatsocket;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChatSocketHeartbeat implements Heartbeat {
    private ScheduledFuture heartbeatFuture = null;
    private ScheduledExecutorService service = null;
    private WeakReference<ChatSocket> socket;
    private static final int MSG_STOP_SIGN = 1;
    private static final String PING_MSG_TEXT = "ping";
    private long delaySecond = 120; // 第一次执行迟延时间（秒）
    private long intervalSecond = 180; // 执行间隔时间(秒)
    public ChatSocketHeartbeat(ChatSocket socket) {
        super();
        this.socket = new WeakReference<>(socket);
        this.heartbeatFuture = null;
        this.service = null;
    }

    public ChatSocketHeartbeat(ChatSocket socket, long delaySecond, long intervalSecond) {
        this(socket);
        this.delaySecond = delaySecond;
        this.intervalSecond = intervalSecond;
    }

    private ChatSocket getSocket() {
        return this.socket.get();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == MSG_STOP_SIGN) {
                ChatSocketHeartbeat.this.stop();
            }
        }
    };

    private Runnable heartbeatRunnable = new Runnable() {
        //创建 run 方法
        public void run() {
            // 发送心跳包
            try {
                ChatSocket socket = ChatSocketHeartbeat.this.getSocket();
                if (socket == null) {
                    Message msg = new Message();
                    msg.arg1 = MSG_STOP_SIGN;
                    handler.sendMessage(msg);
                }
                socket.send(PING_MSG_TEXT);
            } catch (Exception e) {
            }
        }
    };

    @Override
    public void start() {
        if (this.service != null) {
            return;
        }
        // ScheduledExecutorService:是从Java SE5的java.util.concurrent里，
        // 做为并发工具类被引进的，这是最理想的定时任务实现方式。
        this.service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        // 120：秒   180：秒
        // 第一次执行的时间为10秒，然后每隔五秒执行一次
        this.heartbeatFuture = this.service.scheduleAtFixedRate(heartbeatRunnable, this.delaySecond, this.intervalSecond, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        if (this.heartbeatFuture != null) {
            this.heartbeatFuture.cancel(true);
            this.heartbeatFuture = null;
        }
        if (this.service != null) {
            this.service.shutdown();
            this.service = null;
        }
    }
}
