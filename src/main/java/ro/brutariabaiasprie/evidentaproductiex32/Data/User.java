package ro.brutariabaiasprie.evidentaproductiex32.Data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class  User {
    private int ID;
    private int ID_ROLE;
    private String username;
    private String password;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID_ROLE() {
        return ID_ROLE;
    }

    public void setID_ROLE(int ID_ROLE) {
        this.ID_ROLE = ID_ROLE;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", ID_ROLE=" + ID_ROLE +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
