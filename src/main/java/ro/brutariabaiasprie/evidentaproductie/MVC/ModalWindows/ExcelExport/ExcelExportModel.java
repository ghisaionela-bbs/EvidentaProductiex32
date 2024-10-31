package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.ExcelExport;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Data.User;
import ro.brutariabaiasprie.evidentaproductie.Services.DBConnectionService;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExcelExportModel {
    public void export(LocalDate dateFrom, LocalDate dateTo) {
        try {
            Connection connection = DBConnectionService.getConnection();

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

            User user = (User) ConfigApp.getConfig(CONFIG_KEY.APPUSER.name());
            String userCond = "";
            if(user.getID_ROLE() != 1 || user.getID_ROLE() != 0) {
                userCond = " AND ip.ID_UTILIZATOR_I =" + user.getID() + " ";
            }

            //Select records from database
            String sql = "SELECT p.ID, p.denumire, ip.cantitate, ip.datasiora_i FROM " +
                    "INREGISTRARI_PRODUSE AS ip join PRODUSE AS p ON ip.ID_PRODUS = p.ID" + userCond;

            if(dateFrom != null && dateTo != null) {
                sql += " AND ip.datasiora_i >= ? AND ip.datasiora_i <= ? ";
            } else if(dateFrom != null){
                sql += " AND ip.datasiora_i >= ? ";
            } else if(dateTo != null) {
                sql += " AND ip.datasiora_i <= ? ";
            }

            sql += "ORDER BY ip.datasiora_i DESC";

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
                Timestamp dateAndTime = resultSet.getTimestamp("datasiora_i");
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
            String title = "Raport generat in " + dateTimeFormatter.format(timestamp);
            if(dateFrom != null && dateTo != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                title += " pentru inregistrari introduse in intervalul: " + dateFrom.format(formatter) + " - " + dateTo.format(formatter);
            } else if(dateFrom != null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                title += " pentru inregistrari introduse din: " + dateFrom.format(formatter);
            } else if(dateTo != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                title += " pentru inregistrari introduse pana in: " + dateTo.format(formatter);
            }

            infoRow.createCell(0).setCellValue(title);
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
            fileOut.close();
            workbook.close();
            // Open the directory to see my saved file
            Runtime.getRuntime().exec("explorer.exe /select,\"" + path + "\\" + fileName + "\"");

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
