package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindow.Manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.CheckComboBox;
import ro.brutariabaiasprie.evidentaproductie.Data.ACCESS_LEVEL;
import ro.brutariabaiasprie.evidentaproductie.Domain.Order;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Domain.Record;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;
import java.util.ArrayList;
import java.util.WeakHashMap;

public class ManagerModel {
    private final User CONNECTED_USER;
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<Group> groups = FXCollections.observableArrayList();
    private final ObservableList<Order> orders = FXCollections.observableArrayList();
    private final ObservableList<Record> records = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<Group> groups2 = FXCollections.observableArrayList();
    private int orderStatusFilter = -1;
//    private Group orderGroupFilter = null;
//    private Group orderSubgroupFilter = null;
    private final ObservableList<Group> groupFilterList = FXCollections.observableArrayList();
    private ArrayList<Group> orderGroupFilter = new ArrayList<>();
    private ArrayList<Group> orderSubgroupFilter = new ArrayList<>();
    private final ObservableList<Group> subgroupFilterList = FXCollections.observableArrayList();

    private final ObservableList<Group> productGroupFilterList = FXCollections.observableArrayList();
    private ArrayList<Group> productGroupFilter = new ArrayList<>();
    private final ObservableList<Group> productSubgroupFilterList = FXCollections.observableArrayList();
    private ArrayList<Group> productSubgroupFilter = new ArrayList<>();

    public ManagerModel() {
        CONNECTED_USER = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
    }

    public ObservableList<Order> getOrders() {
        return orders;
    }

    public ObservableList<Group> getGroups() {
        return groups;
    }

    public ObservableList<Group> getGroups2() {
        return groups2;
    }

