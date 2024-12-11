package model;

import java.time.LocalDate;
import java.util.List;

public class Order {
	private int id;
	private Account account;
	private LocalDate dateCreated;
	private LocalDate lastUpdated;
	private String phone;
	private String address;
	private Status status;
	private int totalPrice;
	private List<OrderDetail> details;
	private String hash;   // Trường mới
    private String sign; // Trường mới
    private String publicKey;  // Cột mới


	/**
	 * 
	 */
	public Order() {
		super();
	}
	// Constructor với các tham số (bao gồm hash và sign)
    public Order(int id, Account account, LocalDate dateCreated, LocalDate lastUpdated, String phone, String address, Status status,
    		List<OrderDetail> details, String hash, String sign,String publicKey) {
        this.id = id;
        this.account = account;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.details = details;
        this.hash = hash;
        this.sign = sign;
        this.publicKey = publicKey; 
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

	public LocalDate getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDate lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
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
	//
	public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
