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
                name = "Administrator aplicatie";
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
                description = "Are permisiuni nelimitate pentru administrarea operarii aplicatiei si a functionarii acesteia. " +
                        "Poate interveni cand apar erori in aplicatie, ce tin de functionarea corecta a programului.";
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
                description = "Are permisiuni nelimitate pentru administrarea operarii aplicatiei. " +
                        "Poate interveni cand apar erori de operare a programului.";
                break;
            case MANAGER:
                name = "Tehnic";
                viewProductionTab = true;
                editRecords = true;
                viewAdminTab = true;
                viewOrders = true;
                editOrders = true;
                description = "Are permisiuni limitate pentru administrarea operarii aplicatiei." +
                        "Poate interveni cand apar unele erori de operare a programului";
                break;
            case OPERATOR:
                name = "Operator";
                viewProductionTab = true;
                viewAdminTab = true;
                viewOrders = true;
                description = "Are permisiuni limitate pentru operarea aplicatiei. " +
                        "Poate vizualiza unele comenzi si realizari si poate introduce realizari";
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

    @Override
    public String toString() {
        return name;
    }
}
