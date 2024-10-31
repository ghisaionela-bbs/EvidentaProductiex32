package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelImport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
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
    private final ObservableList<String[]> data = FXCollections.observableArrayList();
    private String[][] dataArray;
    private int numRows = 0;
    private int numCols = 0;
    private final StringProperty filename = new SimpleStringProperty();
    private File file;


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
                                value = String.valueOf(cell.getNumericCellValue());
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

    public String validateData(int startRow, int prodNamCol, int umCol) {

        ArrayList<String> ums = new ArrayList<>();
        ums.add("KG");
        ums.add("BUC");

        for(int rowIndex = startRow; rowIndex <= numRows; rowIndex ++) {
            String productName = dataArray[rowIndex][prodNamCol];
            String unitMeasurement = dataArray[rowIndex][umCol];

            if(productName.isEmpty()) {
                return "Aveti campuri goale in denumirile produselor.";
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

    public void insertData(int startRow, int prodNamCol, int umCol) {

        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "INSERT INTO [dbo].[PRODUSE] (denumire, um) VALUES (?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for(int rowIndex = startRow; rowIndex <= numRows; rowIndex ++) {
                String productName = dataArray[rowIndex][prodNamCol];
                String unitMeasurement = dataArray[rowIndex][umCol];

                preparedStatement.setString(1, productName.trim());
                preparedStatement.setString(2, unitMeasurement.trim().toUpperCase());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
