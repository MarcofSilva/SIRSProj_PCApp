import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;

public class BluetoothManager extends Thread {
    private static final String UUID_STRING = "1a86d886-8382-4103-a0d2-98e61ce4d50c";

    private LocalDevice blueDevice = null;
    private StreamConnectionNotifier notifier = null;
    private StreamConnection connection = null;

    public BluetoothManager() {
        try {
            blueDevice = LocalDevice.getLocalDevice();
            blueDevice.setDiscoverable(DiscoveryAgent.GIAC);

            String url = "btspp://localhost:" + UUID_STRING.toString()
                    + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier) Connector.open(url);

        } catch (BluetoothStateException e) {
            System.out
                    .println("ModuleBluetooth: Error getting the bluetooth device");
        } catch (IOException e) {
        }
        System.out.println("waiting for connection...");
        try {
            connection = notifier.acceptAndOpen();
            System.out.println("Conenction created");
        } catch (IOException e) {
            System.out.println("Can not create the connection");
        }
    }
}
