package msharp;

import javafx.scene.media.MediaPlayer;

public class TimeMedia 
{
    private int currentMinute;
    private int currentSecond;
    private int maxMinute;
    private int maxSecond;

    private String currentTime = "00:00";
    private String maxTime = "00:00";

    public String getCurrentTime()
    {
        return currentTime;
    }

    public String getMaxTime()
    {
        return maxTime;
    }
    
    public void SetStartTime(MediaPlayer mediaPlayer)
    {
        currentTime = "00:00";

        maxMinute = (int)mediaPlayer.getTotalDuration().toSeconds() / 60;
        maxSecond = (int)mediaPlayer.getTotalDuration().toSeconds() % 60;

        if (maxMinute < 10 && maxSecond < 10)
            maxTime = "0" + Integer.toString(maxMinute) + ":0" + Integer.toString(maxSecond);
        
        else if (maxMinute >= 10 && maxSecond < 10)
            maxTime = Integer.toString(maxMinute) + ":0" + Integer.toString(maxSecond);
        
        else if (maxMinute < 10 && maxSecond >= 10)
            maxTime = "0" + Integer.toString(maxMinute) + ":" + Integer.toString(maxSecond);        
        
        else
        {
            maxTime = Integer.toString(maxMinute) + ":" + Integer.toString(maxSecond);
        }
    }

    public void SetTimeUpdated(MediaPlayer mediaPlayer)
    {
        currentMinute = (int)(mediaPlayer.getCurrentTime().toSeconds()) / 60;
        currentSecond = (int)(mediaPlayer.getCurrentTime().toSeconds()) % 60;

        if (currentMinute < 10 && currentSecond < 10)
            currentTime = "0" + Integer.toString(currentMinute) + ":0" + Integer.toString(currentSecond);
        
        else if (currentMinute >= 10 && currentSecond < 10)
            currentTime = Integer.toString(currentMinute) + ":0" + Integer.toString(currentSecond);
        
        else if (currentMinute < 10 && currentSecond >= 10)
            currentTime = "0" + Integer.toString(currentMinute) + ":" + Integer.toString(currentSecond);        
        
        else
        {
            currentTime = Integer.toString(currentMinute) + ":" + Integer.toString(currentSecond);
        }
    }
}
