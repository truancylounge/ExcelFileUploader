

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
 * Created by Lalith on 9/22/15.
 */
public class FileUploader {

    private static final String FILE_PATH = "/Users/Lalith/Documents/InventoryManagementSystem/DataFeeds/productmaster_bak.xls";

    public static void main(String[] args) {
        List<Product> productList = readProductFile(FILE_PATH);
        persistProductList(productList);
    }

    private static List<Product> readProductFile(String filePath) {
        List<Product> productList = new ArrayList<Product>();
        try {

            FileInputStream file = new FileInputStream(FILE_PATH);

            // Use XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new HSSFWorkbook(file);

            int numberOfSheets = workbook.getNumberOfSheets();
            System.out.println("Number of sheets in the workbook : " + numberOfSheets);

            String productType = null;
            Sheet firstSheet = workbook.getSheetAt(0);
            for (Row myRow : firstSheet) {
                Product product = new Product();

                Boolean skipProduct = Boolean.FALSE;

                for (Cell myCell : myRow) {
                    switch (myCell.getColumnIndex()) {
                        case 0:
                            product.setProductName(myCell.getRichStringCellValue().getString().trim());
                            break;
                        case 1:
                            product.setSellingRate(myCell.getNumericCellValue());
                            break;
                        case 2:
                            product.setCostRate(myCell.getNumericCellValue());
                            break;
                        case 3:
                            if (myCell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                product.setProductCode(String.valueOf((int) myCell.getNumericCellValue()));
                            else
                                product.setProductCode(myCell.getStringCellValue().trim());
                            break;
                        case 4:
                            if (myCell.getRichStringCellValue().getString() != null) {
                                productType = myCell.getRichStringCellValue().getString().trim();
                                skipProduct = Boolean.TRUE;
                            } else
                                skipProduct = Boolean.FALSE;
                            break;
                    }
                }
                if(!skipProduct) {
                    product.setProductType(productType);
                    productList.add(product);
                }

            }
            file.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return productList;
    }

    private static void persistProductList(final List<Product> products) {
        try {
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriver(new com.mysql.jdbc.Driver());
            dataSource.setUrl("jdbc:mysql://localhost/inventory");
            dataSource.setUsername("root");
            //dataSource.setPassword("P@ssw0rd");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            String sql = "insert into products (productName, productCode, productType, sellingRate, costRate, createdDate, updatedDate) values ( ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    Product product = products.get(i);
                    preparedStatement.setString(1, product.getProductName());
                    preparedStatement.setString(2, product.getProductCode());
                    preparedStatement.setString(3, product.getProductType());
                    preparedStatement.setDouble(4, (product.getSellingRate() == null ? 0 : product.getSellingRate()));
                    preparedStatement.setDouble(5, (product.getCostRate() == null ? 0 : product.getCostRate()));
                    preparedStatement.setDate(6, new Date((new java.util.Date()).getTime()));
                    preparedStatement.setDate(7, new Date((new java.util.Date()).getTime()));
                }

                @Override
                public int getBatchSize() {
                    return products.size();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
