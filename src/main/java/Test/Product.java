package Test;

import java.time.LocalDate;



public class Product {
	private int id;
	private String name;
	private String description;
	private int price;
	private int discount;
	private LocalDate lastUpdated;
	private int amountSold;
	private String imgs;
	public Product(int id, String name, String description, int price, int discount, LocalDate lastUpdated,
			int amountSold, String imgs) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.discount = discount;
		this.lastUpdated = lastUpdated;
		this.amountSold = amountSold;
		this.imgs = imgs;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public LocalDate getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(LocalDate lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public int getAmountSold() {
		return amountSold;
	}
	public void setAmountSold(int amountSold) {
		this.amountSold = amountSold;
	}
	public String getImgs() {
		return imgs;
	}
	public void setImgs(String imgs) {
		this.imgs = imgs;
	}
}

