package cst339.controller;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;

//import cst339.business.OrdersBusinessServiceInterface;
//import cst339.business.SecurityBusinessService;
//import cst339.model.LoginModel;
//import jakarta.validation.Valid;

@Controller
public class LoginController {

//	@Autowired
//	private OrdersBusinessServiceInterface ordersBusinessServiceInterface;
//
//	@Autowired
//	private SecurityBusinessService securityBusinessService;

	/**
	 * Simple Hello World Controller that returns a View Name along with a Model
	 * Attribute named message. Invoke using /test2 URI.
	 * 
	 * @param model Model to bind to the View.
	 * 
	 * @return View name hello
	 */
	@GetMapping("/login")
	public String display(Model model) {
		
		System.out.println(">>>>> LoginController.display");

		// Display Login Form View
		model.addAttribute("title", "Login Form");
		return "login";
	}

	// Bind to the LoginModel Bean
	// @Valid - looks at the Bean for Size validation
	/*
	@PostMapping("/doLogin")
	public String doLogin(@Valid LoginModel loginModel, BindingResult bindingResult, Model model) {

		// Calls test method interface - implementation in OrdersBusinessService
		ordersBusinessServiceInterface.test();

		// returns List<OrderModel>
		var orders = ordersBusinessServiceInterface.getOrders();

		// Calls authenticate in Security Business Service
		boolean authenticated = securityBusinessService.authenticate("cv64", "password");

		if (!authenticated)
			return null;

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
	*/

}
