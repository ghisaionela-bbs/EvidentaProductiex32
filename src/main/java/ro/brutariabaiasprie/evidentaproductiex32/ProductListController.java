package ro.brutariabaiasprie.evidentaproductiex32;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.stage.Stage;
import jxl.write.DateTime;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kordamp.ikonli.javafx.FontIcon;
import ro.brutariabaiasprie.evidentaproductiex32.DTO.ProductDTO;


import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProductListController {
    Stage stage;
    Parent root;
    Connection connection;

    @FXML
    TextField txtFldSearchProduct;

    @FXML
    Button btnSearchProduct;

    @FXML
    Button btnAddProductRecord;

    @FXML
    Button btnNext;

    @FXML
    ListView<ProductDTO> listView;

    @FXML
    Button btnExcelExport;

    public void setController(Stage stage) {
        this.stage = stage;
        txtFldSearchProduct.getProperties().put("vkType", "text");
        txtFldSearchProduct.focusedProperty().addListener((ov, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue)
            {
                System.out.println("Textfield on focus");
            }
            else
            {
                System.out.println("Textfield out focus");
            }
        });
        //Setting up double click for elements in listview
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                //Use ListView's getSelected Item
                ProductDTO productDTO = listView.getSelectionModel().getSelectedItem();
                try {
                    handleListViewItemSelected(productDTO);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //Setting up double tap for elements in listview
        listView.setOnTouchPressed(event -> {
            if (event.getTouchCount() == 2) {
                //Use ListView's getSelected Item
                ProductDTO productDTO = listView.getSelectionModel().getSelectedItem();
                try {
                    handleListViewItemSelected(productDTO);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnNext.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
    }

    public void handleTxtFldSearchProduct() {
        if (!txtFldSearchProduct.getText().isEmpty()) {
            String condition = "WHERE denumire LIKE '%" +  txtFldSearchProduct.getText() + "%'";
            loadListView(condition);
            System.out.println("filtered");
        } else {
            loadListView();
        }
    }

    public void loadListView(String whereCondition) {
        try {
            listView.getItems().clear();
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM PRODUSE" + " " + whereCondition;
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println(sql);
            while (resultSet.next()) {
                int productID = resultSet.getInt("ID");
                String name = resultSet.getString("denumire");
                ProductDTO product = new ProductDTO(productID, name);
                listView.getItems().add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadListView() {
        loadListView("");
    }

    public void handleBtnAddProductOnAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("add-product-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
//            root = fxmlLoader.load();
            AddProductController addProductController = fxmlLoader.getController();
            addProductController.setController();
            Stage stage = new Stage();
            stage.setTitle("Adauga un produs");
            stage.setScene(scene);
//            stage.getScene().setRoot(root);
            stage.showAndWait();
            String productName = addProductController.txtFldProductName.getText();

            if (!productName.isEmpty()) {
                String sql = "INSERT INTO PRODUSE (denumire) VALUES (?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, productName);
                preparedStatement.execute();
                loadListView();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnNextOnAction() throws IOException {
        ProductDTO selectedItem = listView.getSelectionModel().getSelectedItem();
        System.out.println(selectedItem);

        FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("add-product-record-view.fxml"));
        root = fxmlLoader.load();
        AddProductRecordController addProductRecordController = fxmlLoader.getController();
        addProductRecordController.connection = connection;
        addProductRecordController.productDTO = selectedItem;
        addProductRecordController.setController(stage);
//        stage.setScene(new Scene(root));
        stage.getScene().setRoot(root);
        stage.show();
    }

    private void handleListViewItemSelected(ProductDTO selectedItem) throws IOException {
        System.out.println(selectedItem);
        FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("add-product-record-view.fxml"));
        root = fxmlLoader.load();
        AddProductRecordController addProductRecordController = fxmlLoader.getController();
        addProductRecordController.connection = connection;
        addProductRecordController.productDTO = selectedItem;
        addProductRecordController.setController(stage);
        stage.getScene().setRoot(root);
        stage.show();
    }

    public void handleBtnExcelExportOnAction() {
        try {
            String path = "";
            if(ConfigApp.getConfig("EXCEL_EXPORT_PATH") != null){
                path = (String) ConfigApp.getConfig("EXCEL_EXPORT_PATH");
            } else {
                path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\EvidentaProductie";
            }

            System.out.println(path);

            File theDir = new File(path);
            if (!theDir.exists()){
                theDir.mkdirs();
            }

            //Select records from database
            Statement statement = connection.createStatement();
            String sql = "SELECT p.ID, p.denumire, ip.cantitate, ip.datasiora " +
                    "FROM INREGISTRARI_PRODUSE AS ip join PRODUSE AS p ON ip.ID_PRODUS = p.ID " +
                    "ORDER BY datasiora DESC";
            ResultSet resultSet = statement.executeQuery(sql);
            List<Object[]> recordData = new ArrayList<>();
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            while (resultSet.next()) {
                String name = resultSet.getString("denumire");
                float quantity = resultSet.getFloat("cantitate");
                Timestamp dateAndTime = resultSet.getTimestamp("datasiora");
                String formatedDateTime = dateTimeFormatter.format(dateAndTime);
                recordData.add(new Object[]{name, quantity, formatedDateTime});
            }
            // Create a new Excel workbook
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
            SimpleDateFormat dateTimeTitleFormatter = new SimpleDateFormat("_MMddyyyy_HHmm");
            String fileName = "EvidentaProductie" + dateTimeTitleFormatter.format(timestamp) + ".xlsx";
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Evidenta productie" + timestamp);
            // Create the header row
            CellStyle style = workbook.createCellStyle();
            Font font= workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            Row infoRow = sheet.createRow(0);
            infoRow.createCell(0).setCellValue("Raport pentru toate produsele generat in " +
                    dateTimeFormatter.format(timestamp));
            infoRow.setRowStyle(style);
            infoRow.getCell(0).setCellStyle(style);
            Row headerRow = sheet.createRow(1);
            headerRow.createCell(0).setCellValue("Produs");
            headerRow.getCell(0).setCellStyle(style);
            headerRow.createCell(1).setCellValue("Cantitate");
            headerRow.getCell(1).setCellStyle(style);
            headerRow.createCell(2).setCellValue("Data si ora");
            headerRow.getCell(2).setCellStyle(style);
            for (int i = 0; i < recordData.size(); i++) {
                Row row = sheet.createRow(i + 2); // Start from the second row
                row.createCell(0).setCellValue((String) recordData.get(i)[0]);
                row.createCell(1).setCellValue((float) recordData.get(i)[1]);
                row.createCell(2).setCellValue((String) recordData.get(i)[2]);
            }
            // Save the Excel file to a local directory



            FileOutputStream fileOut = new FileOutputStream(path + "\\" + fileName);
            workbook.write(fileOut);

            Runtime.getRuntime().exec("explorer.exe /select,\"" + path + "\\" + fileName + "\"");
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}