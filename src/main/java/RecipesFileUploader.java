
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.io.FileInputStream;
import java.sql.Date;
import java.util.*;

/**
 * Created by Lalith on 9/29/15.
 */
public class RecipesFileUploader {

    private static final String FILE_PATH = "/Users/Lalith/Documents/InventoryManagementSystem/DataFeeds/recipe_latest_update_july15.xlsx";

    public static void main(String[] args) {
        List<Recipe> recipeList = readRecipesFile(FILE_PATH);
        persistRecipeList(recipeList);
    }

    private static List<Recipe> readRecipesFile(String filePath) {

        List<Recipe> recipeList = new ArrayList<Recipe>();

        try{
            FileInputStream file = new FileInputStream(FILE_PATH);

            // Use XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new XSSFWorkbook(file);
            int numberOfSheets = workbook.getNumberOfSheets();
            System.out.println("Number of sheets in the workbook : " + numberOfSheets);



            Sheet cookiesSheet = workbook.getSheetAt(8);
            // Run through the cookie sheet and get all cookie names
            List<String> cookieNames = new ArrayList<String>();
            for (Row myRow : cookiesSheet) {
                if(myRow.getCell(0) != null && myRow.getCell(1).getStringCellValue().equals("")) {
                    if(!myRow.getCell(0).getStringCellValue().equals("")) {
                        System.out.println(myRow.getCell(0));
                        cookieNames.add(myRow.getCell(0).getStringCellValue());
                    }
                }
            }

            int i = 0; // counter used to retrieve recipe name from the list
            for (Row myRow : cookiesSheet) {
                if(myRow.getCell(1).getStringCellValue().equals("YEILD")) {
                    Recipe recipe = new Recipe();
                    recipe.setRecipeName(cookieNames.get(i));
                    recipe.setYield(myRow.getCell(2).getNumericCellValue());
                    recipe.setUnits(myRow.getCell(3).getStringCellValue());
                    recipeList.add(recipe);
                    System.out.println("Yeild: " + myRow.getCell(2) + " units : " + myRow.getCell(3) );
                    i++;
                }
            }

            List<RecipeIngredients> recipeIngredientsList = new ArrayList<RecipeIngredients>();
            for(Row myRow : cookiesSheet) {
                Boolean recipeIngredientStart = Boolean.FALSE;
                Boolean recipeIngredientNumeric = Boolean.FALSE;
                if(myRow.getCell(0).getCellType() == Cell.CELL_TYPE_STRING &&
                        myRow.getCell(0).getStringCellValue().equals("S.No")) {
                    recipeIngredientStart = Boolean.TRUE;
                }

                if(myRow.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    recipeIngredientNumeric = Boolean.TRUE;
                }

                if(recipeIngredientStart && !(myRow.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC)) {
                    recipeIngredientStart = Boolean.FALSE;
                    recipeIngredientNumeric = Boolean.FALSE;
                }

                if(recipeIngredientStart || recipeIngredientNumeric) {
                    RecipeIngredients recipeIngredients = new RecipeIngredients();
                    for (Cell myCell : myRow) {
                        switch (myCell.getColumnIndex()) {
                            case 0:
                                recipeIngredients.setSerialNumber((int) myCell.getNumericCellValue());
                                break;
                            case 1:
                                recipeIngredients.setProductName(myCell.getStringCellValue().trim());
                                break;
                            case 2:
                                recipeIngredients.setQuantity(myCell.getNumericCellValue());
                                break;
                            case 3:
                                recipeIngredients.setUnits(myCell.getStringCellValue().trim());
                                break;
                            case 4:
                                recipeIngredients.setRate(myCell.getNumericCellValue());
                                break;
                            case 5:
                                recipeIngredients.setAmount(myCell.getNumericCellValue());
                                break;
                        }
                    }
                    recipeIngredientsList.add(recipeIngredients);
                }
            }
            System.out.println(recipeIngredientsList);

            List<Integer> splitIndexes = new ArrayList<Integer>();
            for(RecipeIngredients recipeIngredients : recipeIngredientsList) {
                if(recipeIngredients.getSerialNumber() == 1) {
                    splitIndexes.add(recipeIngredientsList.indexOf(recipeIngredients));
                }
            }
            splitIndexes.add(recipeIngredientsList.size());
            System.out.println(splitIndexes);

            int startCounter = 0;
            int endCounter = 1;
            for(Recipe recipe : recipeList) {

                recipe.setRecipeIngredientsList(recipeIngredientsList.subList(splitIndexes.get(startCounter), splitIndexes.get(endCounter)));
                startCounter++;
                endCounter++;
            }



        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return recipeList ;
    }

    private static void persistRecipeList(final List<Recipe> recipeList) {
        try {
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriver(new com.mysql.jdbc.Driver());
            dataSource.setUrl("jdbc:mysql://localhost/inventory");
            dataSource.setUsername("root");
            //dataSource.setPassword("P@ssw0rd");
            //JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            //String sql = "insert into recipes (recipeName, yield, units, createdDate, updatedDate) values ( ?, ?, ?, ?, ?)";


            for(Recipe recipe : recipeList) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("recipeName", recipe.getRecipeName());
                params.put("yield", recipe.getYield());
                params.put("units", recipe.getUnits());
                params.put("createdDate", new Date((new java.util.Date()).getTime()));
                params.put("updatedDate", new Date((new java.util.Date()).getTime()));

                SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("recipes")
                        .usingGeneratedKeyColumns("id");
                long id = insert.executeAndReturnKey(params).longValue();
                System.out.println("Generated id - " + id);

                for(RecipeIngredients recipeIngredients : recipe.getRecipeIngredientsList()) {

                    Map<String, Object> ingredientsParams = new HashMap<String, Object>();
                    ingredientsParams.put("productName", recipeIngredients.getProductName());
                    ingredientsParams.put("quantity", recipeIngredients.getQuantity());
                    ingredientsParams.put("units", recipeIngredients.getUnits());
                    ingredientsParams.put("rate", recipeIngredients.getRate());
                    ingredientsParams.put("amount", recipeIngredients.getAmount());
                    ingredientsParams.put("createdDate", new Date((new java.util.Date()).getTime()));
                    ingredientsParams.put("updatedDate", new Date((new java.util.Date()).getTime()));
                    ingredientsParams.put("recipesId", id);

                    SimpleJdbcInsert ingredientsInsert = new SimpleJdbcInsert(dataSource).withTableName("recipeIngredients");
                    ingredientsInsert.execute(ingredientsParams);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
