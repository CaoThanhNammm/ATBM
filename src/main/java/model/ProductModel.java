package model;

public class ProductModel {
	private int id;
	private Product product;
	private String optionValue;
	private Status status;

	/**
	 * 
	 */
	public ProductModel() {
		super();
	}

	/**
	 * @param id
	 */
	public ProductModel(int id) {
		super();
		this.id = id;
	}

	public ProductModel(int id, Product product, String optionValue, Status status) {
		this.id = id;
		this.product = product;
		this.optionValue = optionValue;
		this.status = status;
	}

	@Override
	public String toString() {
		return "ProductModel [id=" + id + ", product=" + product + ", optionValue=" + optionValue + ", status=" + status
				+ "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
