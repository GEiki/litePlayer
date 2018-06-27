package com.dedaodemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dedaodemo.common.Constant;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;

import java.util.ArrayList;
import java.util.LinkedList;

public class DownloadService extends Service {

    public static final int MSG_SINGLE_DOWNLOAD = 1;
    public static final int MSG_MUTI_DOWNLOAD = 2;
    public static final String ARG_TITLE = "title";
    public static final String ARG_URL = "url";

    LinkedList<String> taskStack = new LinkedList<>();


    Messenger messenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg != null){
                switch (msg.what){
                    case MSG_SINGLE_DOWNLOAD:{
                        final Bundle bundle = msg.getData();
                        DownloadTask task = new DownloadTask.Builder(bundle.getString(ARG_URL), Constant.DOWNLOAD_PATH,bundle.getString(ARG_TITLE)+".mp3")
                                .setPassIfAlreadyCompleted(false)
                                .setMinIntervalMillisCallbackProcess(30)
                                .build();
                        DownloadListener listener = new DownloadListener1() {
                            @Override
                            public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
                                    taskStack.push(bundle.getString(ARG_TITLE));

                            }

                            @Override
                            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

                            }

                            @Override
                            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

                            }

                            @Override
                            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {

                            }

                            @Override
                            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
                                //处理网络中断


                                taskStack.pop();
                                //回调title
                                String title = bundle.getString(ARG_TITLE);
                                if(taskStack.isEmpty()){
                                    stopSelf();
                                }


                            }
                        };
                        singleDownload(task,listener);
                        break;
                    }
                    case MSG_MUTI_DOWNLOAD:{
                        break;

                    }
                    default:break;
                }

            }
        }
    });

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void mutiDownload(DownloadTask[] tasks,DownloadListener listener){
        DownloadTask.enqueue(tasks,listener);
    }

    private void singleDownload(DownloadTask task,DownloadListener listener){
        task.enqueue(listener);
    }
}
