package AU.MightyFour.Sitcomizer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from (this);
        List<View> pages = new ArrayList<View>();

        View page = inflateNegativeEmotionPage();
        pages.add (page);

        page = inflatePositiveEmotionsPage();
        pages.add (page);

        page = inflater.inflate(R.layout.oneliners_view, null);
        pages.add (page);

        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(1);

        setContentView(viewPager);
    }

	private void createAndLaunchPlayer(int rawId)
	{
		if(_mediaPlayer != null)
			_mediaPlayer.release();

		_mediaPlayer = MediaPlayer.create(this, rawId);
		_mediaPlayer.start();

		_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
		{
			@Override
			public void onCompletion(MediaPlayer player)
			{
				player.release();
			}
		});
	}

	private View inflatePositiveEmotionsPage()
	{
		LayoutInflater inflater = LayoutInflater.from (this);
		View result = inflater.inflate(R.layout.buttons_view, null);
		inflateButton(result, R.id.central_button, "Laugh", R.raw.pos_laugh);
		inflateButton(result, R.id.up_left_button, "Cheer", R.raw.pos_cheer);
		inflateButton(result, R.id.up_right_button, "Aww", R.raw.pos_aww);
		inflateButton(result, R.id.bottom_left_button, "Applause", R.raw.pos_applause);
		inflateButton(result, R.id.bottom_right_button, "Yeah, baby!", R.raw.pos_yeah_baby);

		return result;
	}

	private View inflateNegativeEmotionPage()
	{
		LayoutInflater inflater = LayoutInflater.from (this);
		View result = inflater.inflate(R.layout.buttons_view, null);
		inflateButton(result, R.id.up_left_button, "Boo", R.raw.neg_boo);
		inflateButton(result, R.id.up_right_button, "Sad trombone", R.raw.neg_wah_wah);
		inflateButton(result, R.id.bottom_left_button, "Shocked", R.raw.neg_shocked);
		inflateButton(result, R.id.bottom_right_button, "No one is interested", R.raw.neg_crickets);

		return result;
	}

	private void inflateButton(View page, int buttonId, String text, final int soundFileId)
	{
		Button resultButton = (Button) page.findViewById(buttonId);
		resultButton.setText(text);
		resultButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				createAndLaunchPlayer(soundFileId);
			}
		});
	}

	private MediaPlayer _mediaPlayer = null;
}
