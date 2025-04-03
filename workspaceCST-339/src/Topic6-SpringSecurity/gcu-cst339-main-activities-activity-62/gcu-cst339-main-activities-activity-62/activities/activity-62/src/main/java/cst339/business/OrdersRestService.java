package cst339.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cst339.model.OrderList;
import cst339.model.OrderModel;

@RestController

// Provides the URL path to the server, e.g. server:port/service
@RequestMapping("/service")
public class OrdersRestService {

	@Autowired
	private OrdersBusinessServiceInterface ordersBusinessServiceInterface;

	// Appends the service path with /getjson, e.g. server:port/service/getjson
	// Returns a JSON Payload response
	@GetMapping(path = "/getjson", produces = { MediaType.APPLICATION_JSON_VALUE })
	public List<OrderModel> getOrdersAsJson() {
		return ordersBusinessServiceInterface.getOrders();
	}

	// Appends the service path with /getxml, e.g. server:port/service/getxml
	// Returns an XML Payload response
	@GetMapping(path = "/getxml", produces = { MediaType.APPLICATION_XML_VALUE })
	public OrderList getOrdersAsXml() {
		OrderList orderList = new OrderList();
		orderList.setOrders(ordersBusinessServiceInterface.getOrders());
		return orderList;
	}

	@GetMapping(path = "/getorder/{id}")
	public ResponseEntity<?> getOrder(@PathVariable("id") String id) {

		OrderModel orderModel = null;
		System.out.println("OrdersRestService.getOrder - id: " + id);

		try {
			orderModel = ordersBusinessServiceInterface.getOrderById(id);

			if (orderModel == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				
			} else {

				System.out.println(orderModel.toString());
				return new ResponseEntity<>(orderModel, HttpStatus.OK);
			}
			
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
