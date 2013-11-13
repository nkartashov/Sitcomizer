package AU.MightyFour.Sitcomizer;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: maratx
 * Date: 11/13/13
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class NetworkHelper {
    public static void AddSlaveSocketToList(BluetoothSocket socketToAdd) {
        _slavesSocketsList.add(socketToAdd);
    }

    public static void SetMasterSocket(BluetoothSocket masterSocket) {
        _masterSocket = masterSocket;
    }

    public static void SendCommandToSlaves(/*command*/) {

    }

    public static void CloseAllSockets() {
        try {
            _masterSocket.close();
            for(BluetoothSocket socket : _slavesSocketsList) {
                    socket.close();
            }
        }
        catch (Exception e) {
            Log.d(TAG, "exception: " + e.getMessage());
        }
    }

    public static void SetBroadcastReceiverContext(BroadcastReceiver receiver, Activity receiversActivity) {
        _applicationsBluetoothBroadcastReceiver = receiver;
        _activityReceiverBoundTo = receiversActivity;
    }

    public static void UnregisterReceiver() {
        if (_activityReceiverBoundTo != null)
	        _activityReceiverBoundTo.unregisterReceiver(_applicationsBluetoothBroadcastReceiver);
    }

    private static List<BluetoothSocket> _slavesSocketsList = new ArrayList<BluetoothSocket>();
    private static BluetoothSocket _masterSocket;
    private static final String TAG = "NetworkHelper";
    private static BroadcastReceiver _applicationsBluetoothBroadcastReceiver;
    private static Activity _activityReceiverBoundTo;
}