package com.jins_meme.visualizing_blinks_and_6axis;

import android.content.Intent;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.jins_jp.meme.MemeConnectListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeRealtimeData;
import com.jins_jp.meme.MemeRealtimeListener;

public class LiveViewActivity extends AppCompatActivity {

    // TODO : Replace APP_ID and APP_SECRET
    private static final String APP_ID = "App ID";
    private static final String APP_SECRET = "App Secret";

    private FrameLayout blinkLayout;
    private ImageView blinkImage;
    private VideoView blinkView;
    private FrameLayout bodyLayout;
    private ImageView bodyImage;
    private TextView statusLabel;
    private Button connectButton;

    private MemeLib memeLib;

    final private MemeConnectListener memeConnectListener = new MemeConnectListener() {
        @Override
        public void memeConnectCallback(boolean b) {
            //describe actions after connection with JINS MEME
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeViewStatus(true);
                }
            });
        }

        @Override
        public void memeDisconnectCallback() {
            //describe actions after disconnection from JINS MEME
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeViewStatus(false);
                    Toast.makeText(LiveViewActivity.this, "DISCONNECTED", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_view);
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //Sets MemeConnectListener to get connection result.
        memeLib.setMemeConnectListener(memeConnectListener);

        changeViewStatus(memeLib.isConnected());

        //Starts receiving realtime data if MEME is connected
        if (memeLib.isConnected()) {
            memeLib.startDataReport(memeRealtimeListener);
        }
    }

    private void init() {
        //Authentication and authorization of App and SDK
        MemeLib.setAppClientID(getApplicationContext(), APP_ID, APP_SECRET);
        memeLib = MemeLib.getInstance();

        blinkLayout = (FrameLayout)findViewById(R.id.blink_layout);

        blinkImage = (ImageView)findViewById(R.id.blink_image);

        blinkView = (VideoView)findViewById(R.id.blink_view);
        blinkView.setZOrderOnTop(true);
        blinkView.setVideoPath("android.resource://" + this.getPackageName() + "/" + R.raw.blink);
        blinkView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0);
            }
        });

        bodyLayout = (FrameLayout)findViewById(R.id.body_layout);

        bodyImage = (ImageView)findViewById(R.id.body_image);

        statusLabel = (TextView)findViewById(R.id.status_label);

        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (memeLib.isConnected()) {
                    memeLib.disconnect();
                } else {
                    Intent intent = new Intent(LiveViewActivity.this, ConnectActivity.class);
                    startActivity(intent);
                }
            }
        });

        changeViewStatus(memeLib.isConnected());
    }

    private void changeViewStatus(boolean connected) {
        if (connected) {
            statusLabel.setText(R.string.connected);
            statusLabel.setBackgroundColor(ContextCompat.getColor(this, R.color.black));

            connectButton.setBackground(ContextCompat.getDrawable(this, R.drawable.disconnect_button));

            blinkLayout.setAlpha(1.0f);
            blinkView.setVisibility(View.VISIBLE);
            bodyLayout.setAlpha(1.0f);
        } else {
            statusLabel.setText(R.string.not_connected);
            statusLabel.setBackgroundColor(ContextCompat.getColor(this, R.color.red));

            connectButton.setBackground(ContextCompat.getDrawable(this, R.drawable.connect_button));

            blinkImage.setVisibility(View.VISIBLE);
            blinkLayout.setAlpha(0.2f);
            blinkView.setVisibility(View.INVISIBLE);
            bodyLayout.setAlpha(0.2f);
        }
    }


    private final MemeRealtimeListener memeRealtimeListener = new MemeRealtimeListener() {
        @Override
        public void memeRealtimeCallback(final MemeRealtimeData memeRealtimeData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateMemeData(memeRealtimeData);
                }
            });
        }
    };

    private void updateMemeData(MemeRealtimeData d) {

        // for blink
        Log.d("LiveViewActivity", "Blink Speed:" + d.getBlinkSpeed());
        if (d.getBlinkSpeed() > 0) {
            blinkImage.setVisibility(View.INVISIBLE);
            blink();
        }

        // for body (Y axis rotation)
        double radian = Math.atan2(d.getAccX(), d.getAccZ());
        rotate(Math.toDegrees(-radian)); // for mirroring display(radian x -1)
    }

    private void blink(){
        blinkView.seekTo(0);
        blinkView.start();
    }

    private void rotate(double degree) {
        int width = bodyImage.getDrawable().getBounds().width();
        int height = bodyImage.getDrawable().getBounds().height();

        Matrix matrix = new Matrix();
        bodyImage.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate((float)degree, width/2, height/2);
        matrix.postScale(0.5f, 0.5f);
        bodyImage.setImageMatrix(matrix);
    }
}
