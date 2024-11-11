package ro.brutariabaiasprie.evidentaproductie.Data;

public class  User {
    private int ID;
    private int ID_ROLE;
    private String username;
    private String password;
    private Integer ID_GROUP;

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

    public Integer getID_GROUP() {
        return ID_GROUP;
    }

    public void setID_GROUP(Integer ID_GROUP) {
        this.ID_GROUP = ID_GROUP;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", ID_ROLE=" + ID_ROLE +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", ID_GROUP=" + ID_GROUP +
                '}';
    }
}
