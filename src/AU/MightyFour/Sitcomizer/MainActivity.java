package AU.MightyFour.Sitcomizer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

        if(USE_BLUETOOTH) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the chat session
            }
            else {
                if (mChatService == null) setupChat();
            }

            if (mChatService != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                    // Start the Bluetooth chat services
                    mChatService.start();
                }
            }
        }
	}

    @Override
    protected void onDestroy() {
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
        mBluetoothAdapter.setName(mBluetoothAdapter.getName().replace(SUFFIX, ""));
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
			_gestureEventListener.addHandler(gesture, null);
	}

	private void setGestureEventListener()
	{
		_gestureEventListener = new GestureEventListener();

		_gestureEventListener.setInactive();

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
        inflateButton(result, R.id.up_right_button, "YES, MY MASTER", R.raw.lin_darth_vader_yes_my_master);
        inflateButton(result, R.id.bottom_left_button, "DARTH VADER", R.raw.lin_darth_vader_dont_make_me_destroy_you);
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
	public static final String TAG = "MainActivity";

    public static List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private static final boolean D = true;
    public static final String SUFFIX = ".sitcomize";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        mChatService = new BluetoothChatService(this, mHandler);
    }

    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(discoverableIntent);
        }
    }

    private void sendMessage(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "Device not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.d(TAG, "MESSAGE_STATE_CHANGE: " + TranslateState(msg.arg1));
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    Toast.makeText(getApplicationContext(), "Command sent", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    Log.d(TAG, "COMMAND RECEIVED!!!!!!!!!!!!!!!");
                    Toast.makeText(getApplicationContext(), "Playing melody! = )", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                }
                else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "BT enabling problem", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    //func
    public static String TranslateState(int state) {
        switch (state) {
            case BluetoothChatService.STATE_NONE:
                return "STATE_NONE";
            case BluetoothChatService.STATE_LISTEN:
                return "STATE_LISTEN";
            case BluetoothChatService.STATE_CONNECTING:
                return "STATE_CONNECTING";
            case BluetoothChatService.STATE_CONNECTED:
                return "STATE_CONNECTED";
        }
        return "UNKNOWN_STATE";
    }

    //buttons
    public void onMasterClick(View v) {
        Log.d(TAG, "masterButton clicked");
        if(discoveredDevices.size() == 0) {
            Toast.makeText(MainActivity.this, "No devices found yet", Toast.LENGTH_SHORT).show();
        }
        else {
            mBluetoothAdapter.cancelDiscovery();
            for(BluetoothDevice dev : discoveredDevices) {
                mChatService.connect(dev, false);
                discoveredDevices.remove(dev);
            }
        }
    }

    public void onSlaveClick(View v) {
        if(!mBluetoothAdapter.getName().endsWith(SUFFIX))
            mBluetoothAdapter.setName(mBluetoothAdapter.getName() + SUFFIX);
        Log.d(TAG, "slaveButton clicked");
        ensureDiscoverable();
    }

    public void onCancelClick(View v) {
        Log.d(TAG, "cancelButton clicked");
        mBluetoothAdapter.cancelDiscovery();
        mChatService.stop();
    }

    public void onDiscoveryClick(View v) {
        Log.d(TAG, "discoveryButton clicked");
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    public void onSendClick(View v) {
        Log.d(TAG, "sendButton clicked");
        sendMessage("adsf");
    }
}
