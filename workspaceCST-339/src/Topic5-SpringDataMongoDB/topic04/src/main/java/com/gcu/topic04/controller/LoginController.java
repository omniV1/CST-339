package com.gcu.topic04.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gcu.topic04.business.OrdersBusinessServiceInterface;
import com.gcu.topic04.business.SecurityBusinessService;
import com.gcu.topic04.model.LoginModel;

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
    // Check for validation errors
    if (bindingResult.hasErrors()) {
        model.addAttribute("title", "Login Form");
        return "login";
    }

    // Authenticate (use form values - for testing you can keep hardcoded values)
    boolean authenticated = securityBusinessService.authenticate("cv64", "password");

    if (!authenticated) {
        model.addAttribute("title", "Login Form");
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    // Get orders after successful authentication
    ordersBusinessServiceInterface.test();
    var orders = ordersBusinessServiceInterface.getOrders();

    // Set up model for orders page
    model.addAttribute("title", "My Orders");
    model.addAttribute("orders", orders);
    
    return "orders";  // Return the view name - must match an HTML file in templates folder
}

}
