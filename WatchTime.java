package com.cornez.stopwatch;

public class WatchTime {

    // TIME ELEMENTS
    private long mStartTime;
    private long mTimeUpdate;
    private long mStoredTime;
    private long t0;

    public WatchTime() {
        mStoredTime = 0L;
    }

    public void resetWatchTime() {
        mStoredTime = 0L;
    }

    public long getT0() { return t0; }
    public void setT0(long t0) { this.t0 = t0; }
    public void subtractStoredTime(long timeInMilliseconds){
        mStoredTime -= timeInMilliseconds;
    }
    public long getStoredTime(){
        return mStoredTime;
    }
    public void setStoredTime(long timeInMilliseconds) { mStoredTime = timeInMilliseconds; }
}
