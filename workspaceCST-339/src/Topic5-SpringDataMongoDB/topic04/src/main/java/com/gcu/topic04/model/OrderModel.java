package com.gcu.topic04.model;

public class OrderModel {

	private String id;
	private String orderNo;
	private String productName;
	private float price;
	private int quantity;

	public OrderModel(String id, String productName, String orderNo, float price, int quantity) {
		super();
		this.id = id;
		this.productName = productName;
		this.orderNo = orderNo;
		this.price = price;
		this.quantity = quantity;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	// default
	public OrderModel() {
	}

	@Override
	public String toString() {
		return "OrderModel [id=" + id + ", orderNo=" + orderNo + ", productName=" + productName + ", price="
				+ price + ", quantity=" + quantity + "]";
	}
}


