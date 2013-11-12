package AU.MightyFour.Sitcomizer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: maratx
 * Date: 11/13/13
 * Time: 1:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SlaveThread extends Thread {
    public SlaveThread(BluetoothAdapter slavesBluetoothAdapter) {

        try {
            _outgoingConnectionSocket = slavesBluetoothAdapter.listenUsingRfcommWithServiceRecord(serviceName,
                    BluetoothCommands.APPLICATION_UID);
        }
        catch (IOException e) {
            Log.d(TAG, "exception: " + e.getMessage());
        }
    }
    public void run() {
        BluetoothSocket masterSocket = null;
        try {
            Log.d(TAG, "Accepting master...");
            masterSocket = _outgoingConnectionSocket.accept();
            Log.d(TAG, "Master accepted");
        }
        catch (IOException e) {
            Log.d(TAG, "exception: " + e.getMessage());
        }
        try {
            InputStream inputStream = masterSocket.getInputStream();
            byte[] buffer = new byte[1];
            inputStream.read(buffer);
            if(buffer[0] == BluetoothCommands.PREPARE_FOR_INTERACTION) {
                OutputStream outputStream = masterSocket.getOutputStream();
                Log.d(TAG, "Received PREPARE_FOR_INTERACTION");
                buffer[0] = BluetoothCommands.READY_FOR_INTERACTION;
                outputStream.write(buffer);
                Log.d(TAG, "Sent READY_FOR_INTERACTION");
            }
            inputStream.read(buffer);
            // forward command
        }
        catch (Exception e) {
            Log.d(TAG, "exception: " + e.getMessage());
        }
    }
    
    private BluetoothServerSocket _outgoingConnectionSocket;
    private final String TAG = "SlaveThread";
    private final String serviceName = "SitcomizerSlaveNetwork";
}
