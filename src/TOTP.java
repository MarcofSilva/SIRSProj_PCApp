public class TOTP extends HOTP{

    private int _timeRangeOfPassword; //One time password update every TIME_RANGE_PASSWORD seconds

    public TOTP(String secretKey, int numberDigitsOTP, int timeRangeOfPassword) {
        super(secretKey, numberDigitsOTP);
        _timeRangeOfPassword = timeRangeOfPassword;
    }

    public String generateOTP() {
        long unixTime = System.currentTimeMillis()/1000L;
        long counter = unixTime / _timeRangeOfPassword;
        return super.generateOTP(counter);
    }
}
