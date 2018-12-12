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

    public byte[] addSessionNumber(byte[] msg, long sessionnr) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8  + msg.length);
        byteBuffer.put(msg);

        byteBuffer.putLong(sessionnr);

        return byteBuffer.array();
    }

    //Returns null if invalid
    public byte[] validateTimestamp(byte[] msgtimestamp) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(msgtimestamp);
        byte[] msg = new byte[msgtimestamp.length-8];

        byteBuffer.get(msg);

        long timestamp = byteBuffer.getLong();
        long currentDate = new Date().getTime();

        //20000 interval is to much.
        //It is like that because our computer clocks and smartphone clocks were not in sync
        //So we need it to be a big interval so we can test it and demonstrate it
        //With synchronized systems the interval would need to be small to guarantee freshness
        if( !(timestamp > currentDate-10000 && timestamp < currentDate+10000)){
            System.out.println("timestamp = " + timestamp + " -- currentDate = " + currentDate);
            return null;
        }
        return msg;
    }

    public byte[] validateSessionNumber(byte[] msgSessionNumber, long sessionnr) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(msgSessionNumber);
        byte[] msg = new byte[msgSessionNumber.length-8];

        byteBuffer.get(msg);

        long sessionNumber = byteBuffer.getLong();

        if( sessionNumber != sessionnr ){
            System.out.println("receivedSessionNumber = " + sessionNumber + " -- supposedSessionNumber = " + sessionnr);
            return null;
        }
        return msg;
    }

    public byte[] addTimestampAndSessionNumber(byte[] msg, long sessionnr) {
        return addSessionNumber(addTimestamp(msg),sessionnr);
    }

    public byte[] validateTimestampAndSessionNumber(byte[] msgtimesession, long sessionnr) {
        byte[] msgtime;
        byte[] msg;

        if((msgtime = validateSessionNumber(msgtimesession, sessionnr)) != null){
            if((msg = validateTimestamp(msgtime)) != null){
                return msg;
            }
        }
        return null;
    }
}
