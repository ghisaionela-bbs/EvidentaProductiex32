package ro.brutariabaiasprie.evidentaproductie.MVC.ModalWindows.OrderExport;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ro.brutariabaiasprie.evidentaproductie.Data.ACCESS_LEVEL;
import ro.brutariabaiasprie.evidentaproductie.Data.CONFIG_KEY;
import ro.brutariabaiasprie.evidentaproductie.Data.ConfigApp;
import ro.brutariabaiasprie.evidentaproductie.Domain.Group;
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
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private Workbook workbook;
    private final ObservableList<Group> groups = FXCollections.observableArrayList();
    private final ObservableList<Group> subgroups = FXCollections.observableArrayList();
    private ObservableList<Group> checkedGroups = FXCollections.observableArrayList();
    private ObservableList<Group> checkedSubgroups = FXCollections.observableArrayList();


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

    public LocalTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalTime timeEnd) {
        this.timeEnd = timeEnd;
    }

    public LocalTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalTime timeStart) {
        this.timeStart = timeStart;
    }

    public ObservableList<Group> getGroups() {
        return groups;
    }

    public ObservableList<Group> getSubgroups() {
        return subgroups;
    }

    public ObservableList<Group> getCheckedGroups() {
        return checkedGroups;
    }

    public void setCheckedGroups(ObservableList<Group> checkedGroups) {
        this.checkedGroups = checkedGroups;
    }

    public ObservableList<Group> getCheckedSubgroups() {
        return checkedSubgroups;
    }

    public void setCheckedSubgroups(ObservableList<Group> checkedSubgroups) {
        this.checkedSubgroups = checkedSubgroups;
    }

    public void loadSubgroups(ObservableList<Group> checkedGroups) {
        subgroups.clear();
        subgroups.add(null);
        if (ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.ADMINISTRATOR ||
                ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.DIRECTOR) {
            Group noSubgroupOption = new Group(-1, "Fara subgrupa");
            subgroups.add(noSubgroupOption);
        }
        if (groups.isEmpty()) {
            return;
        }

        String whereCond = " 1=1 ";
        switch (ConfigApp.getRole().getAccessLevel()) {
            case ADMINISTRATOR, MANAGER:
                break;
            case OPERATOR:
                whereCond += " AND ID = ? ";
                break;
            case UNAUTHORIZED:
                whereCond += " AND 1=0 ";
                break;
        }

        whereCond += " AND ( 1=0 ";
        for (int i = 0; i < checkedGroups.size(); i++) {
            if(checkedGroups.get(i) != null) {
                whereCond += " OR ID_GRUPA_PARINTE = ? ";
            }
        }
        whereCond += " ) ";

        try {
            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE_PRODUSE WHERE " + whereCond;
            PreparedStatement statement = connection.prepareStatement(sql);
            int paramCount = 1;

            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR, MANAGER:
                    break;
                case OPERATOR:
                    statement.setInt(paramCount, ConfigApp.getUser().getSubgroupId());
                    paramCount += 1;
                    break;
                case UNAUTHORIZED:
                    break;
            }

            for (int i = 0; i < checkedGroups.size(); i++) {
                if (checkedGroups.get(i) != null) {
                    statement.setInt(paramCount, checkedGroups.get(i).getId());
                    paramCount += 1;
                }
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                subgroups.add(new Group(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire"),
                        resultSet.getInt("ID_GRUPA_PARINTE")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
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
            createOrdersSheet(connection);
            createRecordsSheet(connection);
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

    private List<Object[]> getOrders(Connection connection) throws SQLException {
        String whereCond = "";
        switch (ConfigApp.getRole().getAccessLevel()) {
            case ADMINISTRATOR:
            case DIRECTOR:
                break;
//            case MANAGER:
//                whereCond += " AND gp.ID = ? ";
//                break;
//            case OPERATOR:
//                whereCond += " AND gp.ID = ? AND subg.ID = ? AND c.inchisa = 0 ";
//                break;
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

        whereCond += " AND CAST(c.data_programata AS TIME) >= CAST(? AS TIME) ";
        whereCond += " AND CAST(c.data_programata AS TIME) < CAST(? AS TIME) ";

        whereCond += " AND ( 1=0 ";
        if (ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.ADMINISTRATOR ||
                ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.DIRECTOR) {
            for (int i = 0; i < checkedGroups.size(); i++) {
                if(checkedGroups.get(i) != null) {
                    if (checkedGroups.get(i).getId() != -1) {
                        whereCond += " OR p.ID_GRUPA = ? ";
                    } else {
                        whereCond += " OR p.ID_GRUPA IS NULL ";
                    }
                } else {
                    whereCond += " OR 1=1 ";
                }
            }
        } else {
            for (int i = 0; i < checkedGroups.size(); i++) {
                if(checkedGroups.get(i) != null) {
                    if (checkedGroups.get(i).getId() != -1) {
                        whereCond += " OR p.ID_GRUPA = ? ";
                    }
                }
            }
        }
        whereCond += " ) ";

        whereCond += " AND ( 1=0 ";
        if (ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.ADMINISTRATOR ||
                ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.DIRECTOR) {
            for (int i = 0; i < checkedSubgroups.size(); i++) {
                if(checkedSubgroups.get(i) != null) {
                    if (checkedSubgroups.get(i).getId() != -1) {
                        whereCond += " OR p.ID_SUBGRUPA_PRODUSE = ? ";
                    } else {
                        whereCond += " OR p.ID_SUBGRUPA_PRODUSE IS NULL ";
                    }
                } else {
                    whereCond += " OR 1=1 ";
                }
            }
        } else {
            for (int i = 0; i < checkedSubgroups.size(); i++) {
                if(checkedSubgroups.get(i) != null) {
                    if (checkedSubgroups.get(i).getId() != -1) {
                        whereCond += " OR p.ID_SUBGRUPA_PRODUSE = ? ";
                    }
                }
            }
        }
        whereCond += " ) ";


        //Select records from database
        String sql = "SELECT c.ID, " +
                "c.contor, " +
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
                "ui.nume_utilizator AS nume_utilizator_i, " +
                "c.datasiora_m, " +
                "c.ID_UTILIZATOR_M, " +
                "um.nume_utilizator AS nume_utilizator_m, " +
                "c.inchisa " +
                "FROM COMENZI c " +
                "LEFT JOIN PRODUSE p ON p.ID = c.ID_PRODUS " +
                "LEFT JOIN REALIZARI r ON r.ID_COMANDA = c.ID " +
                "LEFT JOIN GRUPE_PRODUSE gp ON gp.ID = p.ID_GRUPA " +
                "LEFT JOIN GRUPE_PRODUSE subg ON subg.ID = p.ID_SUBGRUPA_PRODUSE " +
                "LEFT JOIN UTILIZATORI ui ON ui.ID = c.ID_UTILIZATOR_I " +
                "LEFT JOIN UTILIZATORI um ON um.ID = c.ID_UTILIZATOR_M " +
                "WHERE 1=1 " + whereCond +
                "GROUP BY c.ID, " +
                "c.contor, " +
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
                "c.inchisa, " +
                "ui.nume_utilizator, " +
                "um.nume_utilizator " +
                "ORDER BY c.data_programata ASC ";

        int paramCount = 1;
        PreparedStatement statement = connection.prepareStatement(sql);

        switch (ConfigApp.getRole().getAccessLevel()) {
            case ADMINISTRATOR:
            case DIRECTOR:
                break;
            case MANAGER:
                statement.setInt(paramCount, ConfigApp.getUser().getGroupId());
                paramCount += 1;
                break;
            case OPERATOR:
                statement.setInt(paramCount, ConfigApp.getUser().getGroupId());
                paramCount += 1;
                statement.setInt(paramCount, ConfigApp.getUser().getSubgroupId());
                paramCount += 1;
                break;
            case UNAUTHORIZED:
                break;
        }
        if(dateFrom != null && dateTo != null) {
            statement.setTimestamp(paramCount, Timestamp.valueOf(dateFrom.atStartOfDay()));
            paramCount += 1;
            statement.setTimestamp(paramCount, Timestamp.valueOf(dateTo.atTime(LocalTime.MAX)));
            paramCount += 1;
        } else if(dateFrom != null){
            statement.setTimestamp(paramCount, Timestamp.valueOf(dateFrom.atStartOfDay()));
            paramCount += 1;
        } else if(dateTo != null) {
            statement.setTimestamp(paramCount, Timestamp.valueOf(dateTo.atTime(LocalTime.MAX)));
            paramCount += 1;
        }

        statement.setTime(paramCount, Time.valueOf(timeStart));
        paramCount += 1;
        statement.setTime(paramCount, Time.valueOf(timeEnd));
        paramCount += 1;


        for (int i = 0; i < checkedGroups.size(); i++) {
            if(checkedGroups.get(i) != null) {
                if (checkedGroups.get(i).getId() != -1) {
                    statement.setInt(paramCount, checkedGroups.get(i).getId());
                    paramCount += 1;
                }
            }
        }

        for (int i = 0; i < checkedSubgroups.size(); i++) {
            if(checkedSubgroups.get(i) != null) {
                if (checkedSubgroups.get(i).getId() != -1) {
                    statement.setInt(paramCount, checkedSubgroups.get(i).getId());
                    paramCount += 1;
                }
            }
        }

        //Select records from database
        ResultSet resultSet = statement.executeQuery();
        List<Object[]> recordData = new ArrayList<>();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        while (resultSet.next()) {
            int counter = resultSet.getInt("contor");
            Timestamp dateAndTime = resultSet.getTimestamp("data_programata");
            String product = resultSet.getString("denumire");
            double quantity = resultSet.getDouble("cantitate");
            double completed = resultSet.getDouble("realizat");
            double remainder = resultSet.getDouble("rest");
            String modified = "";
            String username = "";
            Timestamp dateAndTimeModified = null;
            resultSet.getInt("ID_UTILIZATOR_M");
            if (resultSet.wasNull()) {
                username = resultSet.getString("nume_utilizator_i");
                modified = "";
            } else {
                username = resultSet.getString("nume_utilizator_m");
                modified = "DA";
                dateAndTimeModified = resultSet.getTimestamp("datasiora_m");
            }
            String formatedDate = dateFormatter.format(dateAndTime);
            String formatedTime = timeFormatter.format(dateAndTime);
            String formatedModifiedDate = "";
            String formatedModifiedTime = "";
            if(dateAndTimeModified != null) {
                formatedModifiedDate = dateFormatter.format(dateAndTimeModified);
                formatedModifiedTime = timeFormatter.format(dateAndTimeModified);
            }
            recordData.add(new Object[]{counter, formatedDate, formatedTime, product, quantity, completed, remainder,
                    username, modified, formatedModifiedDate, formatedModifiedTime});
        }
        return recordData;
    }

    private List<Object[]> getRecords(Connection connection) throws SQLException {

        String whereCond = "";
        if(dateFrom != null && dateTo != null) {
            whereCond += " AND r.datasiora_i >= ? AND r.datasiora_i <= ? ";
        } else if(dateFrom != null){
            whereCond += " AND r.datasiora_i >= ? ";
        } else if(dateTo != null) {
            whereCond += " AND r.datasiora_i <= ? ";
        }

        whereCond += " AND CAST(r.datasiora_i AS TIME) >= CAST(? AS TIME) ";
        whereCond += " AND CAST(r.datasiora_i AS TIME) < CAST(? AS TIME) ";


        whereCond += " AND ( 1=0 ";
        if (ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.ADMINISTRATOR ||
                ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.DIRECTOR) {
            for (int i = 0; i < checkedGroups.size(); i++) {
                if(checkedGroups.get(i) != null) {
                    if (checkedGroups.get(i).getId() != -1) {
                        whereCond += " OR p.ID_GRUPA = ? ";
                    } else {
                        whereCond += " OR p.ID_GRUPA IS NULL ";
                    }
                } else {
                    whereCond += " OR 1=1 ";
                }
            }
        } else {
            for (int i = 0; i < checkedGroups.size(); i++) {
                if(checkedGroups.get(i) != null) {
                    if (checkedGroups.get(i).getId() != -1) {
                        whereCond += " OR p.ID_GRUPA = ? ";
                    }
                }
            }
        }
        whereCond += " ) ";

        whereCond += " AND ( 1=0 ";
        if (ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.ADMINISTRATOR ||
                ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.DIRECTOR) {
            for (int i = 0; i < checkedSubgroups.size(); i++) {
                if(checkedSubgroups.get(i) != null) {
                    if (checkedSubgroups.get(i).getId() != -1) {
                        whereCond += " OR p.ID_SUBGRUPA_PRODUSE = ? ";
                    } else {
                        whereCond += " OR p.ID_SUBGRUPA_PRODUSE IS NULL ";
                    }
                } else {
                    whereCond += " OR 1=1 ";
                }
            }
        } else {
            for (int i = 0; i < checkedSubgroups.size(); i++) {
                if(checkedSubgroups.get(i) != null) {
                    if (checkedSubgroups.get(i).getId() != -1) {
                        whereCond += " OR p.ID_SUBGRUPA_PRODUSE = ? ";
                    }
                }
            }
        }
        whereCond += " ) ";

        //Select records from database
        String sql = "SELECT p.ID, " +
                "p.denumire, " +
                "r.cantitate, " +
                "r.datasiora_i, " +
                "r.ID_UTILIZATOR_M, " +
                "r.datasiora_m, " +
                "ui.nume_utilizator AS nume_utilizator_i, " +
                "um.nume_utilizator AS nume_utilizator_m " +
                "FROM REALIZARI AS r " +
                "LEFT JOIN PRODUSE AS p ON r.ID_PRODUS = p.ID " +
                "LEFT JOIN UTILIZATORI ui ON ui.ID = r.ID_UTILIZATOR_I " +
                "LEFT JOIN UTILIZATORI um ON um.ID = r.ID_UTILIZATOR_M " +
                "WHERE 1=1 " + whereCond +
                "ORDER BY r.datasiora_i DESC";

        int paramCount = 1;

        PreparedStatement statement = connection.prepareStatement(sql);
        if(dateFrom != null && dateTo != null) {
            statement.setTimestamp(1, Timestamp.valueOf(dateFrom.atStartOfDay()));
            paramCount += 1;
            statement.setTimestamp(2, Timestamp.valueOf(dateTo.atTime(LocalTime.MAX)));
            paramCount += 1;
        } else if(dateFrom != null){
            statement.setTimestamp(1, Timestamp.valueOf(dateFrom.atStartOfDay()));
            paramCount += 1;
        } else if(dateTo != null) {
            statement.setTimestamp(1, Timestamp.valueOf(dateTo.atTime(LocalTime.MAX)));
            paramCount += 1;
        }

        statement.setTime(paramCount, Time.valueOf(timeStart));
        paramCount += 1;
        statement.setTime(paramCount, Time.valueOf(timeEnd));
        paramCount += 1;

        for (int i = 0; i < checkedGroups.size(); i++) {
            if(checkedGroups.get(i) != null) {
                if (checkedGroups.get(i).getId() != -1) {
                    statement.setInt(paramCount, checkedGroups.get(i).getId());
                    paramCount += 1;
                }
            }
        }

        for (int i = 0; i < checkedSubgroups.size(); i++) {
            if(checkedSubgroups.get(i) != null) {
                if (checkedSubgroups.get(i).getId() != -1) {
                    statement.setInt(paramCount, checkedSubgroups.get(i).getId());
                    paramCount += 1;
                }
            }
        }

        //Select records from database
        ResultSet resultSet = statement.executeQuery();
        List<Object[]> recordData = new ArrayList<>();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        while (resultSet.next()) {
            String name = resultSet.getString("denumire");
            double quantity = resultSet.getDouble("cantitate");
            Timestamp dateAndTime = resultSet.getTimestamp("datasiora_i");
            String modified = "";
            String username = "";
            Timestamp dateAndTimeModified = null;
            resultSet.getInt("ID_UTILIZATOR_M");
            if (resultSet.wasNull()) {
                username = resultSet.getString("nume_utilizator_i");
                modified = "";
            } else {
                username = resultSet.getString("nume_utilizator_m");
                modified = "DA";
                dateAndTimeModified = resultSet.getTimestamp("datasiora_m");
            }
            String formatedDate = dateFormatter.format(dateAndTime);
            String formatedTime = timeFormatter.format(dateAndTime);
            String formatedModifiedDate = "";
            String formatedModifiedTime = "";
            if(dateAndTimeModified != null) {
                formatedModifiedDate = dateFormatter.format(dateAndTimeModified);
                formatedModifiedTime = timeFormatter.format(dateAndTimeModified);
            }
            recordData.add(new Object[]{name, quantity, formatedDate, formatedTime,
                username, modified, formatedModifiedDate, formatedModifiedTime});
        }
        return recordData;
    }

    private void createOrdersSheet(Connection connection) throws SQLException {
        List<Object[]> recordData = getOrders(connection);
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
        headerRow.createCell(1).setCellValue("Data plasare");
        headerRow.getCell(1).setCellStyle(headerStyle);
        headerRow.createCell(2).setCellValue("Ora plasare");
        headerRow.getCell(2).setCellStyle(headerStyle);
        headerRow.createCell(3).setCellValue("Produs");
        headerRow.getCell(3).setCellStyle(headerStyle);
        headerRow.createCell(4).setCellValue("Comandat");
        headerRow.getCell(4).setCellStyle(headerStyle);
        headerRow.createCell(5).setCellValue("Realizat");
        headerRow.getCell(5).setCellStyle(headerStyle);
        headerRow.createCell(6).setCellValue("Rest");
        headerRow.getCell(6).setCellStyle(headerStyle);
        headerRow.createCell(7).setCellValue("Utilizator");
        headerRow.getCell(7).setCellStyle(headerStyle);
        headerRow.createCell(8).setCellValue("Modificat");
        headerRow.getCell(8).setCellStyle(headerStyle);
        headerRow.createCell(9).setCellValue("Data modificare");
        headerRow.getCell(9).setCellStyle(headerStyle);
        headerRow.createCell(10).setCellValue("Ora modificare");
        headerRow.getCell(10).setCellStyle(headerStyle);
        for (int i = 0; i < recordData.size(); i++) {
            Row row = sheet.createRow(i + 2); // Start from the second row
            row.createCell(0).setCellValue((int) recordData.get(i)[0]);
            row.createCell(1).setCellValue((String) recordData.get(i)[1]);
            row.createCell(2).setCellValue((String) recordData.get(i)[2]);
            row.createCell(3).setCellValue((String) recordData.get(i)[3]);
            row.createCell(4).setCellValue((double) recordData.get(i)[4]);
            row.createCell(5).setCellValue((double) recordData.get(i)[5]);
            row.createCell(6).setCellValue((double) recordData.get(i)[6]);
            row.createCell(7).setCellValue((String) recordData.get(i)[7]);
            row.createCell(8).setCellValue((String) recordData.get(i)[8]);
            row.createCell(9).setCellValue((String) recordData.get(i)[9]);
            row.createCell(10).setCellValue((String) recordData.get(i)[10]);
        }
    }

    private void createRecordsSheet(Connection connection) throws SQLException {
        List<Object[]> recordData = getRecords(connection);

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
        headerRow.createCell(2).setCellValue("Data introducere");
        headerRow.getCell(2).setCellStyle(style);
        headerRow.createCell(3).setCellValue("Ora introducere");
        headerRow.getCell(3).setCellStyle(style);
        headerRow.createCell(4).setCellValue("Utilizator");
        headerRow.getCell(4).setCellStyle(style);
        headerRow.createCell(5).setCellValue("Modificata");
        headerRow.getCell(5).setCellStyle(style);
        headerRow.createCell(6).setCellValue("Data modificare");
        headerRow.getCell(6).setCellStyle(style);
        headerRow.createCell(7).setCellValue("Ora modificare");
        headerRow.getCell(7).setCellStyle(style);
        for (int i = 0; i < recordData.size(); i++) {
            Row row = sheet.createRow(i + 2); // Start from the second row
            row.createCell(0).setCellValue((String) recordData.get(i)[0]);
            row.createCell(1).setCellValue((double) recordData.get(i)[1]);
            row.createCell(2).setCellValue((String) recordData.get(i)[2]);
            row.createCell(3).setCellValue((String) recordData.get(i)[3]);
            row.createCell(4).setCellValue((String) recordData.get(i)[4]);
            row.createCell(5).setCellValue((String) recordData.get(i)[5]);
            row.createCell(6).setCellValue((String) recordData.get(i)[6]);
            row.createCell(7).setCellValue((String) recordData.get(i)[7]);
        }
    }

    public void loadGroups() {
        try {
            groups.clear();
            groups.add(null);
            if (ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.ADMINISTRATOR ||
                    ConfigApp.getRole().getAccessLevel() == ACCESS_LEVEL.DIRECTOR) {
                Group noGroupOption = new Group(-1, "Fara grupa");
                groups.add(noGroupOption);
            }
            String whereCond = " WHERE 1=1 ";
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR :
                    break;
                case MANAGER, OPERATOR:
                    whereCond += " AND ID = ? ";
                    break;
                case UNAUTHORIZED:
                    whereCond += " AND 1=0 ";
                    break;
            }

            Connection connection = DBConnectionService.getConnection();
            String sql = "SELECT * FROM GRUPE_PRODUSE " + whereCond + " AND ID_GRUPA_PARINTE IS NULL";
            PreparedStatement statement = connection.prepareStatement(sql);

            int paramCount = 1;
            switch (ConfigApp.getRole().getAccessLevel()) {
                case ADMINISTRATOR:
                    break;
                case MANAGER, OPERATOR:
                    statement.setInt(paramCount, ConfigApp.getUser().getGroupId());
                    paramCount += 1;
                    break;
                case UNAUTHORIZED:
                    break;
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                groups.add(new Group(
                        resultSet.getInt("ID"),
                        resultSet.getString("denumire")
                ));
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }
}
