package Main;

public class User {
    private String _username;
    private String _password; //TODO password shouldn't be stored this way
    private boolean _isLoggedIn = false;

    public User(String username, String password){
        _username = username;
        _password = password;
    }

    public boolean isLoggedIn() {
        return _isLoggedIn;
    }

    public void set_isLoggedIn(boolean isLoggedIn) {
        this._isLoggedIn = isLoggedIn;
    }

    public String get_password(){ return _password;}
}
