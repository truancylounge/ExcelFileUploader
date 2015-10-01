import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Lalith on 9/29/15.
 */
public class Recipe {
    private Integer id;
    private String recipeName;
    private Double yield;
    private String units;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private List<RecipeIngredients> recipeIngredientsList;

    public Recipe() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Double getYield() {
        return yield;
    }

    public void setYield(Double yield) {
        this.yield = yield;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    public List<RecipeIngredients> getRecipeIngredientsList() {
        return recipeIngredientsList;
    }

    public void setRecipeIngredientsList(List<RecipeIngredients> recipeIngredientsList) {
        this.recipeIngredientsList = recipeIngredientsList;
    }
}
