package com.gcu.topic2_1.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import model.LoginModel;
import model.OrderModel;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

	/**
	 * Simple Hello World Controller that returns a View Name along with a Model
	 * Attribute named message. Invoke using /test2 URI.
	 * 
	 * @param model Model to bind to the View.
	 * 
	 * @return View name hello
	 */
	@GetMapping("/")
	public String display(Model model) {

		model.addAttribute("title", "Login Form");
		model.addAttribute("loginModel", new LoginModel());
		return "login";
	}

	// Bind to the LoginModel Bean
	// @Valid - looks at the Bean for Size validation
	@PostMapping("/doLogin")
	public String doLogin(@Valid LoginModel loginModel, BindingResult bindingResult, Model model) {

		List<OrderModel> orders = new ArrayList<OrderModel>();
		orders.add(new OrderModel(0L, "0000000000", "Product 0", 0.00f, 0));
		orders.add(new OrderModel(1L, "0000000001", "Product 1", 1.00f, 1));
		orders.add(new OrderModel(2L, "0000000002", "Product 2", 2.00f, 2));
		orders.add(new OrderModel(3L, "0000000003", "Product 3", 3.00f, 3));
		orders.add(new OrderModel(4L, "0000000004", "Product 4", 4.00f, 4));

		// Print the form values out
		System.out.println(String.format("Form with Username of %s and Password of %s", loginModel.getUsername(),
				loginModel.getPassword()));

		model.addAttribute("title", "My Orders");

		if (bindingResult.hasErrors()) {
			return "login";
		}

		model.addAttribute("orders", orders);
		return "orders";
	}

}