    public User getCONNECTED_USER() {
        return CONNECTED_USER;
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    public ObservableList<Record> getRecords() {
        return records;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public ObservableList<Group> getGroupFilterList() { return groupFilterList; }

    public ObservableList<Group> getSubgroupFilterList() {
        return subgroupFilterList;
    }

    public void setOrderStatusFilter(int orderStatusFilter) {
        this.orderStatusFilter = orderStatusFilter;
    }

    public void setOrderGroupFilter(ArrayList<Group> orderGroupFilter) {
        this.orderGroupFilter = orderGroupFilter;
    }

    public void setOrderSubgroupFilter(ArrayList<Group> orderSubgroupFilter) {
        this.orderSubgroupFilter = orderSubgroupFilter;
    }

    public void setProductGroupFilter(ArrayList<Group> productGroupFilter) {
        this.productGroupFilter = productGroupFilter;
    }

    public void setProductSubgroupFilter(ArrayList<Group> productSubgroupFilter) {
        this.productSubgroupFilter = productSubgroupFilter;
    }

    public ObservableList<Group> getProductGroupFilterList() {
        return productGroupFilterList;
    }

    public ObservableList<Group> getProductSubgroupFilterList() {
        return productSubgroupFilterList;
    }

    public void loadProducts() {
        try {

            String whereCond = "WHERE 1=1 ";
            whereCond += " AND ( 1=0 ";
            for (int i = 0; i < productGroupFilter.size(); i++) {
                if(productGroupFilter.get(i) != null) {
                    whereCond += " OR gp.ID = ? ";
                }
            }
            whereCond += " ) ";

            whereCond += " AND ( 1=0 ";
            for (int i = 0; i < productSubgroupFilter.size(); i++) {
                if(productSubgroupFilter.get(i) != null) {
                    whereCond += " OR subg.ID = ? ";
                }
            }
            whereCond += " ) ";

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT " +
                    "p.ID, " +
                    "p.denumire, " +
                    "p.sarja, " +
                    "p.um, " +
                    "p.ID_GRUPA, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "gp.denumire AS denumire_grupa, " +
                    "subg.denumire AS denumire_subgrupa, " +
                    "subg.ID_GRUPA_PARINTE AS subg_id_parinte " +
                    "FROM PRODUSE p " +
                    "LEFT JOIN GRUPE_PRODUSE gp ON p.ID_GRUPA = gp.ID " +
                    "LEFT JOIN GRUPE_PRODUSE subg ON p.ID_SUBGRUPA_PRODUSE = subg.ID " +
                    whereCond +
                    "ORDER BY p.denumire ASC, p.um ";
            PreparedStatement statement = connection.prepareStatement(sql);
            int paramCount = 1;
            for (int i = 0; i < productGroupFilter.size(); i++) {
                if(productGroupFilter.get(i) != null) {
                    statement.setInt(paramCount, productGroupFilter.get(i).getId());
                    paramCount += 1;
                }
            }
            for (int i = 0; i < productSubgroupFilter.size(); i++) {
                if(productSubgroupFilter.get(i) != null) {
                    statement.setInt(paramCount, productSubgroupFilter.get(i).getId());
                    paramCount += 1;
                }
            }

            ResultSet resultSet = statement.executeQuery();
            products.clear();
            while(resultSet.next()) {
                Group group = null;
                int groupId = resultSet.getInt("ID_GRUPA");
                if(!resultSet.wasNull()) {
                    group = new Group(groupId,
                            resultSet.getString("denumire_grupa"));
                }
                int subgroupId = resultSet.getInt("subg_id_parinte");
                Group subgroup = new Group();
                if(!resultSet.wasNull()) {
                    subgroup = new Group(
                            subgroupId,
                            resultSet.getString("denumire_subgrupa"),
                            resultSet.getInt("subg_id_parinte"));
                }
                Product product = new Product(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire"),
                        resultSet.getDouble("sarja"),
                        resultSet.getString("um"),
                        group,
                        subgroup,
                        resultSet.getInt("ID_SUBGRUPA_PRODUSE")
                );
//                product.setGroup(group);
                products.add(product);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadProductGroupFilterList() {
        try {
            productGroupFilterList.clear();
            productGroupFilterList.add(null);
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE_PRODUSE WHERE ID_GRUPA_PARINTE IS NULL";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                productGroupFilterList.add(new Group(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire")
                ));
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }

    public void loadProductSubgroupFilterList() {
        productSubgroupFilterList.clear();
        productSubgroupFilterList.add(null);
        if (productGroupFilter.isEmpty()) {
            return;
        }

        String whereCond = " 1=0 ";
        for (int i = 0; i < productGroupFilter.size(); i++) {
            if(productGroupFilter.get(i) != null) {
                whereCond += " OR ID_GRUPA_PARINTE = ? ";
            }
        }

        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE_PRODUSE WHERE " + whereCond;
            PreparedStatement statement = connection.prepareStatement(sql);
            int paramCount = 1;
            for (int i = 0; i < productGroupFilter.size(); i++) {
                if (productGroupFilter.get(i) != null) {
                    statement.setInt(paramCount, productGroupFilter.get(i).getId());
                    paramCount += 1;
                }
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                productSubgroupFilterList.add(new Group(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire"),
                        resultSet.getInt("ID_GRUPA_PARINTE")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

//    public void loadRecords() {
//        try {
//            Connection connection = DBConnectionService.getConnection();
//
//            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
//            String whereCond = "";
//            if(user.getRoleId() != 1 && user.getRoleId() != 2) {
//                whereCond = " AND r.ID_UTILIZATOR_I = ? ";
//            }
//
//            String sql = " SELECT TOP 100 " +
//                    "r.ID, " +
//                    "r.ID_PRODUS, " +
//                    "p.denumire, " +
//                    "p.um, " +
//                    "p.ID_GRUPA, " +
//                    "gp.denumire AS denumire_grupa, " +
//                    "r.cantitate, " +
//                    "r.datasiora_i, " +
//                    "r.ID_UTILIZATOR_I, " +
//                    "r.datasiora_m, " +
//                    "r.ID_UTILIZATOR_M " +
//                    "FROM REALIZARI r " +
//                    "LEFT JOIN PRODUSE p ON p.ID = r.ID_PRODUS " +
//                    "LEFT JOIN GRUPE_PRODUSE gp ON gp.ID = p.ID_GRUPA " +
//                    "WHERE (r.ID_COMANDA IS NULL OR r.ID_COMANDA = 0) " +
//                    whereCond +
//                    "ORDER BY r.datasiora_i DESC ";
//
//            PreparedStatement statement = connection.prepareStatement(sql);
//            if(user.getRoleId() != 1 && user.getRoleId() != 2) {
//                statement.setInt(1, user.getId());
//            }
//            ResultSet resultSet = statement.executeQuery();
//            records.clear();
//            while (resultSet.next()) {
//                Group group = null;
//                int groupId = resultSet.getInt("ID_GRUPA");
//                if(!resultSet.wasNull()) {
//                    group = new Group(groupId, resultSet.getString("denumire_grupa"));
//                }
//                Product product = new Product();
//                product.setId(resultSet.getInt("ID_PRODUS"));
//                product.setName(resultSet.getString("denumire"));
//                product.setUnitMeasurement(resultSet.getString("um"));
//                product.setGroup(group);
//
//                ro.brutariabaiasprie.evidentaproductie.Domain.Record record = new Record();
//                record.setId(resultSet.getInt("ID"));
//                record.setProduct(product);
//                record.setQuantity(resultSet.getDouble("cantitate"));
//                record.setDateTimeInserted(resultSet.getTimestamp("datasiora_i"));
//                record.setUserIdInserted(resultSet.getInt("ID_UTILIZATOR_I"));
//                record.setDateTimeModified(resultSet.getTimestamp("datasiora_m"));
//                record.setUserIdModified(resultSet.getInt("ID_UTILIZATOR_M"));
//                records.add(record);
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void loadOrders() {
        try {
            String whereCond = " WHERE 1=1 ";
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                case DIRECTOR:
                    break;
                case MANAGER:
                    whereCond += " AND gp.ID = ? ";
                    break;
                case OPERATOR:
                    whereCond += " AND gp.ID = ? AND subg.ID = ? AND c.inchisa = 0 ";
                    break;
                case UNAUTHORIZED:
                    whereCond += " AND 1=0 ";
            }

            switch (orderStatusFilter) {
                case -1:
                    break;
                case 0:
                    whereCond += " AND c.inchisa = 0 ";
                    break;
                case 1:
                    whereCond += " AND c.inchisa = 1 ";
                    break;
            }

            whereCond += " AND ( 1=0 ";
            for (int i = 0; i < orderGroupFilter.size(); i++) {
                if(orderGroupFilter.get(i) != null) {
                    whereCond += " OR gp.ID = ? ";
                }
            }
            whereCond += " ) ";

            whereCond += " AND ( 1=0 ";
            for (int i = 0; i < orderSubgroupFilter.size(); i++) {
                if(orderSubgroupFilter.get(i) != null) {
                    whereCond += " OR subg.ID = ? ";
                }
            }
            whereCond += " ) ";

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT c.ID, " +
                    "c.contor, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.sarja, " +
                    "p.um, " +
                    "gp.ID AS ID_GRUPA, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "gp.denumire AS denumire_grupa, " +
                    "c.data_programata, " +
                    "c.cantitate, " +
                    "SUM(COALESCE(r.cantitate, 0.00)) AS realizat, " +
                    "c.cantitate - SUM(COALESCE(r.cantitate, 0.00)) AS rest, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.inchisa " +
                    "FROM COMENZI c " +
                    "LEFT JOIN PRODUSE p ON p.ID = c.ID_PRODUS " +
                    "LEFT JOIN REALIZARI r ON r.ID_COMANDA = c.ID " +
                    "LEFT JOIN GRUPE_PRODUSE gp ON gp.ID = p.ID_GRUPA " +
                    "LEFT JOIN GRUPE_PRODUSE subg ON subg.ID = p.ID_SUBGRUPA_PRODUSE " +
                    whereCond +
                    "GROUP BY c.ID, " +
                    "c.contor, " +
                    "c.data_programata, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.sarja, " +
                    "p.um, " +
                    "gp.ID, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "gp.denumire, " +
                    "c.cantitate, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.inchisa " +
                    "ORDER BY c.data_programata ASC ";

            int paramCount = 1;
            PreparedStatement statement = connection.prepareStatement(sql);
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                case DIRECTOR:
                    break;
                case MANAGER:
                    statement.setInt(paramCount, ConfigApp.getUser().getGroupId());
                    paramCount += 1;
                    break;
                case OPERATOR:
                    statement.setInt(paramCount, ConfigApp.getUser().getGroupId());
                    paramCount += 1;
                    statement.setInt(paramCount, ConfigApp.getUser().getSubgroupId());
                    paramCount += 1;
                    break;
                case UNAUTHORIZED:
                    break;
            }

            for (int i = 0; i < orderGroupFilter.size(); i++) {
                if(orderGroupFilter.get(i) != null) {
                    statement.setInt(paramCount, orderGroupFilter.get(i).getId());
                    paramCount += 1;
                }
            }

            for (int i = 0; i < orderSubgroupFilter.size(); i++) {
                if(orderSubgroupFilter.get(i) != null) {
                    statement.setInt(paramCount, orderSubgroupFilter.get(i).getId());
                    paramCount += 1;
                }
            }

            ResultSet resultSet = statement.executeQuery();

            orders.clear();
            while (resultSet.next()) {
                Group group = null;
                int groupId = resultSet.getInt("ID_GRUPA");
                if(!resultSet.wasNull()) {
                    group = new Group(groupId, resultSet.getString("denumire_grupa"));
                }

                Order order = new Order();
                order.setId(resultSet.getInt("ID"));
                order.setCounter(resultSet.getInt("contor"));
                order.setProduct(new Product(
                        resultSet.getInt("ID_PRODUS"),
                        resultSet.getString("denumire"),
                        resultSet.getDouble("sarja"),
                        resultSet.getString("um"),
                        group,
                        resultSet.getInt("ID_SUBGRUPA_PRODUSE")
                ));
                order.setDateScheduled(resultSet.getTimestamp("data_programata"));
                order.setQuantity(resultSet.getDouble("cantitate"));
                order.setCompleted(resultSet.getDouble("realizat"));
                order.setRemainder(resultSet.getDouble("rest"));
                order.setDateTimeInserted(resultSet.getTimestamp("datasiora_i"));
                order.setUserIdInserted(resultSet.getInt("ID_UTILIZATOR_I"));
                order.setDateTimeModified(resultSet.getTimestamp("datasiora_m"));
                order.setUserIdModified(resultSet.getInt("ID_UTILIZATOR_M"));
                order.setClosed(resultSet.getBoolean("inchisa"));
                orders.add(order);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadGroupFilterList() {
        try {
            groupFilterList.clear();
            groupFilterList.add(null);

            String whereCond = " WHERE 1=1 ";
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR :
                    break;
                case MANAGER, OPERATOR:
                    whereCond += " AND ID = ? ";
                    break;
                case UNAUTHORIZED:
                    whereCond += " AND 1=0 ";
                    break;
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE_PRODUSE " + whereCond + " AND ID_GRUPA_PARINTE IS NULL";

            int paramCount = 1;
            PreparedStatement statement = connection.prepareStatement(sql);
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                    break;
                case MANAGER, OPERATOR:
                    statement.setInt(paramCount, ConfigApp.getUser().getGroupId());
                    paramCount += 1;
                    break;
                case UNAUTHORIZED:
                    break;
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                groupFilterList.add(new Group(
                    resultSet.getInt("ID"),
                    resultSet.getString("denumire")
                ));
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }



    public void loadSubgroupFilterList() {
        subgroupFilterList.clear();
        subgroupFilterList.add(null);
        if (orderGroupFilter.isEmpty()) {
            return;
        }

        String whereCond = " 1=1 ";

        switch (ConfigApp.getRole().getAccessLevel()) {
            case ADMINISTRATOR, MANAGER:
                break;
            case OPERATOR:
                whereCond += " AND ID = ? ";
                break;
            case UNAUTHORIZED:
                whereCond += " AND 1=0 ";
                break;
        }

        whereCond += " AND ( 1=0 ";
        for (int i = 0; i < orderGroupFilter.size(); i++) {
            if(orderGroupFilter.get(i) != null) {
                whereCond += " OR ID_GRUPA_PARINTE = ? ";
            }
        }
        whereCond += " ) ";

        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE_PRODUSE WHERE " + whereCond;
            PreparedStatement statement = connection.prepareStatement(sql);
            int paramCount = 1;

            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR, MANAGER:
                    break;
                case OPERATOR:
                    statement.setInt(paramCount, ConfigApp.getUser().getSubgroupId());
                    paramCount += 1;
                    break;
                case UNAUTHORIZED:
                    break;
            }

            for (int i = 0; i < orderGroupFilter.size(); i++) {
                if (orderGroupFilter.get(i) != null) {
                    statement.setInt(paramCount, orderGroupFilter.get(i).getId());
                    paramCount += 1;
                }
            }


            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                subgroupFilterList.add(new Group(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire"),
                        resultSet.getInt("ID_GRUPA_PARINTE")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public void loadGroups() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE_PRODUSE";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            groups.clear();
            while (resultSet.next()) {
                Group group = new Group(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire")
                );
                groups.add(group);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadGroups2() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT g.ID, g.denumire, g.ID_GRUPA_PARINTE, ( " +
                    " CASE " +
                    "    WHEN g.ID_GRUPA_PARINTE IS NULL THEN g.ID " +
                    "    ELSE g.ID_GRUPA_PARINTE " +
                    " END " +
                    ") AS grupa FROM GRUPE_PRODUSE g " +
                    "LEFT JOIN GRUPE_PRODUSE subg ON subg.ID_GRUPA_PARINTE = g.ID " +
                    "GROUP BY ( " +
                    " CASE " +
                    "    WHEN g.ID_GRUPA_PARINTE IS NULL THEN g.ID " +
                    "    ELSE g.ID_GRUPA_PARINTE " +
                    " END " +
                    "), g.ID, g.denumire, g.ID_GRUPA_PARINTE " +
                    "ORDER BY grupa ASC, g.ID_GRUPA_PARINTE ASC, g.denumire ASC ";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            groups2.clear();
            while (resultSet.next()) {
                int parentGroupId = resultSet.getInt("ID_GRUPA_PARINTE");
                Group group = null;
                if(resultSet.wasNull()) {
                    group = new Group(
                            resultSet.getInt("ID"),
                            resultSet.getString("denumire")
                    );
                } else {
                    group = new Group(
                            resultSet.getInt("ID"),
                            resultSet.getString("denumire"),
                            parentGroupId
                    );
                }
                groups2.add(group);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadUsers() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT " +
                    "ID, " +
                    "ID_ROL, " +
                    "nume_utilizator, " +
                    "parola, " +
                    "ID_GRUPA, " +
                    "ID_SUBGRUPA_PRODUSE " +
                    "FROM UTILIZATORI";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            users.clear();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("ID"));
                user.setRoleId(resultSet.getInt("ID_ROL"));
                user.setUsername(resultSet.getString("nume_utilizator"));
                user.setPassword(resultSet.getString("parola"));
                user.setGroupId(resultSet.getInt("ID_GRUPA"));
                user.setSubgroupId(resultSet.getInt("ID_SUBGRUPA_PRODUSE"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
