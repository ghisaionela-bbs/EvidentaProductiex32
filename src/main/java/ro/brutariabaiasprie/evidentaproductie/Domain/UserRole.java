package ro.brutariabaiasprie.evidentaproductie.Domain;

import ro.brutariabaiasprie.evidentaproductie.Data.ACCESS_LEVEL;

public class UserRole {
    private final String name;
    private ACCESS_LEVEL accessLevel = ACCESS_LEVEL.UNAUTHORIZED;
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
    private String description = "";


    public UserRole(ACCESS_LEVEL ID_ROLE) {
        accessLevel = ID_ROLE;
        switch (ID_ROLE) {
            case ADMINISTRATOR:
                name = "Administrator";
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
            case DIRECTOR:
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
            case MANAGER:
                name = "Tehnic";
                viewProductionTab = true;
                editRecords = true;
                viewAdminTab = true;
                viewOrders = true;
                editOrders = true;
                viewGroups = true;
                editGroups = true;
                break;
            case OPERATOR:
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

    public ACCESS_LEVEL getAccessLevel() {
        return accessLevel;
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

    public String getDescription() {
        return description;
    }
}
