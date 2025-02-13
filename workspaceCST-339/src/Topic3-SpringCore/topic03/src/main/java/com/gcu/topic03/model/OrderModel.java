package com.gcu.topic03.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "order")
@XmlType(propOrder = { "id", "orderNo", "productName", "price", "quantity" }) // Include "id" in propOrder
public class OrderModel {

	private Long id;
	private String orderNo;
	private String productName;
	private float price;
	private int quantity;

	// Default constructor required for JAXB
	public OrderModel() {
	}

	public OrderModel(Long id, String orderNo, String productName, float price, int quantity) {
		this.id = id;
		this.orderNo = orderNo;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

	@Override
	public String toString() {
		return "OrderModel [id=" + id + ", orderNo=" + orderNo + ", productName=" + productName + ", price=" + price
				+ ", quantity=" + quantity + "]";
	}

}
