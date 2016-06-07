package com.example.laddieri.audiotrackexample;


import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity {

    Thread t;
    int sr = 44100;
    boolean isRunning = false;
    boolean playing =false;

    SeekBar fSlider;
    double sliderval;
    Button play, stop;
    AudioTrack audiotrack;
    Double frequency;
    Double temposliderval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fSlider = (SeekBar) findViewById(R.id.frequency);

        play = (Button)findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                playTone();
            }
        });

        //stop button
        stop = (Button)findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                audiotrack.stop();
            }
        });

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) sliderval = progress / (double) seekBar.getMax();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };

        fSlider.setOnSeekBarChangeListener(listener);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        isRunning=false;
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t=null;
    }


    public void playTone()
    {
        t = new Thread()
        {
            public void run()
            {
                t.setPriority(Thread.MAX_PRIORITY);

                int buffersize = AudioTrack.getMinBufferSize(sr, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                audiotrack = new AudioTrack(AudioManager.STREAM_MUSIC, sr,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                        buffersize, AudioTrack.MODE_STREAM);

                short samples[] = new short[buffersize];

                int amplitude = 10000;

                double twopi = 2 * Math.PI;

                double ph = 0.0;

                audiotrack.play();

                double r = 1.0594630943593; //the 12th root of 2

                frequency = 261.63;
                for (int k = 1; k < 9; k++) //number of notes played
                {
                    frequency *= r;
                    for (int i = 0; i < 4 + 4 ; i++) //duration of each note, if i = 50, note duration = 12 seconds; minimum tempo = 60 bpm
                    {
                        for (int j = 0; j < buffersize; j++)
                        {
                            samples[j] = (short) (amplitude * Math.sin(ph));
                            ph += twopi * frequency / sr;
                        }
                        audiotrack.write(samples, 0, buffersize);
                    }
                }
            }
        };
        t.start();
    }

}