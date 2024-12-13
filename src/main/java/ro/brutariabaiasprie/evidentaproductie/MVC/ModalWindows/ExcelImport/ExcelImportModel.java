package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelImport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class ExcelImportModel {
    private final ObservableList<Object[]> data = FXCollections.observableArrayList();
    private Object[][] dataArray;
    private int numRows = 0;
    private int numCols = 0;
    private final StringProperty filename = new SimpleStringProperty();
    private File file;
    private int sheetNumber;
    private int startRow;
    private int prodNameCol;
    private int batchCol;
    private int umCol;
    private int maxSheetNumber;


    public ObservableList<Object[]> getData() {
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

    public void setUmCol(int umCol) {
        this.umCol = umCol;
    }

    public void setBatchCol(int batchCol) {
        this.batchCol = batchCol;
    }

    public void setProdNameCol(int prodNameCol) {
        this.prodNameCol = prodNameCol;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setSheetNumber(int sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public String readWorkbook() {
        try {
            FileInputStream fileStream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fileStream);

            maxSheetNumber = workbook.getNumberOfSheets();
            if(maxSheetNumber < sheetNumber) {
                workbook.close();
                return "Numarul sheet-ului din care doriti sa preluati datele este mai mare decat numarul total de sheet-uri din document!";
            }

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
                                value = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case NUMERIC:
                                value = cell.getNumericCellValue();
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
                                value = "";
                                break;
                        }
                    } else {
                        value = null;
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

        return "";
    }

    public String validateData() {

        ArrayList<String> ums = new ArrayList<>();
        ums.add("KG");
        ums.add("BUC");

        for(int rowIndex = startRow; rowIndex <= numRows; rowIndex ++) {
            String productName = (String) dataArray[rowIndex][prodNameCol];
            String unitMeasurement = (String) dataArray[rowIndex][umCol];

            if(productName.isEmpty()) {
                return "Aveti campuri goale in denumirile produselor.";
            }
            if(dataArray[rowIndex][batchCol] != null) {
                try {
                    Double.parseDouble(dataArray[rowIndex][batchCol].toString());
                } catch (Exception e) {
                    return "Toate datele din coloana de valori sarja trebuie sa fie numerice.";
                }
            }
            if(unitMeasurement.isEmpty()) {
                return "Aveti campuri goale in unitatile de masura ale produselor.";
            } else {
                if(!ums.contains(unitMeasurement.trim().toUpperCase())) {
                    return "Unitatile de masura pot fi doar KG sau BUC.";
                }
            }
        }
        return "";
    }

    public void insertData() {

        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "INSERT INTO [dbo].[PRODUSE] (denumire, sarja, um) VALUES (?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for(int rowIndex = startRow; rowIndex <= numRows; rowIndex ++) {
                String productName = (String) dataArray[rowIndex][prodNameCol];
                String unitMeasurement = (String) dataArray[rowIndex][umCol];

                preparedStatement.setString(1, productName.trim());
                if(dataArray[rowIndex][batchCol] == null) {
                    preparedStatement.setDouble(2, 0.00);
                } else {
                    preparedStatement.setDouble(2, (double) dataArray[rowIndex][batchCol]);
                }
                preparedStatement.setString(3, unitMeasurement.trim().toUpperCase());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
