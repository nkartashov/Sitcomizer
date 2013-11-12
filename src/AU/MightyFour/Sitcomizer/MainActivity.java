package AU.MightyFour.Sitcomizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
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

        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(1);

	    Log.v(TAG, "all pages are initialized");

	    setContentView(viewPager);

	    MusicPlayer.initializePlayer(this);
	    setGestureEventListener();
    }


	@Override
	public void onPause()
	{
		_gestureEventListener.setInactive();
		super.onPause();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		_gestureEventListener.setActive();
	}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_help:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	private void setGestureEventListener()
	{
		_gestureEventListener = new GestureEventListener();

		_gestureEventListener.setActive();

		SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(
			_gestureEventListener.accelerationEventListener(),
			sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
			SensorManager.SENSOR_DELAY_NORMAL);

		sensorManager.registerListener(
			_gestureEventListener.gyroscopeEventListener(),
			sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
			SensorManager.SENSOR_DELAY_NORMAL);

		_gestureEventListener.addHandler(GestureTypes.SHAKE_GESTURE, R.raw.snd_shotgun_reload);
		//_gestureEventListener.addHandler(GestureTypes.WINNER_GESTURE, R.raw.pos_applause);
		_gestureEventListener.addHandler(GestureTypes.TILT_LEFT_GESTURE, R.raw.snd_lightsaber_on);
	}

	private View inflatePositiveEmotionsPage()
	{
		LayoutInflater inflater = LayoutInflater.from (this);
		View result = inflater.inflate(R.layout.positive_emo_view, null);
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
		View result = inflater.inflate(R.layout.negative_emo_view, null);
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
				MusicPlayer.createAndLaunchPlayer(soundFileId);
			}
		});
	}

	private GestureEventListener _gestureEventListener;
	private final String TAG = "MainActivity";
}
