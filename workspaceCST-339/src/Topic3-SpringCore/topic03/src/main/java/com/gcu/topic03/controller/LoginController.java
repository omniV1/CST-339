package com.gcu.topic03.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gcu.topic03.business.OrdersBusinessServiceInterface;
import com.gcu.topic03.business.SecurityBusinessService;
import com.gcu.topic03.model.LoginModel;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	private OrdersBusinessServiceInterface ordersBusinessServiceInterface;
	
	@Autowired
	private SecurityBusinessService securityBusinessService;

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

		// Calls test method interface - implementation in OrdersBusinessService
		ordersBusinessServiceInterface.test();
		
		// returns List<OrderModel>
		var orders = ordersBusinessServiceInterface.getOrders();
		
		// Calls authenticate in Security Business Service
		securityBusinessService.authenticate("cv64", "password");
		
		
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
