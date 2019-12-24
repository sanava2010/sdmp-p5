package com.sdmp.clipserver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.sdmp.common.music.callback;
import com.sdmp.common.music.IMusicPlayerInterface;
public class musicPlayerService extends Service {
    private Notification notification ;

    private static String CHANNEL_ID = "Music player style" ;
    @Override
    public void onCreate() {
        super.onCreate();
        this.createNotificationChannel();

        // Create a notification area notification so the user
        // can get back to the MusicServiceClient

        final Intent notificationIntent = new Intent();//new Intent(getApplicationContext(),musicPlayerService.class);
        notificationIntent.setComponent(new ComponentName("com.sdmp.audioclient", "com.sdmp.audioclient.MainActivity"));
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT) ;

        notification =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setOngoing(true).setContentTitle("Music Service")
                        .setContentText("Music Service is running")
                        .setTicker("Music is playing!")
                        .setFullScreenIntent(pendingIntent, false)
                        .build();
        startForeground(1,notification);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music player notification";
            String description = "The channel for music player notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private MediaPlayer mp;
    int pauseLength;
    private final IMusicPlayerInterface.Stub mBinder= new IMusicPlayerInterface.Stub() {

        @Override
        public void play(int song, final callback cb) throws RemoteException {
            if(mp!=null && mp.isPlaying())
            {
                Log.i("Service","Another song can't be played");
                return;
            }
            mp=MediaPlayer.create(getApplicationContext(),R.raw.music1);
            if (mp!=null) {
                switch(song)
                {
                    case 1:
                        break;
                    case 2:
                        mp=MediaPlayer.create(getApplicationContext(),R.raw.music2);
                        break;
                    case 3:
                        mp=MediaPlayer.create(getApplicationContext(),R.raw.music3);
                        break;
                    case 4:
                        mp=MediaPlayer.create(getApplicationContext(),R.raw.music4);
                        break;
                    case 5:
                        mp=MediaPlayer.create(getApplicationContext(),R.raw.music5);
                        break;
                }
                Log.i("Service","Playing song!");
                mp.setLooping(false);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        Log.i("Service","Done!");
                        try {
                            cb.songDone();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }

        }

        @Override
        public void pause() throws RemoteException {
            if(mp!=null)
            {
                Log.i("Service","Pausing song");
                mp.pause();
                pauseLength=mp.getCurrentPosition();
            }
        }

        @Override
        public void resume() throws RemoteException {
            if(mp!=null)
            {
                Log.i("Service","Resuming song");
                mp.seekTo(pauseLength);
                mp.start();
            }
        }


        @Override
        public void stopAndUnbind() throws RemoteException {
            if(mp!=null)
            {
                Log.i("Service","Stopping and unbinding");
                mp.stop();


            }
        }


    };
    public musicPlayerService() {
        //mp=MediaPlayer.create(this,R.raw.music1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
}
