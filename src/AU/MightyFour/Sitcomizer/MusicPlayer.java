package AU.MightyFour.Sitcomizer;

import android.app.Activity;
import android.media.MediaPlayer;

/**
 * Created with IntelliJ IDEA.
 * User: nikita_kartashov
 * Date: 12/11/2013
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class MusicPlayer
{
	public static void initializePlayer(Activity activity)
	{
		_playerActivity = activity;
	}

	public static void createAndLaunchPlayer(int rawId)
	{
		if (_player != null)
			_player.release();

		_player = MediaPlayer.create(_playerActivity, rawId);
		_player.start();

		_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer player) {
				player.release();
			}
		});
	}

	private static Activity _playerActivity;
	private static MediaPlayer _player = null;
}
