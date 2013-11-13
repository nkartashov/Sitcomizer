package AU.MightyFour.Sitcomizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{
    private int RAW_ON_SHAKE;
    private int RAW_ON_WINNER;
    private int RAW_ON_TILT_LEFT;
    private int RAW_ON_TILT_RIGHT;

    private boolean USE_GESTURE;
    private boolean USE_GESTURE_PASSIVE;

    private boolean USE_SHAKE;
    private boolean USE_WINNER;
    private boolean USE_TILT_LEFT;
    private boolean USE_TILT_RIGHT;

    private boolean USE_BLUETOOTH;
    private boolean IS_MASTER;

    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSettings();

        LayoutInflater inflater = LayoutInflater.from (this);
        List<View> pages = new ArrayList<View>();

        View page = inflateNegativeEmotionPage();
        pages.add (page);

        page = inflatePositiveEmotionsPage();
        pages.add (page);

        page = inflateStarWarsEmotionPage();
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
		if (!USE_GESTURE_PASSIVE)
			_gestureEventListener.setInactive();
		super.onPause();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		setGestureSettings();

		_gestureEventListener.setGesturesOnState(USE_GESTURE);
	}

    @Override
    protected void onDestroy() {
        NetworkHelper.CloseAllSockets();
        NetworkHelper.UnregisterReceiver();
	    super.onDestroy();
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
                Intent intent_settings = new Intent(this, SettingsActivity.class);
                startActivity(intent_settings);
                return true;
            case R.id.menu_help:
                Intent intent_help = new Intent(this, HelpActivity.class);
                startActivity(intent_help);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initSettings()
    {
        PreferenceManager.setDefaultValues(this, R.xml.pref_main_gesture, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_gesture, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_network, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_alarm, false);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void setGestureSettings()
    {
        USE_GESTURE = sp.getBoolean("switch_main_key", false);
        Log.v(TAG, "USE_GESTURE " + String.valueOf(USE_GESTURE));
        USE_GESTURE_PASSIVE = sp.getBoolean("switch_passive_key", false);
        Log.v(TAG, "USE_GESTURE_PASSIVE " + String.valueOf(USE_GESTURE_PASSIVE));

        USE_SHAKE = sp.getBoolean("checkbox_shake_key", false);
        Log.v(TAG, "USE_SHAKE " + String.valueOf(USE_SHAKE));
        USE_WINNER = sp.getBoolean("checkbox_winner_key", false);
        Log.v(TAG, "USE_WINNER " + String.valueOf(USE_WINNER));
        USE_TILT_LEFT = sp.getBoolean("checkbox_tilt_left_key", false);
        Log.v(TAG, "USE_TILT_LEFT " + String.valueOf(USE_TILT_LEFT));
        USE_TILT_RIGHT = sp.getBoolean("checkbox_tilt_right_key", false);
        Log.v(TAG, "USE_TILT_RIGHT " + String.valueOf(USE_TILT_RIGHT));

        USE_BLUETOOTH = sp.getBoolean("switch_bluetooth_key", false);
        Log.v(TAG, "USE_BLUETOOTH " + String.valueOf(USE_BLUETOOTH));
        if (sp.getString("list_roles_key", "MASTER").equals("MASTER")) {
            IS_MASTER = true;
        }
        else {
            IS_MASTER = false;
        }
        Log.v(TAG, "IS_MASTER " + String.valueOf(IS_MASTER));


        try {
            RAW_ON_SHAKE = R.raw.class.getField(sp.getString("list_shake_key", "pos_applause"))
                    .getInt(R.raw.class.getField(sp.getString("list_shake_key", "pos_applause")));

            RAW_ON_WINNER = R.raw.class.getField(sp.getString("list_winner_key", "pos_applause"))
                    .getInt(R.raw.class.getField(sp.getString("list_winner_key", "pos_applause")));

            RAW_ON_TILT_LEFT = R.raw.class.getField(sp.getString("list_tilt_left_key", "pos_applause"))
                    .getInt(R.raw.class.getField(sp.getString("list_tilt_left_key", "pos_applause")));

            RAW_ON_TILT_RIGHT = R.raw.class.getField(sp.getString("list_tilt_right_key", "pos_applause"))
                    .getInt(R.raw.class.getField(sp.getString("list_tilt_right_key", "pos_applause")));


	        updateGestureHandler(GestureTypes.SHAKE_GESTURE, RAW_ON_SHAKE, USE_SHAKE);
	        updateGestureHandler(GestureTypes.WINNER_GESTURE, RAW_ON_WINNER, USE_WINNER);
	        updateGestureHandler(GestureTypes.TILT_LEFT_GESTURE, RAW_ON_TILT_LEFT, USE_TILT_LEFT);
	        updateGestureHandler(GestureTypes.TILT_RIGHT_GESTURE, RAW_ON_TILT_RIGHT, USE_TILT_RIGHT);
        }
        catch (Exception e) {}
    }

	private void updateGestureHandler(Integer gesture, Integer handler, boolean gestureState)
	{
		if (gestureState)
			_gestureEventListener.addHandler(gesture, handler);
		else
			_gestureEventListener.removeGestureHandler(gesture);
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
	}

	private View inflatePositiveEmotionsPage()
	{
		LayoutInflater inflater = LayoutInflater.from (this);
		View result = inflater.inflate(R.layout.positive_emo_view, null);
		inflateButton(result, R.id.central_button, "LAUGH", R.raw.pos_laugh);
		inflateButton(result, R.id.up_left_button, "CHEER", R.raw.pos_cheer);
		inflateButton(result, R.id.up_right_button, "AWW", R.raw.pos_aww);
		inflateButton(result, R.id.bottom_left_button, "APPLAUSE", R.raw.pos_applause);
		inflateButton(result, R.id.bottom_right_button, "YEAH, BABY!", R.raw.pos_yeah_baby);

		return result;
	}

	private View inflateNegativeEmotionPage()
	{
		LayoutInflater inflater = LayoutInflater.from (this);
		View result = inflater.inflate(R.layout.negative_emo_view, null);
		inflateButton(result, R.id.up_left_button, "BOO", R.raw.neg_boo);
		inflateButton(result, R.id.up_right_button, "SAD TROMBONE", R.raw.neg_wah_wah);
		inflateButton(result, R.id.bottom_left_button, "SHOCKED", R.raw.neg_shocked);
		inflateButton(result, R.id.bottom_right_button, "BA DUM TSS", R.raw.snd_ba_dum_tss);

		return result;
	}

    private View inflateStarWarsEmotionPage()
    {
        LayoutInflater inflater = LayoutInflater.from (this);
        View result = inflater.inflate(R.layout.starwars_emo_view, null);
        inflateButton(result, R.id.up_left_button, "CHUBAKKA RAWRR", R.raw.lin_chubakka_rawrr);
        inflateButton(result, R.id.up_right_button, "DARTH VADER", R.raw.lin_darth_vader_dont_make_me_destroy_you);
        inflateButton(result, R.id.bottom_left_button, "YES, MY MASTER", R.raw.lin_darth_vader_yes_my_master);
        inflateButton(result, R.id.bottom_right_button, "I'L BE BACK", R.raw.movie_ilbeback);

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
