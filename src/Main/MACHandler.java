package Main;

public class MACHandler {

    public byte[] getMAC(byte[] array){
        //TODO calculate and return MAC
        return array; //TODO
    }

    //Returns null if invalid and msg without the mac in the end
    public byte[] validateMAC(byte[] array) {
        //TODO
        //getMAC(com parte inicial da msg que nao contem o mac)
        /*if(maccalculado != getMAC) {
            return null;
        }*/
        //return parte inicial da msg sem o mac
        return array; //TODO
    }
}
