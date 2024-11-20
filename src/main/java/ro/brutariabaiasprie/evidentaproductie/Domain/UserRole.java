package ro.brutariabaiasprie.evidentaproductie.Domain;

public class UserRole {
    private final String name;
    private boolean viewProductionTab = false;
    private boolean editRecords = false;
    private boolean viewAdminTab = false;
    private boolean viewOrders = false;
    private boolean editOrders = false;
    private boolean viewProducts = false;
    private boolean editProducts = false;
    private boolean viewGroups = false;
    private boolean editGroups = false;
    private boolean viewUsers = false;
    private boolean editUsers = false;


    public UserRole(int ID_ROLE) {
        switch (ID_ROLE) {
            case 1:
                name = "Director";
                viewProductionTab = true;
                editRecords = true;
                viewAdminTab = true;
                viewOrders = true;
                editOrders = true;
                viewProducts = true;
                editProducts = true;
                viewGroups = true;
                editGroups = true;
                viewUsers = true;
                editUsers = true;
                break;
            case 2:
                name = "Tehnic";
                viewProductionTab = true;
                editRecords = true;
                viewAdminTab = true;
                viewOrders = true;
                editOrders = true;
                viewGroups = true;
                editGroups = true;
                break;
            case 3:
                name = "Operator";
                viewProductionTab = true;
                viewAdminTab = true;
                viewOrders = true;
                break;
            default:
                name = "Neautorizat";
                break;
        }
    }

    public String getName() {
        return name;
    }

    public boolean canViewProductionTab() {
        return viewProductionTab;
    }

    public boolean canEditRecords() {
        return editRecords;
    }

    public boolean canViewAdminTab() {
        return viewAdminTab;
    }

    public boolean canViewOrders() {
        return viewOrders;
    }

    public boolean canEditOrders() {
        return editOrders;
    }

    public boolean canViewGroups() {
        return viewGroups;
    }

    public boolean canEditGroups() {
        return editGroups;
    }

    public boolean canViewUsers() {
        return viewUsers;
    }

    public boolean canEditUsers() {
        return editUsers;
    }

    public boolean canViewProducts() {
        return viewProducts;
    }

    public boolean canEditProducts() {
        return editProducts;
    }
}
