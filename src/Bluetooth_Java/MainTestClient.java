package Bluetooth_Java;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.bluetooth.UUID;
import Main.Manager;
import Main.KeyManager;

import static java.util.Arrays.copyOfRange;

public class MainTestClient{

    private static final String UUID_STRING = "1a86d88683824103a0d298e61ce4d50c";
    private UUID[] uuidSet;

    private Object lock = new Object();

    private LocalDevice localDevice;
    private DiscoveryAgent discoveryAgent;
    private StreamConnection connection;
    private String connectionURL;

    private KeyManager keyManager = KeyManager.getInstance();

    public MainTestClient() {
        while(!LocalDevice.isPowerOn()) {
            System.out.println("Turn on bluetooth");
            try {
                Thread.sleep(10000); // sleep for some time and then check if the bluetooth has already been turned on
            } catch (InterruptedException e) {
                e.printStackTrace();
                //TODO
            }
        }
        try {
            localDevice = LocalDevice.getLocalDevice();
            discoveryAgent = localDevice.getDiscoveryAgent();

            uuidSet = new UUID[1];
            uuidSet[0] = new UUID(UUID_STRING, false);
        } catch (BluetoothStateException e) {
            System.out.println("BluetoothStack not detected."); //TODO
        }
    }

    public void run(String username) {
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
            manageConnection(connection, username);
        } catch (Exception e) {
            //TODO
        }
    }

    private void manageConnection(StreamConnection connection, String username){
        try {
            InputStream is = connection.openInputStream();
            OutputStream os = connection.openOutputStream();

            //TODO for testing
            /*byte[] intbytes = new byte[8];
            ByteBuffer.wrap(intbytes).putLong(Manager.getInstance().getUser(username).getSessionNumber());*/ //TODO
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            byteBuffer.putLong(Manager.getInstance().getUser(username).getSessionNumber());
            byte[] request = byteBuffer.array();
            System.out.println(new String(request));
            os.write(request);
            os.close();


            byte[] buffer = new byte[2048];
            ByteBuffer byteBufferread = ByteBuffer.allocate(2048);
            int bytesRead;
            int totalbytes = 0;
            while((bytesRead = is.read(buffer)) >= 0) {
                totalbytes += bytesRead;
                byteBufferread.put(buffer);
            }

            byte[] result = new byte[totalbytes];
            result = byteBufferread.array();

            System.out.println("message: " + keyManager.byteArrayToHexString(result));

            ByteBuffer bBuffer = ByteBuffer.wrap(result);
            int privKeySize = bBuffer.getInt();
            int pubKeySize = bBuffer.getInt();
            byte[] privateKey = new byte[privKeySize];
            byte[] publicKey = new byte[pubKeySize];
            bBuffer.get(privateKey);
            bBuffer.get(publicKey);

            System.out.println("privateKey: " + keyManager.byteArrayToHexString(privateKey) + "publicKey: " + keyManager.byteArrayToHexString(publicKey));

            /*byte[] privateKey = copyOfRange(buffer, 8, privKeySize + 8); //from: inclusive, to: exclusive
            byte[] publicKey = copyOfRange(buffer, 8 + privKeySize, 8 + privKeySize + pubKeySize);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();*/
            Manager.getInstance().storePublicKey(publicKey);
            if(privKeySize != 0)
                Manager.getInstance().storePrivateKey(privateKey);

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

    public void closeConnection(){
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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



