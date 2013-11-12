package AU.MightyFour.Sitcomizer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: maratx
 * Date: 11/13/13
 * Time: 2:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class OnBecomeSlaveClickHandler implements View.OnClickListener {
    public OnBecomeSlaveClickHandler(Activity parentActivity)
    {
        _parentActivity = parentActivity;
    }

    @Override
    public  void onClick(View view) {


        BluetoothAdapter defaultBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

        Log.d(TAG, "Slave button pressed!");
        SlaveThread slave = new SlaveThread(defaultBluetoothAdapter);
        slave.start();
    }

    private final static int REQUEST_ENABLE_BT = 1;
    private final Activity _parentActivity;
    private final String TAG = "OnBecomeSlaveClickHandler";
}
