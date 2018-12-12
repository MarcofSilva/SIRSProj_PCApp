package Main;


import java.util.Date;
import java.nio.ByteBuffer;

public class AuthorizationHandler {

    public byte[] addTimestamp(byte[] msg){
        ByteBuffer byteBuffer = ByteBuffer.allocate(8  + msg.length);
        byteBuffer.put(msg);

        byteBuffer.putLong(new Date().getTime());

        return byteBuffer.array();
    }

    //Returns null if invalid
    public byte[] validateTimestamp(byte[] msgtimestamp) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(msgtimestamp);

        byte[] msg = new byte[msgtimestamp.length-8];

        byteBuffer.get(msg);

        long timestamp = byteBuffer.getLong();
        long currentDate = new Date().getTime();

        //intervalo de 1seg pa tras 1 seg pa frente
        if( ! (timestamp > currentDate-1000 && timestamp < currentDate-1000)){
            return null;
        }
        return msg;
    }

    public byte[] addSessionNumber(byte[] msg) {
        return msg;
    }

    public byte[] validateSessionNumber(byte[] msgSessionNumber) {
        return msgSessionNumber;
    }

    public byte[] addTimestampAndSessionNumber(byte[] msg) {
        return addSessionNumber(addTimestamp(msg));
    }

    public byte[] validateTimestampAndSessionNumber(byte[] msg) {
        byte[] validTimestamp;
        if((validTimestamp = addTimestamp(msg)) != null)
            return addSessionNumber(addTimestamp(msg));
        return null;
    }
}
