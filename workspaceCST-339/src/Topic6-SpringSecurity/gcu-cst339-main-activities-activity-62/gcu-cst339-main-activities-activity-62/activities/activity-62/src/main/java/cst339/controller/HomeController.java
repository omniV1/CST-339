package cst339.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home(Model model) {
		
		System.out.println(">>>>> HomeController.home");
		
		model.addAttribute("title", "My Home");
		return "home";
	}
}
