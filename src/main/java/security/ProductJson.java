package security;

// Lớp con chứa thông tin sản phẩm (dùng cho JSON)
public class ProductJson {
    private int productId;
    private String productName;
    private int price;
    private int discount;
    private int priceAfterDiscount;
    private String option;
    private int quantity;
	public ProductJson(int productId, String productName, int price, int discount, int priceAfterDiscount,
			String option, int quantity) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.price = price;
		this.discount = discount;
		this.priceAfterDiscount = priceAfterDiscount;
		this.option = option;
		this.quantity = quantity;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getDiscount() {
		return discount;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public int getPriceAfterDiscount() {
		return priceAfterDiscount;
	}
	public void setPriceAfterDiscount(int priceAfterDiscount) {
		this.priceAfterDiscount = priceAfterDiscount;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

    
}

