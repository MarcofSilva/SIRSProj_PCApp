package Main;

import java.util.ArrayList;

public class User {
    private String _username;
    private String _password; //this password is the hash of the real one
    private boolean _isLoggedIn = false;
    private long sessionNumber = 0;

    private ArrayList<String> _files = new ArrayList<String>();

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

    public String get_username(){return _username;}

    public ArrayList<String> get_files() {
        return _files;
    }

    public void add_file(String file) {
        _files.add(file);
    }

    public void remove_file(String file) {
        _files.remove(file);
    }

    public long getSessionNumber() { return sessionNumber; }

    public void incSessionNumber() { this.sessionNumber++; }
}
