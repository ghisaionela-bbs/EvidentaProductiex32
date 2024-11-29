package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderImport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class OrderImportModel {
    private final ObservableList<String[]> data = FXCollections.observableArrayList();
    private String[][] dataArray;
    private int numRows = 0;
    private int numCols = 0;
    private final StringProperty filename = new SimpleStringProperty();
    private File file;
    private int[] idArray;


    public ObservableList<String[]> getData() {
        return data;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public String getFilename() {
        return filename.get();
    }

    public StringProperty filenameProperty() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename.set(filename);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void readWorkbook() {
        try {
            FileInputStream fileStream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fileStream);

            Sheet sheet = workbook.getSheetAt(0);
            numRows = sheet.getLastRowNum();
            numCols = 0;
            for(int i = 0; i < numRows; i ++){
                Row row = sheet.getRow(i);
                int rowNumCols = row.getLastCellNum();
                if(rowNumCols > numCols) {
                    numCols = rowNumCols;
                }
            }

            dataArray = new String[numRows + 1][numCols + 1];
            idArray = new int[numRows + 1];

            Row row;
            Cell cell;
            String value = "";

            for(int r = 1; r <= numRows; r ++) {
                dataArray[r][0] = String.valueOf(r);
            }
            for(int c = 1; c <= numCols; c ++) {
                dataArray[0][c] = String.valueOf(c);
            }

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for(int rowIndex = 0; rowIndex < numRows; rowIndex ++) {
                row = sheet.getRow(rowIndex);
                for(int columnIndex = 0; columnIndex < numCols; columnIndex ++) {
                    cell = row.getCell(columnIndex);
                    CellValue cellValue = evaluator.evaluate(cell);
                    if (cellValue!=null) {
                        switch (cellValue.getCellType()) {
                            case BOOLEAN:
                                value = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case NUMERIC:
                                if(DateUtil.isCellDateFormatted(cell)) {
                                    value = String.valueOf(cell.getDateCellValue());
                                } else {
                                    value = String.valueOf(cell.getNumericCellValue());
                                }
                                break;
                            case STRING:
                                value = cell.getStringCellValue();
                                break;
                            case BLANK:
                                break;
                            case ERROR:
                                value = String.valueOf(cell.getErrorCellValue());
                                break;
                            // CELL_TYPE_FORMULA will never occur
                            case FORMULA:
                                break;
                            case _NONE:
                                value = String.valueOf(cell.getStringCellValue());
                        }
                    }
                    dataArray[rowIndex + 1][columnIndex + 1] = value;
                }
            }
            data.clear();
            data.addAll(Arrays.asList(dataArray));

            workbook.close();
            fileStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String validateData(int startRow, int prodNamCol, int quantityCol, int dateCol, int timeCol) {
        ArrayList<Product> products = new ArrayList<>();
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT " +
                    "p.ID, " +
                    "p.denumire, " +
                    "p.um, " +
                    "p.ID_GRUPA, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "gp.denumire AS denumire_grupa " +
                    "FROM PRODUSE p " +
                    "LEFT JOIN GRUPE_PRODUSE gp ON p.ID_GRUPA = gp.ID " +
                    "ORDER BY p.um, p.denumire ASC";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            products.clear();
            while(resultSet.next()) {
                Group group = null;
                int groupId = resultSet.getInt("ID_GRUPA");
                if(!resultSet.wasNull()) {
                    group = new Group(groupId,
                            resultSet.getString("denumire_grupa"));
                }
                Product product = new Product(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire"),
                        resultSet.getString("um"),
                        group,
                        resultSet.getInt("ID_SUBGRUPA_PRODUSE")
                );
                product.setGroup(group);
                products.add(product);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");

        for(int rowIndex = startRow; rowIndex <= numRows; rowIndex ++) {
            String productName = dataArray[rowIndex][prodNamCol];
            String quantity = dataArray[rowIndex][quantityCol];
            String date = dataArray[rowIndex][dateCol];
            String time = dataArray[rowIndex][timeCol];

            if(productName.isEmpty()) {
                return "Aveti campuri goale in denumirile produselor.";
            } else {
                boolean found = false;
                for(Product product : products) {
                    if(product.getName().equals(productName.trim())) {
                        idArray[rowIndex] = product.getId();
                        found = true;
                    }
                }
                if(!found) {
                    idArray = new int[rowIndex + 1];
                    return "Produsul cu numele " + productName.trim() + " nu a fost gasit in baza da date!";
                }
            }
            if(quantity.isEmpty()) {
                return "Aveti campuri goale in unitatile de masura ale produselor.";
            } else {
                try {
                    Double.parseDouble(quantity);
                } catch (Exception e) {
                    return "Aveti campuri in coloana de cantitate care nu contin un format numeric corect.";
                }
            }
            if(date.isEmpty()) {
                return "Aveti campuri goale in coloana pentru data.";
            } else {
                try{
                    dateFormatter.parse(date);
                } catch (ParseException e) {
                    return "Exista campuri in care data nu respecta formatul dd/MM/yyyy";
                }
            }
            if(time.isEmpty()) {
                return "Aveti campuri goale in coloana pentru ora.";
            } else {
                try{
                    timeFormatter.parse(date);
                } catch (ParseException e) {
                    return "Exista campuri in care ora nu respecta formatul hh:mm";
                }
            }
        }



        return "";
    }

    public void insertData(int startRow, int prodNamCol, int quantityCol, int dateCol, int timeCol) {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "INSERT INTO [dbo].[COMENZI] (denumire, cantitate, data_programata, ID_PRODUS, ID_UTILIZATOR_I) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for(int rowIndex = startRow; rowIndex <= numRows; rowIndex ++) {
                String productName = dataArray[rowIndex][prodNamCol];
                double quantity = Double.parseDouble(dataArray[rowIndex][quantityCol]);
                String date = dataArray[rowIndex][dateCol];
                String time = dataArray[rowIndex][timeCol];
                LocalDate localDate = LocalDate.parse(date);
                LocalTime localTime = LocalTime.parse(time);
                Timestamp dateScheduled = Timestamp.valueOf(LocalDateTime.of(localDate, localTime));

                preparedStatement.setString(1, productName.trim());
                preparedStatement.setDouble(2, quantity);
                preparedStatement.setTimestamp(3, dateScheduled);
                preparedStatement.setInt(4, idArray[rowIndex]);
                preparedStatement.setInt(5, ConfigApp.getUser().getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
