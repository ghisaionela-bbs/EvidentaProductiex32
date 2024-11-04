package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderAssociation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.OrderResultsDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Exceptions.OrderNotFound;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.sql.*;

public class OrderAssociationModel {
    private final ObservableList<OrderResultsDTO> orderSearchResults;
    private final ObjectProperty<ProductDTO> product;
    private OrderDTO order;

    public OrderAssociationModel(ProductDTO product) {
        this.orderSearchResults = FXCollections.observableArrayList();
        this.product = new SimpleObjectProperty<>(product);
    }

    public ObservableList<OrderResultsDTO> getOrderSearchResults() {
        return orderSearchResults;
    }

    public ProductDTO getProduct() {
        return product.get();
    }

    public ObjectProperty<ProductDTO> productProperty() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product.set(product);
    }

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }

    public OrderDTO getSelectedOrder(int orderID) {
        try {
            Connection connection = DBConnectionService.getConnection();

            String sql = "SELECT * FROM [dbo].[COMENZI] WHERE ID = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderID);
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()){
                throw new OrderNotFound("The order " + product.get().getID() + " was not found.");
            }

            Timestamp dateTime = resultSet.getTimestamp("datasiora_i");
            int USER_ID = resultSet.getInt("ID_UTILIZATOR_I");

            return order = new OrderDTO(orderID, dateTime, USER_ID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void loadSearchResults() {
        try {
            Connection connection = DBConnectionService.getConnection();

            String sql = "SELECT " +
                    "c.ID AS ORDER_ID, " +
                    "ic.ID AS ORDER_ITEM_ID, " +
                    "p.ID AS PRODUCT_ID, " +
                    "c.datasiora_i, " +
                    "p.denumire, " +
                    "ic.cantitate, " +
                    "p.um " +
                    "FROM [dbo].[COMENZI] AS c " +
                    "LEFT JOIN [dbo].[ITEME_COMENZI] AS ic ON c.ID = ic.ID_COMANDA " +
                    "LEFT JOIN [dbo].[PRODUSE] AS p ON p.ID = ic.ID_PRODUS " +
                    "WHERE ic.ID_PRODUS = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, product.get().getID());
            ResultSet resultSet = statement.executeQuery();

            orderSearchResults.clear();
            if(!resultSet.next()){
                throw new OrderNotFound("No order was found for order:" + product.get().getID());
            } else {
                do {
                    orderSearchResults.add(getOrderResultFromResultset(resultSet));
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private OrderResultsDTO getOrderResultFromResultset(ResultSet resultSet) throws SQLException {
        int ORDER_ID = resultSet.getInt("ORDER_ID");
        Timestamp orderDateAndTime = resultSet.getTimestamp("datasiora_i");
        String productName = resultSet.getString("denumire");
        double quantity = resultSet.getDouble("cantitate");
        String unitMeasurement = resultSet.getString("um");
        return new OrderResultsDTO(ORDER_ID, orderDateAndTime, productName, quantity, unitMeasurement);
    }



}
