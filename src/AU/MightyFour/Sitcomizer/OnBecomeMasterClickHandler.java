package AU.MightyFour.Sitcomizer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: maratx
 * Date: 11/13/13
 * Time: 2:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class OnBecomeMasterClickHandler implements View.OnClickListener {
    public OnBecomeMasterClickHandler(Activity parentActivity) {
        _parentActivity = parentActivity;
    }

    @Override
    public  void onClick(View view)
    {
        final BluetoothAdapter defaultBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(defaultBluetoothAdapter == null) {
            Log.d(TAG, "null BTAdapter!");
            return;
        }

        if (defaultBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "BTAdapter is enabled!");
        }
        else
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            _parentActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        _deviceDiscoveredMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice slaveDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d(TAG, "Device added: " + slaveDevice.getName() + " address: " + slaveDevice.getAddress());
                    MasterThread masterThread = new MasterThread(slaveDevice);
                    masterThread.start();
                    defaultBluetoothAdapter.cancelDiscovery();
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        _parentActivity.registerReceiver(_deviceDiscoveredMessageReceiver, filter);
        // Не забудьте снять регистрацию в onDestroy
    }

    private final static int REQUEST_ENABLE_BT = 1;
    private final Activity _parentActivity;
    private final String TAG = "OnBecomeMasterClickHandler";
    private BroadcastReceiver _deviceDiscoveredMessageReceiver;
}
