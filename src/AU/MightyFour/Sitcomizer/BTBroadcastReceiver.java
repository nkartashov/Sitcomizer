package AU.MightyFour.Sitcomizer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: maratx
 * Date: 11/13/13
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class BTBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d(MainActivity.TAG, "Device found: " + device.getName());
            String deviceName = device.getName();
            if(deviceName.endsWith(MainActivity.SUFFIX)) {
                MainActivity.discoveredDevices.add(device);
                Log.d(MainActivity.TAG, "Device added: " + device.getName());
            }
        }
    }
}
