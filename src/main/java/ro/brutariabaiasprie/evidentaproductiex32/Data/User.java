package ro.brutariabaiasprie.evidentaproductiex32.Data;

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

//    public String stringify() {
//        return this.ID + ";" + this.ID_ROLE + ";" + this.username + ";" + this.password + ";";
//    }
//
//    public void parse(String string) {
//        Pattern pattern = Pattern.compile("(.+?);");
//        Matcher matcher = pattern.matcher(string);
//        matcher.matches();
//        matcher.groupCount();
//        this.ID = Integer.parseInt(matcher.group(0));
//        this.ID_ROLE = Integer.parseInt(matcher.group(1));
//        this.username = matcher.group(2);
//        this.password = matcher.group(3);
//    }
}
