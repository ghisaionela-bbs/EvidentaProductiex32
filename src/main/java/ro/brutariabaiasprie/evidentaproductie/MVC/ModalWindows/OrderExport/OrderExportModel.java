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
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Workbook workbook;

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public void export() {
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

            workbook = new XSSFWorkbook();
            create_orders_sheet(connection);
            create_records_sheet(connection);
            // Save the Excel file to a local directory
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateTimeTitleFormatter = new SimpleDateFormat("_ddMMyyyy_HHmm");
            Timestamp timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
            String fileName = "EvidentaProductie" + dateTimeTitleFormatter.format(timestamp) + ".xlsx";
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

    private List<Object[]> get_orders(Connection connection) throws SQLException {
        String whereCond = "";
        switch (ConfigApp.getRole().getAccessLevel()) {
            case ADMINISTRATOR:
            case DIRECTOR:
                break;
            case MANAGER:
                whereCond += " AND gp.ID = ? ";
                break;
            case OPERATOR:
                whereCond += " AND gp.ID = ? AND subg.ID = ? AND c.inchisa = 0 ";
                break;
            case UNAUTHORIZED:
                whereCond += " AND 1=0 ";
        }

        if(dateFrom != null && dateTo != null) {
            whereCond += " AND c.data_programata >= ? AND c.data_programata <= ? ";
        } else if(dateFrom != null){
            whereCond += " AND c.data_programata >= ? ";
        } else if(dateTo != null) {
            whereCond += " AND c.data_programata <= ? ";
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
                "WHERE 1=1 " + whereCond +
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
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            Timestamp dateAndTime = resultSet.getTimestamp("data_programata");
            String product = resultSet.getString("denumire");
            double quantity = resultSet.getDouble("cantitate");
            double completed = resultSet.getDouble("realizat");
            double remainder = resultSet.getDouble("rest");
            String formatedDate = dateFormatter.format(dateAndTime);
            String formatedTime = timeFormatter.format(dateAndTime);
            recordData.add(new Object[]{id, formatedDate, formatedTime, product, quantity, completed, remainder});
        }
        return recordData;
    }

    private List<Object[]> get_records(Connection connection) throws SQLException {

        String whereCond = "";
        if(dateFrom != null && dateTo != null) {
            whereCond += " AND r.datasiora_i >= ? AND r.datasiora_i <= ? ";
        } else if(dateFrom != null){
            whereCond += " AND r.datasiora_i >= ? ";
        } else if(dateTo != null) {
            whereCond += " AND r.datasiora_i <= ? ";
        }

        //Select records from database
        String sql = "SELECT p.ID, " +
                "p.denumire, " +
                "r.cantitate, " +
                "r.datasiora_i " +
                "FROM REALIZARI AS r " +
                "LEFT JOIN PRODUSE AS p ON r.ID_PRODUS = p.ID " +
                "WHERE 1=1 " + whereCond +
                "ORDER BY r.datasiora_i DESC";

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
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        while (resultSet.next()) {
            String name = resultSet.getString("denumire");
            double quantity = resultSet.getDouble("cantitate");
            Timestamp dateAndTime = resultSet.getTimestamp("datasiora_i");
            String formatedDate = dateFormatter.format(dateAndTime);
            String formatedTime = timeFormatter.format(dateAndTime);
            recordData.add(new Object[]{name, quantity, formatedDate, formatedTime});
        }
        return recordData;
    }

    private void create_orders_sheet(Connection connection) throws SQLException {
        List<Object[]> recordData = get_orders(connection);
        // Create a new Excel workbook
        Calendar calendar = Calendar.getInstance();
        Timestamp timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Sheet sheet = workbook.createSheet("Comenzi");
        // Create the header row
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
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
        headerRow.createCell(1).setCellValue("Data");
        headerRow.getCell(1).setCellStyle(headerStyle);
        headerRow.createCell(2).setCellValue("Ora");
        headerRow.getCell(2).setCellStyle(headerStyle);
        headerRow.createCell(3).setCellValue("Produs");
        headerRow.getCell(3).setCellStyle(headerStyle);
        headerRow.createCell(4).setCellValue("Comandat");
        headerRow.getCell(4).setCellStyle(headerStyle);
        headerRow.createCell(5).setCellValue("Realizat");
        headerRow.getCell(5).setCellStyle(headerStyle);
        headerRow.createCell(6).setCellValue("Rest");
        headerRow.getCell(6).setCellStyle(headerStyle);
        for (int i = 0; i < recordData.size(); i++) {
            Row row = sheet.createRow(i + 2); // Start from the second row
            row.createCell(0).setCellValue((int) recordData.get(i)[0]);
            row.createCell(1).setCellValue((String) recordData.get(i)[1]);
            row.createCell(2).setCellValue((String) recordData.get(i)[2]);
            row.createCell(3).setCellValue((String) recordData.get(i)[3]);
            row.createCell(4).setCellValue((double) recordData.get(i)[4]);
            row.createCell(5).setCellValue((double) recordData.get(i)[5]);
            row.createCell(6).setCellValue((double) recordData.get(i)[6]);
        }
    }

    private void create_records_sheet(Connection connection) throws SQLException {
        List<Object[]> recordData = get_records(connection);

        // Create a new Excel workbook
        Calendar calendar = Calendar.getInstance();
        Timestamp timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Sheet sheet = workbook.createSheet("Realizari");
        // Create the header row
        CellStyle style = workbook.createCellStyle();
        Font font= workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        Row infoRow = sheet.createRow(0);
        String title = "Raport generat in " + dateTimeFormatter.format(timestamp);
        if(dateFrom != null && dateTo != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            title += " pentru realizari introduse in intervalul: " + dateFrom.format(formatter) + " - " + dateTo.format(formatter);
        } else if(dateFrom != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            title += " pentru realizari introduse din: " + dateFrom.format(formatter);
        } else if(dateTo != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            title += " pentru realizari introduse pana in: " + dateTo.format(formatter);
        }

        infoRow.createCell(0).setCellValue(title);
        infoRow.setRowStyle(style);
        infoRow.getCell(0).setCellStyle(style);
        Row headerRow = sheet.createRow(1);
        headerRow.createCell(0).setCellValue("Produs");
        headerRow.getCell(0).setCellStyle(style);
        headerRow.createCell(1).setCellValue("Cantitate");
        headerRow.getCell(1).setCellStyle(style);
        headerRow.createCell(2).setCellValue("Data");
        headerRow.getCell(2).setCellStyle(style);
        headerRow.createCell(3).setCellValue("Ora");
        headerRow.getCell(3).setCellStyle(style);
        for (int i = 0; i < recordData.size(); i++) {
            Row row = sheet.createRow(i + 2); // Start from the second row
            row.createCell(0).setCellValue((String) recordData.get(i)[0]);
            row.createCell(1).setCellValue((double) recordData.get(i)[1]);
            row.createCell(2).setCellValue((String) recordData.get(i)[2]);
            row.createCell(3).setCellValue((String) recordData.get(i)[3]);
        }
    }
}
