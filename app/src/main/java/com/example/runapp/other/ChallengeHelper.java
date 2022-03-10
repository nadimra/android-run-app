package com.example.runapp.other;

/**
 * helper class to create challenges for firebase database
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-01-02
 */
public class ChallengeHelper {
    private String username;
    private int distance;
    private long timeInMillis;
    private long timestamp;

    public ChallengeHelper(){
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ChallengeHelper(String username, int distance, long timeInMillis){
        this.username = username;
        this.distance = distance;
        this.timeInMillis = timeInMillis;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public int getDistance() {
        return distance;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }
}
