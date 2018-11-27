package Bluetooth_Java;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

/**
 * Minimal Device Discovery example.
 */
public class Main {

    public static final Vector/*<RemoteDevice>*/ devicesDiscovered = new Vector();
    static RemoteDevice device;
    public static void main(String[] args) throws IOException, InterruptedException {

        Object lock = new Object();
        devicesDiscovered.clear();
        class MyDiscoveryListener implements DiscoveryListener {
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
                devicesDiscovered.addElement(btDevice);
                try {
                    System.out.println("     name " + btDevice.getFriendlyName(false));
                    System.out.println(btDevice.getFriendlyName(false).equals("Galaxy A7 (2018)"));
                    if( btDevice.getFriendlyName(false).equals("Galaxy A7 (2018)")) device = btDevice;
                } catch (IOException cantGetDeviceName) {
                }
            }

            public void inquiryCompleted(int discType) {
                System.out.println("Device Inquiry completed!");
                synchronized (lock) {
                    lock.notifyAll();
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
                synchronized (lock) {
                    System.out.println("Service Search completed!");
                    lock.notify();
                }
            }

            public void servicesDiscovered(int arg0, ServiceRecord[] services) {
                for (int i = 0; i < services.length; i++) {
                    String url = services[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    if (url == null) {
                        continue;
                    }
                    System.out.println("1");
                    DataElement serviceName = services[i].getAttributeValue(0x0100);
                    System.out.println("2");
                    if (serviceName != null) {
                        System.out.println("service " + serviceName.getValue() + " found " + url);
                    } else {
                        System.out.println("service found " + url);
                    }
                    sendMessageToDevice(url);
                }
            }
        }


        try {
            // 1
            LocalDevice localDevice = LocalDevice.getLocalDevice();

            // 2
            DiscoveryAgent agent = localDevice.getDiscoveryAgent();

            // 3
            agent.startInquiry(DiscoveryAgent.GIAC, new MyDiscoveryListener());

            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UUID[] uuidSet = new UUID[1];
        uuidSet[0] = new UUID(0x1105); //OBEX Object Push service

        int[] attrIDs = new int[]{
                0x0100 // Service name
        };

        LocalDevice localDevice = LocalDevice.getLocalDevice();
        DiscoveryAgent agent = localDevice.getDiscoveryAgent();
        agent.searchServices(null, uuidSet, device, new MyDiscoveryListener());


        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }

        private static void sendMessageToDevice(String serverURL){
            try{
                System.out.println("Connecting to " + serverURL);

                ClientSession clientSession = (ClientSession) Connector.open(serverURL);
                HeaderSet hsConnectReply = clientSession.connect(null);
                if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
                    System.out.println("Failed to connect");
                    return;
                }

                HeaderSet hsOperation = clientSession.createHeaderSet();
                hsOperation.setHeader(HeaderSet.NAME, "Hello.txt");
                hsOperation.setHeader(HeaderSet.TYPE, "text");

                //Create PUT Operation
                Operation putOperation = clientSession.put(hsOperation);

                // Sending the message
                byte data[] = "Hello World !!!".getBytes("iso-8859-1");
                OutputStream os = putOperation.openOutputStream();
                os.write(data);
                os.close();

                putOperation.close();
                clientSession.disconnect(null);
                clientSession.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
}