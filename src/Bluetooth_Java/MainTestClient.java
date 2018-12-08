package Bluetooth_Java;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.bluetooth.UUID;

/**
 * Minimal Device Discovery example.
 */
public class MainTestClient {

    private static final String UUID_STRING = "1a86d88683824103a0d298e61ce4d50c";
    private String connectionURL = "btspp://localhost:" + UUID_STRING + ";name=RemoteBluetooth";

    private LocalDevice localDevice;
    private DiscoveryAgent discoveryAgent;
    private StreamConnection connection;
    private String url;

    public MainTestClient() {

        try {
            localDevice = LocalDevice.getLocalDevice();
            discoveryAgent = localDevice.getDiscoveryAgent();

            UUID[] uuidSet = new UUID[1];
            uuidSet[0] = new UUID(UUID_STRING, false);
            // search the paired devices list for the andrdoid smartphone used for testing the system
            RemoteDevice pairedDevice = discoveryAgent.retrieveDevices(DiscoveryAgent.PREKNOWN)[0];
            discoveryAgent.searchServices(null, uuidSet, pairedDevice, new MyDiscoveryListener());
            connection = (StreamConnection)Connector.open(connectionURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() {
        // waiting for connection
        while(true) {
            try {
                manageConnection(connection);
            } catch (Exception e) {
                //try again
                continue;
                //TODO deveria ser assim
            }
        }
    }

    private void manageConnection(StreamConnection connection){
        try {
            InputStream is = connection.openInputStream();
            OutputStream os = connection.openOutputStream();

            //TODO for testing
            byte data[] = "Hello Smartphone, from computer, passa para ca a chave de encriptacao !!!".getBytes();
            os.write(data);
            os.close();
            byte[] buffer = new byte[1024];
            is.read(buffer);
            System.out.println(new String(buffer));
            //

            securityManagment(os);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void securityManagment(OutputStream os) {
        //TODO
    }


    public static void main(String[] args) {
        MainTestClient obj = new MainTestClient();
        obj.run();
    }
    class MyDiscoveryListener implements DiscoveryListener {
        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {

        }

        public void inquiryCompleted(int discType) {

        }

        public void serviceSearchCompleted(int transID, int respCode) {
            System.out.println("Service Search completed!");
        }

        public void servicesDiscovered(int arg0, ServiceRecord[] services) {
            for (int i = 0; i < services.length; i++) {
                url = services[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                if (url == null) {
                    continue;
                }
                System.out.println("service found " + url);
            }
            //sendMessageToDevice(url);
        }
    }
}



