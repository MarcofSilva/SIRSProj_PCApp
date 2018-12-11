package Bluetooth_Java;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.bluetooth.UUID;
import Main.Manager;

public class MainTestClient{

    private static final String UUID_STRING = "1a86d88683824103a0d298e61ce4d50c";
    private UUID[] uuidSet;

    private Object lock = new Object();

    private LocalDevice localDevice;
    private DiscoveryAgent discoveryAgent;
    private StreamConnection connection;
    private String connectionURL;

    public MainTestClient() {

        try {
            localDevice = LocalDevice.getLocalDevice();
            discoveryAgent = localDevice.getDiscoveryAgent();

            uuidSet = new UUID[1];
            uuidSet[0] = new UUID(UUID_STRING, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // search the paired devices list for the android smartphone used for testing the system
        RemoteDevice pairedDevice = discoveryAgent.retrieveDevices(DiscoveryAgent.PREKNOWN)[0];
        try {
            discoveryAgent.searchServices(null, uuidSet, pairedDevice, new MyDiscoveryListener());

            //Wait for the services search to be over
            synchronized (lock) {
                lock.wait();
            }
            //After founding the service of the android app get the conector and start the communication management
            connection = (StreamConnection)Connector.open(connectionURL);
            manageConnection(connection);
        } catch (Exception e) {
            //TODO
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

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Manager.getInstance().storePublicKey(keyPair.getPublic().getEncoded());
            System.out.println("message: " + new String(buffer));
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
        System.out.println("yee");
        MainTestClient obj = new MainTestClient();

        obj.run();
    }
    class MyDiscoveryListener implements DiscoveryListener {
        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            //Not relevant
        }

        public void inquiryCompleted(int discType) {
            //Not relevant
        }

        public void serviceSearchCompleted(int transID, int respCode) {
            System.out.println("Service Search completed!");
        }

        public void servicesDiscovered(int arg0, ServiceRecord[] services) {
            //We are expecting that only one service exist in the paired device
            for (int i = 0; i < services.length; i++) {
                connectionURL = services[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                if (connectionURL == null) {
                    continue;
                }
                System.out.println("service found " + connectionURL);
            }
            //Awakens the thread waiting for the service search to be over
            synchronized(lock) {
                lock.notifyAll();
            }
        }
    }
}



