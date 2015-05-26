package tcc.rnapedometer;

import android.app.Activity;
import android.media.MediaPlayer;

/**
 * Created by FAGNER on 14/04/2015.
 */
public class MediaPlayerManager {

    private static Activity m_Context;

    public static void Inicialize(Activity context)
    {
        m_Context = context;
    }

    public static void PlaySound(final int soundFile)
    {
        m_Context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mediaPlayer = MediaPlayer.create(m_Context, soundFile);
/*                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                }); */
                mediaPlayer.start();
            }
        });


    }
}
