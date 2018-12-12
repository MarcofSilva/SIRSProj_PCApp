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

public class BluetoothManager {

    private static final String UUID_STRING = "1a86d88683824103a0d298e61ce4d50c";
    private UUID[] uuidSet;

    private Object lock = new Object();

    private LocalDevice localDevice;
    private DiscoveryAgent discoveryAgent;
    private StreamConnection connection;
    private String connectionURL;
    private int numServices = 0;

    private KeyManager keyManager = KeyManager.getInstance();

    public BluetoothManager() {
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

    public byte[] run(String username) {
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
            return manageConnection(connection, username);
        } catch (Exception e) {
            //TODO
        }
        return null;
    }

    private byte[] manageConnection(StreamConnection connection, String username){
        try {
            InputStream is = connection.openInputStream();
            OutputStream os = connection.openOutputStream();
            long sessionNum = Manager.getInstance().getUser(username).getSessionNumber();

            byte[] request = keyManager.prepareMessageToSend(sessionNum);
            System.out.println("pedido feito : " + keyManager.byteArrayToHexString(request));
            os.write(request);
            os.close();


            byte[] buffer = new byte[2048];

            ByteBuffer byteBufferRead = ByteBuffer.allocate(8192);
            int bytesRead;
            int totalBytes = 0;
            while ((bytesRead = is.read(buffer)) >= 0) {
                totalBytes += bytesRead;
                byteBufferRead.put(buffer, 0, bytesRead);
            }
            System.out.println("asdfasdfasdf" + totalBytes);
            byte[] result = byteBufferRead.array();

            byte[] received = new byte[totalBytes];
            ByteBuffer byteBuffer = ByteBuffer.wrap(result);
            byteBuffer.get(received, 0, totalBytes);
            System.out.println("message received: " + keyManager.byteArrayToHexString(received));

            byte[] msg;
            if ((msg = keyManager.validateMessageReceived(received, sessionNum)) != null) {
                System.out.println("message received altered: " + keyManager.byteArrayToHexString(msg));
                ByteBuffer bBuffer = ByteBuffer.wrap(msg);
                int privKeySize = bBuffer.getInt();
                int pubKeySize = bBuffer.getInt();
                byte[] privateKey = new byte[privKeySize];
                byte[] publicKey = new byte[pubKeySize];
                bBuffer.get(privateKey);
                bBuffer.get(publicKey);

                System.out.println("privateKey: " + keyManager.byteArrayToHexString(privateKey) + "publicKey: " + keyManager.byteArrayToHexString(publicKey));

                Manager.getInstance().storePublicKey(publicKey);
                return privateKey;
            }
            else {
                System.out.println("message received altered: " + keyManager.byteArrayToHexString(msg));
                System.out.println("Message received not valid!!");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            if(numServices == 0) {
                RemoteDevice pairedDevice = discoveryAgent.retrieveDevices(DiscoveryAgent.PREKNOWN)[0];
                try {
                    discoveryAgent.searchServices(null, uuidSet, pairedDevice, new MyDiscoveryListener());
                } catch (BluetoothStateException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Service Search completed!");
        }

        public void servicesDiscovered(int arg0, ServiceRecord[] services) {
            //We are expecting that only one service exist in the paired device
            for (int i = 0; i < services.length; i++) {
                numServices++;
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



