import java.sql.Timestamp;

/**
 * Created by Lalith on 9/23/15.
 */
public class Product {
    private Integer id;
    private String productCode;
    private String productName;
    private String productType;
    private Double sellingRate;
    private Double costRate;
    private Timestamp createdDate;
    private Timestamp updatedDate;

    public Product(Integer id, String productCode, String productName, String productType, Double sellingRate, Double costRate) {
        this.productCode = productCode;
        this.productName = productName;
        this.productType = productType;
        this.sellingRate = sellingRate;
        this.costRate = costRate;
    }

    public Product() {

    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Double getSellingRate() {
        return sellingRate;
    }

    public void setSellingRate(Double sellingRate) {
        this.sellingRate = sellingRate;
    }

    public Double getCostRate() {
        return costRate;
    }

    public void setCostRate(Double costRate) {
        this.costRate = costRate;
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
}
