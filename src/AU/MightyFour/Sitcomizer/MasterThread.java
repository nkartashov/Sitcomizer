package AU.MightyFour.Sitcomizer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: maratx
 * Date: 11/13/13
 * Time: 12:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class MasterThread extends Thread {
    public MasterThread(BluetoothDevice slaveDevice) {
        try {
            _incomingConnectionsSocket = slaveDevice.createRfcommSocketToServiceRecord(BluetoothCommands.APPLICATION_UID);
            Log.d(TAG, "RfcommSocket created");
        }
        catch (IOException e) {
            Log.d(TAG, "exception: " + e.getMessage());
        }
    }

    public void run() {
        try {
            Log.d(TAG, "Connecting slave...");
            _incomingConnectionsSocket.connect();
            Log.d(TAG, "Slave connected");
        }
        catch (IOException connectException) {
            Log.d(TAG, "exception: " + connectException.getMessage());
            try {
                _incomingConnectionsSocket.close();
            }
            catch (IOException closeException) {
                Log.d(TAG, "exception: " + closeException.getMessage());
            }
            return;
        }

        byte[] buffer = new byte[1];
        buffer[0] = BluetoothCommands.PREPARE_FOR_INTERACTION;
        try {
            OutputStream outStream = _incomingConnectionsSocket.getOutputStream();
            outStream.write(buffer);
            Log.d(TAG, "PREPARE_FOR_INTERACTION sent");
            InputStream inputStream = _incomingConnectionsSocket.getInputStream();
            Log.d(TAG, "Waiting for READY_FOR_INTERACTION...");
            try {
                inputStream.read(buffer);
                if(buffer[0] == BluetoothCommands.READY_FOR_INTERACTION) {
                    Log.d(TAG, "READY_FOR_INTERACTION received!");
                    // Code for adding device to group
                }
                else {
                    Log.d(TAG, "Incorrect response received!");
                }
            }
            catch (Exception e) {
                Log.d(TAG, "exception: " + e.getMessage());
            }
        }
        catch (Exception e) {
            Log.d(TAG, "exception: " + e.getMessage());
        }
    }

    private BluetoothSocket _incomingConnectionsSocket;
    private final String TAG = "MasterThread";
}
