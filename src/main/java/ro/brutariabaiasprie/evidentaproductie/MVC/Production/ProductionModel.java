package ro.brutariabaiasprie.evidentaproductie.MVC.Production;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductRecordDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;
import java.util.Calendar;

public class ProductionModel {
    private ObservableList<ProductRecordDTO> productRecords;
    private ObservableList<ProductDTO> products;

    public ProductionModel() {
        this.productRecords = FXCollections.observableArrayList();
        this.products = FXCollections.observableArrayList();
    }

    public ObservableList<ProductRecordDTO> getProductRecords() {
        return productRecords;
    }

    public void setProductRecords(ObservableList<ProductRecordDTO> productRecords) {
        this.productRecords = productRecords;
    }

    public ObservableList<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(ObservableList<ProductDTO> products) {
        this.products = products;
    }

    public void loadProductRecords() {
        try {
            Connection connection = DBConnectionService.getConnection();

            String whereCond = "";
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            if(user.getID_ROLE() != 0 || user.getID_ROLE() != 1) {
                whereCond = "WHERE ip.ID_UTILIZATOR_I = ? ";
            }

            String sql = "SELECT ip.ID, p.denumire, p.um, ip.cantitate, ip.datasiora_i FROM [dbo].[INREGISTRARI_PRODUSE] AS ip " +
                    "JOIN [dbo].[PRODUSE] AS p ON ip.ID_PRODUS = p.ID " +
                    whereCond +
                    "ORDER BY ip.datasiora_i DESC";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(user.getID_ROLE() != 0 || user.getID_ROLE() != 1) {
                statement.setInt(1, user.getID());
            }
            ResultSet resultSet = statement.executeQuery();

            productRecords.clear();
            while(resultSet.next()) {
                int productId = resultSet.getInt("ID");
                String name = resultSet.getString("denumire");
                String unitMeasurement = resultSet.getString("um");
                double quantity = resultSet.getDouble("cantitate");
                Timestamp dateAndTime = resultSet.getTimestamp("datasiora_i");
                ProductRecordDTO productRecordDTO = new ProductRecordDTO(productId, name, unitMeasurement, quantity, dateAndTime);
                productRecords.add(productRecordDTO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadProducts() {
        try {
            Connection connection = DBConnectionService.getConnection();

            String sql = "SELECT * FROM [dbo].[PRODUSE]";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            products.clear();
            while(resultSet.next()) {
                int ID = resultSet.getInt("ID");
                String name = resultSet.getString("denumire");
                String unitMeasurement = resultSet.getString("um");
                ProductDTO productDTO = new ProductDTO(ID, name, unitMeasurement);
                products.add(productDTO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProductRecordToDB(ProductDTO product, double quantity) {
        try {
            Connection connection = DBConnectionService.getConnection();

            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            String sql = "INSERT INTO [dbo].[INREGISTRARI_PRODUSE] (ID_PRODUS, cantitate, datasiora_i, ID_UTILIZATOR_I) VALUES (?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, product.getId());
            statement.setDouble(2, quantity);
            statement.setTimestamp(3, timestamp);
            statement.setInt(4, 1);
            statement.execute();

            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            int ID = keys.getInt(1);

            ProductRecordDTO productRecordDTO = new ProductRecordDTO(ID, product.getName(), product.getUnitMeasurement(), quantity, timestamp);

            productRecords.add(0, productRecordDTO);


        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
