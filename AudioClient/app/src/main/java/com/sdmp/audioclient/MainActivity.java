package com.sdmp.audioclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sdmp.common.music.IMusicPlayerInterface;
import com.sdmp.common.music.callback;

public class MainActivity extends AppCompatActivity {

    private Boolean isBound;
    private IMusicPlayerInterface mMusicPlayerService;
    Button pause,resume,stopAndUnbind,stopService;
    RadioGroup rg1;
    Boolean btRadio,btPause,btRes,btUnbind,btStop;
    private final ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder iservice) {

            mMusicPlayerService = IMusicPlayerInterface.Stub.asInterface(iservice);

            isBound = true;

        }

        public void onServiceDisconnected(ComponentName className) {

            mMusicPlayerService = null;

            isBound = false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button  bind=(Button)findViewById((R.id.buttonBind));
        pause=(Button)findViewById(R.id.buttonPause);
        resume=(Button)findViewById(R.id.buttonResume);
        stopAndUnbind=(Button)findViewById(R.id.buttonUnbind);
        stopService=(Button)findViewById(R.id.buttonStop);
        rg1 = (RadioGroup)findViewById(R.id.radioGroup);
        Log.i("OnCreate","Saved instance state is  null");
        for(int i = 0; i < rg1.getChildCount(); i++){
            ((RadioButton)rg1.getChildAt(i)).setEnabled(false);
        }
        pause.setEnabled(false);
        resume.setEnabled(false);
        stopAndUnbind.setEnabled(false);
        stopService.setEnabled(false);
        btPause=false;
        btRadio=false;
        btRes=false;
        btUnbind=false;
        isBound=false;
        btStop=false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Playback will be stopped").setTitle("Stop Service");
        final AlertDialog dialog = builder.create();
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBound) {

                    boolean b = false;
                    Intent i = new Intent(IMusicPlayerInterface.class.getName());

                    //Android API-20 no longer supports implicit intents
                    // to bind to a service
                    // Must make intent explicit or lower target API level to 19.
                    ResolveInfo info = getPackageManager().resolveService(i, 0);
                    i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
                    startForegroundService(i);
                    b = bindService(i, mConnection, Context.BIND_AUTO_CREATE);
                    if (b) {
                        Log.i("MainActivity", "bindService() succeeded!");
                    } else {
                        Log.i("MainActivity", "bindService() failed!");
                    }
                    for(int j = 0; j < rg1.getChildCount(); j++){
                        ((RadioButton)rg1.getChildAt(j)).setEnabled(true);
                    }
                    btRadio=true;
                    btStop=true;
                    stopService.setEnabled(true);

                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound)
                {
                    try {
                        mMusicPlayerService.pause();
                        Log.i("MainActivity", "Pause invoked");
                        resume.setEnabled(true);
                        btRes=true;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound)
                {
                    try {
                        mMusicPlayerService.resume();
                        Log.i("MainActivity", "Resuming song");
                        stopAndUnbind.setEnabled(true);
                        btUnbind=true;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        stopAndUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean b2;
                if(isBound)
                {
                    try {
                        mMusicPlayerService.stopAndUnbind();
                        Log.i("MainActivity", "Stopping song");
                        unbindService(mConnection);
                        isBound=false;

                        pause.setEnabled(false);
                        resume.setEnabled(false);
                        stopAndUnbind.setEnabled(false);
                        stopService.setEnabled(true);
                        for(int i = 0; i < rg1.getChildCount(); i++){
                            ((RadioButton)rg1.getChildAt(i)).setEnabled(false);
                        }
                        btPause=false;
                        btRadio=false;
                        btRes=false;
                        btUnbind=false;
                        btStop=true;

                        Log.i("MainActivity", "Unbinding from service");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.show();




                if(isBound)
                {
                    try {
                        mMusicPlayerService.stopAndUnbind();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    Log.i("MainActivity", "Stopping song");
                    unbindService(mConnection);
                    isBound=false;



                    //Toast.makeText(getApplicationContext(),"Music playback will be stopped",Toast.LENGTH_SHORT).show();
                }
                pause.setEnabled(false);
                resume.setEnabled(false);
                stopAndUnbind.setEnabled(false);
                stopService.setEnabled(false);
                for(int i = 0; i < rg1.getChildCount(); i++){
                    ((RadioButton)rg1.getChildAt(i)).setEnabled(false);
                }
                btPause=false;
                btRadio=false;
                btRes=false;
                btUnbind=false;
                btStop=false;
                Intent i = new Intent(IMusicPlayerInterface.class.getName());


                ResolveInfo info = getPackageManager().resolveService(i, 0);
                i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
                stopService(i);
            }
        });


    }
    public void handleRadio(View v)
    {
        boolean checked = ((RadioButton) v).isChecked();
        int song=0;
        switch (v.getId())
        {
            case R.id.radioButton1:
                if(checked)
                    song=1;
                break;
            case R.id.radioButton2:
                if(checked)
                    song=2;
                break;
            case R.id.radioButton3:
                if(checked)
                    song=3;
                break;
            case R.id.radioButton4:
                if(checked)
                    song=4;
                break;
            case R.id.radioButton5:
                if(checked)
                    song=5;
        }
        if(isBound) {
            try {
                mMusicPlayerService.play(song,mcallback);
                Log.i("MainActivity", "Play invoked with song "+song);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        pause.setEnabled(true);
        btPause=true;
    }
    private final callback.Stub mcallback=new callback.Stub() {
        @Override
        public void songDone() throws RemoteException {
            Log.i("MainActivity","Song done, callback called, unbinding...");
            unbindService(mConnection);
            isBound=false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pause.setEnabled(false);
                    resume.setEnabled(false);
                    stopAndUnbind.setEnabled(false);
                    stopService.setEnabled(true);
                    for(int i = 0; i < rg1.getChildCount(); i++){
                        ((RadioButton)rg1.getChildAt(i)).setEnabled(false);
                    }
                    btPause=false;
                    btRadio=false;
                    btRes=false;
                    btUnbind=false;
                    btStop=true;
                }
            });

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity","OnResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity","OnPause");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBound)
        {
            try {
                mMusicPlayerService.stopAndUnbind();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i("MainActivity", "Stopping song");
            unbindService(mConnection);
            isBound=false;
        }
        Intent i = new Intent(IMusicPlayerInterface.class.getName());
        ResolveInfo info = getPackageManager().resolveService(i, 0);
        i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
        stopService(i);

        /*
        if (isBound) {

            unbindService(this.mConnection);
            isBound=false;
        }

         */
    }


}
