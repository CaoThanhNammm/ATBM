package security;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

// Lớp con chứa thông tin order (dùng cho JSON)
public class OrderJson {
    private String userId;
    private String fullName;
    private List<ProductJson> products;
    private String dateCreated;
    private int total;
    
    
	public OrderJson(String userId, String fullName, List<ProductJson> products, String dateCreated, int total) {
		super();
		this.userId = userId;
		this.fullName = fullName;
		this.products = products;
		this.dateCreated = dateCreated;
		this.total = total;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public List<ProductJson> getProducts() {
		return products;
	}
	public void setProducts(List<ProductJson> products) {
		this.products = products;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	

    
    
    
}


