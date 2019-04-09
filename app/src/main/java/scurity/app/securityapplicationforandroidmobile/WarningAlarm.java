package scurity.app.securityapplicationforandroidmobile;

import android.content.Context;
import android.media.MediaPlayer;

public class WarningAlarm {
    private static MediaPlayer mediaPlayer = null;

    public static void play(Context context, int resource)
    {
        stop(context);

        mediaPlayer = MediaPlayer.create(context, resource);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public static void stop(Context context)
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
