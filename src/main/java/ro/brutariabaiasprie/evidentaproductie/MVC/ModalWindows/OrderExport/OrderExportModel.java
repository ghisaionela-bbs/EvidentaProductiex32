package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderExport;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
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

public class OrderExportModel {
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

            int paramNo = 0;
            String whereCond = "";
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                case DIRECTOR:
                    break;
                case MANAGER:
                    whereCond += "WHERE gp.ID = ? ";
                    paramNo += 1;
                    break;
                case OPERATOR:
                    whereCond += "WHERE gp.ID = ? AND subg.ID = ? AND c.inchisa = 0 ";
                    paramNo += 2;
                    break;
                case UNAUTHORIZED:
                    whereCond += "WHERE 1=0 ";
            }

            if(dateFrom != null && dateTo != null) {
                whereCond += " AND r.datasiora_i >= ? AND r.datasiora_i <= ? ";
                paramNo += 2;
            } else if(dateFrom != null){
                whereCond += " AND r.datasiora_i >= ? ";
                paramNo += 1;
            } else if(dateTo != null) {
                whereCond += " AND r.datasiora_i <= ? ";
                paramNo += 1;
            }

            //Select records from database
            String sql = "SELECT c.ID, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.um, " +
                    "gp.ID AS ID_GRUPA, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "gp.denumire AS denumire_grupa, " +
                    "c.data_programata, " +
                    "c.cantitate, " +
                    "SUM(COALESCE(r.cantitate, 0.00)) AS realizat, " +
                    "c.cantitate - SUM(COALESCE(r.cantitate, 0.00)) AS rest, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.inchisa " +
                    "FROM COMENZI c " +
                    "LEFT JOIN PRODUSE p ON p.ID = c.ID_PRODUS " +
                    "LEFT JOIN REALIZARI r ON r.ID_COMANDA = c.ID " +
                    "LEFT JOIN GRUPE_PRODUSE gp ON gp.ID = p.ID_GRUPA " +
                    "LEFT JOIN GRUPE_PRODUSE subg ON subg.ID = p.ID_SUBGRUPA_PRODUSE " +
                    whereCond +
                    "GROUP BY c.ID, " +
                    "c.data_programata, " +
                    "c.ID_PRODUS, " +
                    "p.denumire, " +
                    "p.um, " +
                    "gp.ID, " +
                    "p.ID_SUBGRUPA_PRODUSE, " +
                    "gp.denumire, " +
                    "c.cantitate, " +
                    "c.datasiora_i, " +
                    "c.ID_UTILIZATOR_I, " +
                    "c.datasiora_m, " +
                    "c.ID_UTILIZATOR_M, " +
                    "c.inchisa " +
                    "ORDER BY c.data_programata ASC ";

            int curr_param = 1;
            PreparedStatement statement = connection.prepareStatement(sql);

            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                case DIRECTOR:
                    break;
                case MANAGER:
                    statement.setInt(curr_param, ConfigApp.getUser().getGroupId());
                    curr_param += 1;
                    break;
                case OPERATOR:
                    statement.setInt(curr_param, ConfigApp.getUser().getGroupId());
                    curr_param += 1;
                    statement.setInt(curr_param, ConfigApp.getUser().getSubgroupId());
                    curr_param += 1;
                    break;
                case UNAUTHORIZED:
                    break;
            }
            if(dateFrom != null && dateTo != null) {
                statement.setTimestamp(curr_param, Timestamp.valueOf(dateFrom.atStartOfDay()));
                curr_param += 1;
                statement.setTimestamp(curr_param, Timestamp.valueOf(dateTo.atTime(LocalTime.MAX)));
                curr_param += 1;
            } else if(dateFrom != null){
                statement.setTimestamp(curr_param, Timestamp.valueOf(dateFrom.atStartOfDay()));
                curr_param += 1;
            } else if(dateTo != null) {
                statement.setTimestamp(curr_param, Timestamp.valueOf(dateTo.atTime(LocalTime.MAX)));
                curr_param += 1;
            }


            //Select records from database
            ResultSet resultSet = statement.executeQuery();
            List<Object[]> recordData = new ArrayList<>();
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                Timestamp dateAndTime = resultSet.getTimestamp("data_programata");
                String product = resultSet.getString("denumire");
                double quantity = resultSet.getDouble("cantitate");
                double completed = resultSet.getDouble("realizat");
                double remainder = resultSet.getDouble("rest");
                String formatedDateTime = dateTimeFormatter.format(dateAndTime);
                recordData.add(new Object[]{id, formatedDateTime, product, quantity, completed, remainder});
            }
            // Create a new Excel workbook
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
            SimpleDateFormat dateTimeTitleFormatter = new SimpleDateFormat("_ddMMyyyy_HHmm");
            String fileName = "EvidentaProductie" + dateTimeTitleFormatter.format(timestamp) + ".xlsx";
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Evidenta productie" + timestamp);
            // Create the header row
            CellStyle headerStyle = workbook.createCellStyle();
            Font font= workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            Row infoRow = sheet.createRow(0);
            String title = "Raport generat in " + dateTimeFormatter.format(timestamp);
            if(dateFrom != null && dateTo != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                title += " pentru comenzi programate in intervalul: " + dateFrom.format(formatter) + " - " + dateTo.format(formatter);
            } else if(dateFrom != null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                title += " pentru comenzi programate din: " + dateFrom.format(formatter);
            } else if(dateTo != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                title += " pentru comenzi programate pana in: " + dateTo.format(formatter);
            }

            infoRow.createCell(0).setCellValue(title);
            infoRow.setRowStyle(headerStyle);
            infoRow.getCell(0).setCellStyle(headerStyle);
            Row headerRow = sheet.createRow(1);
            headerRow.createCell(0).setCellValue("Nr");
            headerRow.getCell(0).setCellStyle(headerStyle);
            headerRow.createCell(1).setCellValue("Data si ora");
            headerRow.getCell(1).setCellStyle(headerStyle);
            headerRow.createCell(2).setCellValue("Produs");
            headerRow.getCell(2).setCellStyle(headerStyle);
            headerRow.createCell(3).setCellValue("Comandat");
            headerRow.getCell(3).setCellStyle(headerStyle);
            headerRow.createCell(4).setCellValue("Realizat");
            headerRow.getCell(4).setCellStyle(headerStyle);
            headerRow.createCell(5).setCellValue("Rest");
            headerRow.getCell(5).setCellStyle(headerStyle);
            for (int i = 0; i < recordData.size(); i++) {
                Row row = sheet.createRow(i + 2); // Start from the second row
                row.createCell(0).setCellValue((int) recordData.get(i)[0]);
                row.createCell(1).setCellValue((String) recordData.get(i)[1]);
                row.createCell(2).setCellValue((String) recordData.get(i)[2]);
                row.createCell(3).setCellValue((double) recordData.get(i)[3]);
                row.createCell(4).setCellValue((double) recordData.get(i)[4]);
                row.createCell(5).setCellValue((double) recordData.get(i)[5]);
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
