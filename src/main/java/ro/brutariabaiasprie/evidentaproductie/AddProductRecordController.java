package ro.brutariabaiasprie.evidentaproductie;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ro.brutariabaiasprie.evidentaproductie.Controllers.IController;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductDTO;
import ro.brutariabaiasprie.evidentaproductie.DTO.ProductRecordDTO;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddProductRecordController implements IController {
    Connection connection;
    ProductDTO productDTO;
    Stage stage;
    Parent root;

    @FXML
    Label lblProductName;

    @FXML
    TextField txtFldQuantity;

    @FXML
    Button btnBack;

    @FXML
    Label lblAddRecordError;

    @FXML
    GridPane gridPaneNumpad;

    @FXML
    TableView<ProductRecordDTO> tableView;

    public void setController(Stage stage) {
        this.stage = stage;
        txtFldQuantity.getProperties().put("vkType", "numeric");
        lblProductName.setText(productDTO.getName());
        lblAddRecordError.setVisible(false);
        setTableView();
        loadTableView();

        // force the field to be double only (10 decimals and 5 precision)
        txtFldQuantity.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {

                if (newValue != null && !newValue.isEmpty()) {
                    String filteredValue = newValue.replaceAll(" ", "");
                    Pattern pattern = Pattern.compile("(\\d{1,10}\\.\\d{1,2}|\\d{1,10}\\.|\\.\\d{1,2}|\\d{1,10})");
                    Matcher matcher = pattern.matcher(filteredValue);
                    if (matcher.find())
                    {
                        txtFldQuantity.setText(matcher.group(0));
                    } else {
                        txtFldQuantity.clear();
                    }
                } else {
                    txtFldQuantity.clear();
                }
            }
        });
        lblProductName.prefWidthProperty().bind(txtFldQuantity.widthProperty());
        gridPaneNumpad.prefWidthProperty().bind(txtFldQuantity.widthProperty());
    }

    private void setTableView() {
//        TableColumn<ProductRecordDTO, String> nameColumn = new TableColumn<>("Produs");
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        tableView.getColumns().add(nameColumn);
        tableView.setPlaceholder(new Label("Nu exista inregistrari pentru acest produs."));
        TableColumn<ProductRecordDTO, Double> quantityColumn = new TableColumn<>("Cantitate");
        quantityColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");
        quantityColumn.setPrefWidth(100);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(column -> {
            TableCell<ProductRecordDTO, Double> cell = new TableCell<ProductRecordDTO, Double>()  {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(String.format("%.2f", item));
                    }
                }
            };
            return cell;
        });
        tableView.getColumns().add(quantityColumn);

        TableColumn<ProductRecordDTO, Timestamp> dateAndTimeColumn = new TableColumn<>("Data si ora");
        dateAndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateAndTime"));
        quantityColumn.setPrefWidth(100);
        dateAndTimeColumn.setCellFactory(column -> {
            TableCell<ProductRecordDTO, Timestamp> cell = new TableCell<ProductRecordDTO, Timestamp>() {
                private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                @Override
                protected void updateItem(Timestamp item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(format.format(item));
                    }
                }
            };

            return cell;
        });
        tableView.getColumns().add(dateAndTimeColumn);

        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        quantityColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width
        dateAndTimeColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 50 ); // 50% width

    }

    public void loadTableView() {
        try {
            Statement statement = connection.createStatement();
            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            String userCond = "";
            if(user.getID_ROLE() != 1) {
                userCond = " AND ip.ID_UTILIZATOR=" + user.getID() + " ";
            }
            String sql = "SELECT p.ID, p.denumire, ip.cantitate, ip.datasiora FROM " +
                    "INREGISTRARI_PRODUSE AS ip join PRODUSE AS p ON ip.ID_PRODUS = p.ID" +
                    " " + "WHERE p.ID = " + productDTO.getId() + userCond +
                    " " + "ORDER BY datasiora DESC";
            ResultSet resultSet = statement.executeQuery(sql);
            tableView.getItems().clear();
            while (resultSet.next()) {
                int productId = resultSet.getInt("ID");
                String name = resultSet.getString("denumire");
                double quantity = resultSet.getDouble("cantitate");
                Timestamp dateAndTime = resultSet.getTimestamp("datasiora");
                ProductRecordDTO productRecordDTO = new ProductRecordDTO(productId, name, quantity, dateAndTime);
                tableView.getItems().add(productRecordDTO);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleBtnBackOnAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EvidentaProductie.class.getResource("product-list-view.fxml"));
        root = fxmlLoader.load();
        ProductListController productListController = fxmlLoader.getController();
        productListController.connection = connection;
        productListController.setController(stage);
        productListController.loadListView();
//        stage.setScene(new Scene(root));
        stage.getScene().setRoot(root);
        stage.show();
    }

    public void handleBtnAddProductRecord() {
        if(txtFldQuantity.getText().isEmpty()) {
            lblAddRecordError.setVisible(true);
            return;
        }
        try {
            lblAddRecordError.setVisible(false);

            double quantity = Double.parseDouble(txtFldQuantity.getText());
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());

            String sql = "INSERT INTO INREGISTRARI_PRODUSE (ID_PRODUS, cantitate, datasiora, ID_UTILIZATOR) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, productDTO.getId());
            preparedStatement.setDouble(2, quantity);
            preparedStatement.setTimestamp(3, timestamp);
            preparedStatement.setInt(4, user.getID());
            preparedStatement.execute();
            loadTableView();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\EvidentaProductie";
            }

            File theDir = new File(path);
            if (!theDir.exists()){
                theDir.mkdirs();
            }

            LocalDate dateFrom = excelExportController.datePickFrom.getValue();
            LocalDate dateTo = excelExportController.datePickTo.getValue();

            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            String userCond = "";
            if(user.getID_ROLE() != 1) {
                userCond = " AND ip.ID_UTILIZATOR=" + user.getID() + " ";
            }

            //Select records from database
            String sql = "SELECT p.ID, p.denumire, ip.cantitate, ip.datasiora FROM " +
                    "INREGISTRARI_PRODUSE AS ip join PRODUSE AS p ON ip.ID_PRODUS = p.ID" +
                    " WHERE p.ID = " + productDTO.getId() + userCond;

            if(dateFrom != null && dateTo != null) {
                sql += " AND ip.datasiora >= ? AND ip.datasiora <= ? ";
            } else if(dateFrom != null){
                sql += " AND ip.datasiora >= ? ";
            } else if(dateTo != null) {
                sql += " AND ip.datasiora <= ? ";
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

            //Select records from database
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
            // Create the header row
            CellStyle style = workbook.createCellStyle();
            Font font= workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            Row infoRow = sheet.createRow(0);
            infoRow.createCell(0).setCellValue("Raport pentru produsul " + productDTO.getName() + " generat in " +
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
                row.createCell(1).setCellValue((double) recordData.get(i)[1]);
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

//    @FXML
//    private void handleBtnNumpadOnAction(ActionEvent event) {
//        Button node = (Button) event.getSource() ;
//        String value = node.getText();
//        if("0123456789.".contains(value)) {
//            String quantity = txtFldQuantity.getText();
//            txtFldQuantity.setText(quantity + value);
//        } else if ("Sterge".equals(value)) {
//            String quantity = txtFldQuantity.getText();
//            if (quantity.isEmpty()){
//                return;
//            }
//            txtFldQuantity.setText(quantity.substring(0, quantity.length() - 1));
//        }
//    }

}
