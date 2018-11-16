package com.cornez.stopwatch;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyActivity extends Activity {

    // UI ELEMENTS: BUTTONS WILL TOGGLE IN VISIBILITY
    private EditText timeDisplay;
    private Button startBtn;
    private Button stopBtn;
    private Button onOffBtn;

    // TIME ELEMENTS
    private WatchTime watchTime;
    private long timeInMilliseconds = 0L;

    // SOUND MANAGEMENT TOOLS
    private SoundPool soundPool;
    private SparseIntArray soundMap;

    // THE HANDLER FOR THE THREAD ELEMENT
    //private Handler handler = new Handler();
    private Handler mHandler;

    // INTERNAL VALUES
    boolean onOffVal = true;

    // MESSAGE CONSTANTS
    private static final int STOP_TIMER = 100;
    private static final int PAUSE_TIMER = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TASK 1: ACTIVATE THE ACTIVITY AND THE LAYOUT
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // TASK 2: CREATE REFERENCES TO UI COMPONENTS
        timeDisplay = (EditText) findViewById(R.id.textView1);
        startBtn = (Button) findViewById(R.id.button1);
        stopBtn = (Button) findViewById(R.id.button2);
        onOffBtn = (Button) findViewById(R.id.button3);

        // TASK 3: INIT THE UI
        initUI();

        // TASK 4: INSTANTIATE THE OBJECT THAT MODELS THE STOPWATCH TIME
        watchTime = new WatchTime();

        //TASK 5: INSTANTIATE A HANDLER TO RUN ON THE UI THREAD
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.arg1) {
                    case STOP_TIMER:
                        // reset UI
                        initUI();
                        // play alarm
                        soundPool.play(1, 1, 1, 1, 0, 1.0f);
                        // end timer thread
                        this.removeCallbacks(updateTimerRunnable);
                        break;
                }
            }
        };

        // INIT SOUND MANAGEMENT OBJECTS
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundMap = new SparseIntArray(1);
        soundMap.put(1, soundPool.load(this, R.raw.alarm, 1));

    }

    // sets the UI to it's default value
    private void initUI() {
        // PROGRAM SIDE
        // HIDE THE STOP BUTTON
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
//        onOffBtn.setEnabled(true);
        // set on/off to on
        setButtonOn();

        // UI SIDE
        // SET THE TIMER DISPLAY
        timeDisplay.setText("00:00");

    }

    // set the on/off to off
    private void setButtonOff() {
        onOffBtn.setText(R.string.Off);
        onOffVal = false;
    }

    // set the on/off to on
    private void setButtonOn() {
        onOffBtn.setText(R.string.On);
        onOffVal = true;
    }

    public void startTimer(View view) {
        // TASK 1: SET THE START BUTTON TO INVISIBLE
        //         AND THE STOP BUTTON TO VISIBLE
        stopBtn.setEnabled(true);
        startBtn.setEnabled(false);
        setButtonOff();

        // TASK 2: GET USER TIME INPUT
        long userTime = 0;
        String[] timeSegments = timeDisplay.getText().toString().split(":");
        // check if this is valid data, if not just set to 0
        try {
            userTime = 1000L * (60L * Long.parseLong(timeSegments[0]) + Long.parseLong(timeSegments[1]));
        } catch (NumberFormatException e) {
            // return to default UI
            initUI();
        }

        // TASK 3: SET THE START TIME AND CALL THE CUSTOM HANDLER
        // set stored time
        watchTime.setStoredTime(userTime);
        // set t0, used to calculate time decrement
        watchTime.setT0(SystemClock.uptimeMillis());
        mHandler.postDelayed(updateTimerRunnable, 20);
    }

    private Runnable updateTimerRunnable = new Runnable() {
        public void run() {

            // TASK 1: COMPUTE THE TIME DIFFERENCE
//            timeInMilliseconds = watchTime.getStoredTime() - SystemClock.uptimeMillis();
            timeInMilliseconds =  SystemClock.uptimeMillis() - watchTime.getT0();
            // reset t0
            watchTime.setT0(SystemClock.uptimeMillis());

//            watchTime.setTimeUpdate(watchTime.getStoredTime() - timeInMilliseconds);
            int time = (int) (watchTime.getStoredTime() / 1000);
            // decrement the stored time
            watchTime.subtractStoredTime(timeInMilliseconds);

            // TASK 2: COMPUTE MINUTES, SECONDS, AND MILLISECONDS
            int minutes = time / 60;
            int seconds = time % 60;
            int milliseconds = (int) (watchTime.getStoredTime() % 100);

            // TASK 3: DISPLAY THE TIME IN THE TEXTVIEW
            timeDisplay.setText(String.format("%02d", minutes) + ":"
                    + String.format("%02d", seconds));

            // TASK 4: CHECK FOR TIMER END
            if(watchTime.getStoredTime() <= 0L) {
                // end thread
                Message msg = mHandler.obtainMessage(0, STOP_TIMER, 0);
                mHandler.sendMessageAtFrontOfQueue(msg);
            }

            // TASK 5: SPECIFY NO TIME LAPSE BETWEEN POSTING
            mHandler.postDelayed(this, 10);
        }
    };

    public void stopTimer(View view) {
        // TASK 1: DISABLE THE START BUTTON
        //         AND ENABLE THE STOP BUTTON
        stopBtn.setEnabled(false);
        startBtn.setEnabled(true);
        onOffBtn.setEnabled(true);

        // TASK 2: UPDATE THE TIME SWAP VALUE AND CALL THE HANDLER
        // watchTime.addStoredTime(timeInMilliseconds);
        mHandler.removeCallbacks(updateTimerRunnable);
    }

    public void resetTimer(View view) {
        // TASK 1: CLEAR WATCH TIME
        watchTime.resetWatchTime();

        // TASK 2: END THE TIMER THREAD
        Message msg = mHandler.obtainMessage(0, STOP_TIMER, 0);
        mHandler.sendMessageAtFrontOfQueue(msg);

        // TASK 3: DISPLAY THE TIME IN THE TEXTVIEW
        setButtonOn();
        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
