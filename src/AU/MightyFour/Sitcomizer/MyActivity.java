package com.example.FromChat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {
    public static final String TAG = "MyActivity";
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.main);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        mChatService = new BluetoothChatService(this, mHandler);
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
        mBluetoothAdapter.setName(mBluetoothAdapter.getName().replace(SUFFIX, ""));
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
            Toast.makeText(MyActivity.this, "No devices found yet", Toast.LENGTH_SHORT).show();
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
