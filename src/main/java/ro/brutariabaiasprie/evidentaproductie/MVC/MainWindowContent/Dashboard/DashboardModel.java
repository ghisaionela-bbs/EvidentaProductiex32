package ro.brutariabaiasprie.evidentaproductie.MVC.MainWindowContent.Dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DatabaseObjects.OProductGroup;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardModel {
    private final ObservableList<OProductGroup> productGroups = FXCollections.observableArrayList();

    public ObservableList<OProductGroup> getProductGroups() {
        return productGroups;
    }

    public void loadProductGroups() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM [dbo].[GRUPE_PRODUSE]";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            productGroups.clear();
            while (resultSet.next()) {
                OProductGroup productGroup = new OProductGroup();
                productGroup.setIdProperty(resultSet.getInt("ID"));
                productGroup.setNameProperty(resultSet.getString("denumire"));
                productGroups.add(productGroup);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
