package ro.brutariabaiasprie.evidentaproductie;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ro.brutariabaiasprie.evidentaproductie.Controllers.IController;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;


import javax.swing.filechooser.FileSystemView;
import java.io.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProductListController implements IController {
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
        setListView();
        btnNext.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
    }

    private void setListView() {
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
        listView.setCellFactory(new Callback<ListView<ProductDTO>, ListCell<ProductDTO>>() {
            @Override
            public ListCell<ProductDTO> call(ListView<ProductDTO> param) {
                return new ListCell<ProductDTO>() {

                    @Override
                    protected void updateItem(ProductDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Label lblId = new Label(item.getId() + ":");
                            Label lblProductName = new Label(item.getName());
                            HBox hBox = new HBox(lblId, lblProductName);
                            hBox.setSpacing(10);
                            hBox.setPadding(new Insets(10));
                            setText(null);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });
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
        if(selectedItem == null) {
            return;
        }
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
            FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("excel-export-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            ExcelExportController excelExportController = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setTitle("Exporta in excel");
            stage.setScene(scene);
            stage.showAndWait();
            if(!excelExportController.export) {
                return;
            }

            String path = "";
            if(ConfigApp.getConfig(CONFIG_KEY.EXCEL_EXPORT_PATH.name()) != null){
                path = (String) ConfigApp.getConfig(CONFIG_KEY.EXCEL_EXPORT_PATH.name());
            } else {
                path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\EvidentaProductie\\Rapoarte excel";
            }

            System.out.println(path);
            File theDir = new File(path);
            if (!theDir.exists()){
                theDir.mkdirs();
            }

            LocalDate dateFrom = excelExportController.datePickFrom.getValue();
            LocalDate dateTo = excelExportController.datePickTo.getValue();

            //Select records from database
            String sql = "SELECT p.ID, p.denumire, ip.cantitate, ip.datasiora " +
                    "FROM INREGISTRARI_PRODUSE AS ip join PRODUSE AS p ON ip.ID_PRODUS = p.ID ";

            if(dateFrom != null && dateTo != null) {
                sql += " WHERE ip.datasiora >= ? AND ip.datasiora <= ? ";
            } else if(dateFrom != null){
                sql += " WHERE ip.datasiora >= ? ";
            } else if(dateTo != null) {
                sql += " WHERE ip.datasiora <= ? ";
            }

            sql += "ORDER BY ip.datasiora DESC";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if(dateFrom != null && dateTo != null) {
                preparedStatement.setTimestamp(1, Timestamp.valueOf(dateFrom.atStartOfDay()));
                preparedStatement.setTimestamp(2, Timestamp.valueOf(dateTo.atTime(LocalTime.MAX)));
            } else if(dateFrom != null){
                preparedStatement.setTimestamp(1, Timestamp.valueOf(dateFrom.atStartOfDay()));
            } else if(dateTo != null) {
                preparedStatement.setTimestamp(1, Timestamp.valueOf(dateTo.atTime(LocalTime.MAX)));
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Object[]> recordData = new ArrayList<>();
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while (resultSet.next()) {
                String name = resultSet.getString("denumire");
                double quantity = resultSet.getDouble("cantitate");
                Timestamp dateAndTime = resultSet.getTimestamp("datasiora");
                String formatedDateTime = dateTimeFormatter.format(dateAndTime);
                recordData.add(new Object[]{name, quantity, formatedDateTime});
            }
            // Create a new Excel workbook
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
            SimpleDateFormat dateTimeTitleFormatter = new SimpleDateFormat("_ddMMyyyy_HHmm");
            String fileName = "EvidentaProductie" + dateTimeTitleFormatter.format(timestamp) + ".xlsx";
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Evidenta productie" + timestamp);
            // Create the first row where we store data like when it was generated
            CellStyle style = workbook.createCellStyle();
            Font font= workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            Row infoRow = sheet.createRow(0);
            infoRow.createCell(0).setCellValue("Raport pentru toate produsele generat in " + dateTimeFormatter.format(timestamp));
            infoRow.setRowStyle(style);
            infoRow.getCell(0).setCellStyle(style);
            // Create header row
            Row headerRow = sheet.createRow(1);
            headerRow.createCell(0).setCellValue("Produs");
            headerRow.getCell(0).setCellStyle(style);
            headerRow.createCell(1).setCellValue("Cantitate");
            headerRow.getCell(1).setCellStyle(style);
            headerRow.createCell(2).setCellValue("Data si ora");
            headerRow.getCell(2).setCellStyle(style);
            // Insert data into cells
            for (int i = 0; i < recordData.size(); i++) {
                Row row = sheet.createRow(i + 2); // Start from the third row
                row.createCell(0).setCellValue((String) recordData.get(i)[0]);
                row.createCell(1).setCellValue((double) recordData.get(i)[1]);
                row.createCell(2).setCellValue((String) recordData.get(i)[2]);
            }
            //Autosize columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            // Save the Excel file to a local directory
            FileOutputStream fileOut = new FileOutputStream(path + "\\" + fileName);
            workbook.write(fileOut);
            workbook.close();
            fileOut.close();
            Runtime.getRuntime().exec("explorer.exe /select,\"" + path + "\\" + fileName + "\"");
//            Desktop desktop = Desktop.getDesktop();
//            desktop.open(new File(path + "\\" + fileName));
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleBtnDisconnectOnAction() {
        try {
            ConfigApp.deleteConfig(CONFIG_KEY.APPUSER.name());
            ConfigApp.write_config();
            FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("login-view.fxml"));
            root = fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            loginController.connection = connection;
            loginController.setController(stage);
            stage.getScene().setRoot(root);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}