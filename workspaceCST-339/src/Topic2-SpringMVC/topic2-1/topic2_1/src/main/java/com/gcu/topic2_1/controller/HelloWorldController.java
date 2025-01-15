package com.gcu.topic2_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/hello")
public class HelloWorldController {

	/**
	 * Simple Hello World Controller that returns a String in the response body.
	 * Invoke using /test1 URI.
	 * 
	 * @return Hello World test string
	 */
	@GetMapping("/test1")
	@ResponseBody
	public String printHello() {
		// Simply return a String in the response body (must use @ResponseBody
		// annotation)
		return "Hello World! - Owen";
	}

	/**
	 * Simple Hello World Controller that returns a View Name along with a Model
	 * Attribute named message. Invoke using /test2 URI.
	 * 
	 * @param model Model to bind to the View.
	 * 
	 * @return View name hello
	 */
	@GetMapping("/test2")
	public String printHello(Model model) {
		// Simply return a Model with an attribute named message and return a View named
		// hello using a passed in ModelMap
		model.addAttribute("message", "Hello Spring MVC Framework! - Owen");
		return "hello";
	}

	/**
	 * Simple Hello World Controller that returns a View Name along with a Model
	 * Attribute named message. Invoke using /test3 URI.
	 * 
	 * @return ModelAndView with both the Model and the View to render
	 */
	@GetMapping("/test3")
	public ModelAndView printHello1() {
		// Create a ModelAndView and then set an attribute named message and with a View
		// named hello
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", new String("Hello World from ModelAndView!"));
		modelAndView.addObject("message2", new String("Another Hello World from ModelAndView!"));
		modelAndView.setViewName("hello");
		return modelAndView;
	}

	/**
	 * Simple Hello World Controller that returns a View Name along with a Model
	 * Attribute named message. Invoke using /test4 URI.
	 * 
	 * @param message HTTP Parameter named message to add to the Model for
	 *                rendering.
	 * @param model   Model to bind to the View.
	 * 
	 * @return View name hello
	 */
	@GetMapping("/test4")
	public String printHello(@RequestParam("message") String message, Model model) {
		// Simply return a Model attribute named message and return a View named hello
		model.addAttribute("message", message);
		return "hello";
	}
}
