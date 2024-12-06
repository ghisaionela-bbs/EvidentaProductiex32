package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderImport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
import ro.brutariabaiasprie.evidentaproductie.Domain.Product;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class OrderImportModel {
    private final ObservableList<Object[]> data = FXCollections.observableArrayList();
    private Object[][] dataArray;
    private int numRows = 0;
    private int numCols = 0;
    private final StringProperty filename = new SimpleStringProperty();
    private File file;
    private int[] idArray;
    public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//    public SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    public DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//    public SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
    private int sheetNumber;
    private int startRow;
    private int prodNameCol;
    private int quantityCol;
    private int dateCol;
    private int timeCol;

    public ObservableList<Object[]> getData() {
        return data;
    }

    public int getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(int sheetNumber) {
        this.sheetNumber = sheetNumber;
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

    public int getTimeCol() {
        return timeCol;
    }

    public void setTimeCol(int timeCol) {
        this.timeCol = timeCol;
    }

    public int getDateCol() {
        return dateCol;
    }

    public void setDateCol(int dateCol) {
        this.dateCol = dateCol;
    }

    public int getQuantityCol() {
        return quantityCol;
    }

    public void setQuantityCol(int quantityCol) {
        this.quantityCol = quantityCol;
    }

    public int getProdNameCol() {
        return prodNameCol;
    }

    public void setProdNameCol(int prodNameCol) {
        this.prodNameCol = prodNameCol;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void readWorkbook() {
        try {
            FileInputStream fileStream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fileStream);

            Sheet sheet = workbook.getSheetAt(sheetNumber);
            numRows = sheet.getLastRowNum() + 1;
            numCols = 0;
            for(int i = 0; i < numRows; i ++){
                Row row = sheet.getRow(i);
                int rowNumCols = row.getLastCellNum();
                if(rowNumCols > numCols) {
                    numCols = rowNumCols;
                }
            }

            dataArray = new Object[numRows + 1][numCols + 1];
            idArray = new int[numRows + 1];

            Row row;
            Cell cell;
            Object value = "";

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
                                cell.getCellStyle().getDataFormat();
                                value = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case NUMERIC:
                                if(DateUtil.isCellDateFormatted(cell)) {
                                    value = switch (BuiltinFormats.getBuiltinFormat(cell.getCellStyle().getDataFormat())) {
                                        case "m/d/yy" -> cell.getLocalDateTimeCellValue().toLocalDate();
                                        case "h:mm" -> cell.getLocalDateTimeCellValue().toLocalTime();
                                        default -> String.valueOf(cell.getLocalDateTimeCellValue());
                                    };
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

    public String validateData() {
        ArrayList<Product> products = new ArrayList<>();
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT " +
                    "p.ID, " +
                    "p.denumire, " +
                    "p.sarja, " +
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
                        resultSet.getDouble("sarja"),
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

        for(int rowIndex = startRow; rowIndex <= numRows; rowIndex ++) {
            String productName = dataArray[rowIndex][prodNameCol].toString();
            String quantity = dataArray[rowIndex][quantityCol].toString();
            Object date = dataArray[rowIndex][dateCol];
            Object time = dataArray[rowIndex][timeCol];

            if(productName.isEmpty()) {
                return "Aveti campuri goale in denumirile produselor.\n" +
                        "\nColoana: " + prodNameCol + " Rand: " + rowIndex;
            } else {
                boolean found = false;
                for( int i = 0; i < products.size() && !found; i++) {
                    if(products.get(i).getName().equals(productName.trim())) {
                        idArray[rowIndex] = products.get(i).getId();
                        found = true;
                    }
                }
                if(!found) {
                    return "Produsul cu numele " + productName.trim() + " nu a fost gasit in baza da date!" +
                            "\nColoana: " + prodNameCol + " Rand: " + rowIndex;
                }
            }
            if(quantity.isEmpty()) {
                return "Aveti campuri goale in cantitatile comandate ale produselor." +
                        "\nColoana: " + quantityCol + " Rand: " + rowIndex;
            } else {
                try {
                    Double.parseDouble(quantity);
                } catch (Exception e) {
                    return "Aveti campuri in coloana de cantitate care nu contin un format numeric corect." +
                            "\nColoana: " + quantityCol + " Rand: " + rowIndex;
                }
            }
            if(date == null) {
                return "Aveti campuri goale in coloana pentru data programata." +
                        "\nColoana: " + dateCol + " Rand: " + rowIndex;
            } else {
                if(!(date instanceof LocalDate)) {
                    return "Exista campuri in care data nu respecta formatul dd/mm/yyyy." +
                            "\nColoana: " + dateCol + " Rand: " + rowIndex;
                }
            }
            if(time == null) {
                return "Aveti campuri goale in coloana pentru ora." +
                        "\nColoana: " + dateCol + " Rand: " + rowIndex;
            } else {
                if(!(time instanceof LocalTime)) {
                    return "Exista campuri in care ora nu respecta formatul hh:mm." +
                            "\nColoana: " + dateCol + " Rand: " + rowIndex;
                }
            }
        }
        return "";
    }

    public void insertData() {
        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "INSERT INTO [dbo].[COMENZI] (ID_PRODUS, cantitate, data_programata, ID_UTILIZATOR_I) " +
                    "VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for(int rowIndex = startRow; rowIndex <= numRows; rowIndex ++) {
                double quantity = Double.parseDouble(dataArray[rowIndex][quantityCol].toString());
                LocalDate date = (LocalDate) dataArray[rowIndex][dateCol];
                LocalTime time = (LocalTime) dataArray[rowIndex][timeCol];
                Timestamp dateScheduled = Timestamp.valueOf(LocalDateTime.of(date, time));
                preparedStatement.setInt(1, idArray[rowIndex]);
                preparedStatement.setDouble(2, quantity);
                preparedStatement.setTimestamp(3, dateScheduled);
                preparedStatement.setInt(4, ConfigApp.getUser().getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
