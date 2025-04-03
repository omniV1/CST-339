package cst339.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * <orders> <order> <id>1</id> <orderNo>0000000000</orderNo> <price>1.0</price>
 * <productName>This is Product 1</productName> <quantity>1</quantity> </order>
 * </orders>
 */

// Annotation that creates the XML element wrapper for orders, e.g. <orders>...</orders>
@XmlRootElement(name = "orders")
public class OrderList {

	private List<OrderModel> orders = new ArrayList<OrderModel>();

	// Annotation that creates the XML element wrapper for each order, e.g.
	// <order>...</order>
	@XmlElement(name = "order")
	public List<OrderModel> getOrders() {
		
		System.out.println(">>>>> OrderList.getOrders");
		return this.orders;
	}

	public void setOrders(List<OrderModel> orders) {
		
		System.out.println(">>>>> OrderList.setOrders");
		this.orders = orders;
	}

}
