package Bluetooth_Java;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Minimal Device Discovery example.
 */
public class MainTestServer {

    private static final String UUID_STRING = "1a86d88683824103a0d298e61ce4d50c";

    private LocalDevice local;
    private StreamConnectionNotifier notifier;
    private StreamConnection connection;

    public MainTestServer() {

        // setup the server to listen for connection
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);

            String url = "btspp://localhost:" + UUID_STRING + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier)Connector.open(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() {
        // waiting for connection
        while(true) {
            try {
                System.out.println("waiting for connection...");
                connection = notifier.acceptAndOpen();
            } catch (Exception e) {
                //try again
                continue;
                //TODO deveria ser assim
            }
            System.out.println("OHHHH YESSS");
            manageConnection(connection);
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
        MainTestServer obj = new MainTestServer();
        obj.run();
    }
}