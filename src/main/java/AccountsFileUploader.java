import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.io.FileInputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lalith on 9/23/15.
 */
public class AccountsFileUploader {
    private static final String FILE_PATH = "/Users/Lalith/Documents/InventoryManagementSystem/DataFeeds/accountsmaster_upload.xls";

    public static void main(String[] args) {
        List<Account> accounts = readProductFile(FILE_PATH);
        persistAccountList(accounts);


    }

    private static List<Account> readProductFile(String filePath) {
        List<Account> accountList = new ArrayList<Account>();
        try {

            FileInputStream file = new FileInputStream(FILE_PATH);

            // Use XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new HSSFWorkbook(file);

            int numberOfSheets = workbook.getNumberOfSheets();
            System.out.println("Number of sheets in the workbook : " + numberOfSheets);

            Sheet firstSheet = workbook.getSheetAt(0);
            for (Row myRow : firstSheet) {
                Account account = new Account();
                for (Cell myCell : myRow) {
                    switch (myCell.getColumnIndex()) {
                        case 0:
                            if(myCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                account.setName(String.valueOf(myCell.getNumericCellValue()));
                            else
                                account.setName(myCell.getStringCellValue());
                            break;
                        case 1:
                            account.setOutstandingBalance(myCell.getNumericCellValue());
                            break;
                        case 2:
                            account.setDebit(myCell.getNumericCellValue());
                            break;
                        case 3:
                            account.setCredit(myCell.getNumericCellValue());
                            break;
                    }
                }
                accountList.add(account);
            }
            file.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return accountList;
    }

    private static void persistAccountList(final List<Account> accounts) {
        try {
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriver(new com.mysql.jdbc.Driver());
            dataSource.setUrl("jdbc:mysql://localhost/inventory");
            dataSource.setUsername("root");
            //dataSource.setPassword("P@ssw0rd");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            String sql = "insert into accounts (name, outstandingBalance, debit, credit, createdDate, updatedDate) values ( ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    Account account = accounts.get(i);
                    preparedStatement.setString(1, account.getName());
                    preparedStatement.setDouble(2, (account.getOutstandingBalance() == null ? 0 : account.getOutstandingBalance()));
                    preparedStatement.setDouble(3, (account.getDebit() == null ? 0 : account.getDebit()));
                    preparedStatement.setDouble(4, (account.getCredit() == null ? 0 : account.getCredit()));
                    preparedStatement.setDate(5, new Date((new java.util.Date()).getTime()));
                    preparedStatement.setDate(6, new Date((new java.util.Date()).getTime()));
                }

                @Override
                public int getBatchSize() {
                    return accounts.size();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}