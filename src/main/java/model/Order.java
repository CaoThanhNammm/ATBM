package model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Order {
	private int id;
	private Account account;
	private LocalDate dateCreated;
	private LocalDate lastUpdated;
	private String phone;
	private String address;
	private Status status;
	private int totalPrice;
	private String hash;
	private String sign;
	private List<OrderDetail> details;
	private String publicKey;

	/**
	 * 
	 */
	public Order() {
		super();
	}

	public Order(int id, Account account, LocalDate dateCreated, LocalDate lastUpdated, String phone, String address,
			Status status, List<OrderDetail> details) {
		super();
		this.id = id;
		this.account = account;
		this.dateCreated = dateCreated;
		this.lastUpdated = lastUpdated;
		this.phone = phone;
		this.address = address;
		this.status = status;
		this.details = details;
	}

	public JsonObject createJson() {
		int total = 0;

		JsonArray jsonProducts = new JsonArray();

		Collections.sort(details);
		for (OrderDetail o : details) {
			total += (o.getPrice() - o.getDiscount()) * o.getQuantity();

			JsonObject jsonProduct = new JsonObject();
			jsonProduct.addProperty("productId", o.getModel().getProduct().getId());
			jsonProduct.addProperty("price", o.getPrice());
			jsonProduct.addProperty("discount", o.getDiscount());
			jsonProduct.addProperty("priceAfterdiscount", o.getPrice() - o.getDiscount());
			jsonProduct.addProperty("option", o.getModel().getOptionValue());
			jsonProduct.addProperty("quantity", o.getQuantity());

			jsonProducts.add(jsonProduct);
		}

		JsonObject jsonResponse = new JsonObject();
		jsonResponse.addProperty("userId", account.getId());
		jsonResponse.add("products", jsonProducts);
		jsonResponse.addProperty("dateCreated", dateCreated + "");
		jsonResponse.addProperty("total", total);
		return jsonResponse;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public LocalDate getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDate dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getHash() {
		return hash;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public LocalDate getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDate lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getPhone() {
		return phone;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", account=" + account + ", dateCreated=" + dateCreated + ", lastUpdated="
				+ lastUpdated + ", phone=" + phone + ", address=" + address + ", status=" + status + ", totalPrice="
				+ totalPrice + ", hash=" + hash + ", sign=" + sign + ", details=" + details + "]";
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<OrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetail> details) {
		this.details = details;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

}
