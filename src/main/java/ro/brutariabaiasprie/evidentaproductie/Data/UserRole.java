package ro.brutariabaiasprie.evidentaproductie.Data;


import java.util.Map;

public class UserRole{
    private int IDROL;
    private Map<Permissions, Boolean> permissions;

    public int getIDROL() {
        return IDROL;
    }

    public void setIDROL(int IDROL) {
        this.IDROL = IDROL;
    }

    public Map<Permissions, Boolean> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<Permissions, Boolean> permissions) {
        this.permissions = permissions;
    }

    public void putPermission(Permissions permission, Boolean is_permited) {
        this.permissions.put(permission, is_permited);
    }
}
