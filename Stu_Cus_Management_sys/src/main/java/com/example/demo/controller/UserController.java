package com.example.demo.controller;







import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.entity.User;
import com.example.demo.entity.Student;
import com.example.demo.excel.ExcelGeneratorUser;
import com.example.demo.service.UserServic;
import com.example.demo.service.EmailSenderService;
import com.example.demo.service.StudentService;



@Controller
public class UserController {
	
	private UserServic userServic;
	
	public UserController(UserServic userServic) {
		super();
		this.userServic = userServic;
	}
	 @GetMapping("/export_users")
	    public String exportIntoExcelFile(HttpServletResponse response) throws IOException {
	        response.setContentType("application/octet-stream");
	        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	        String currentDateTime = dateFormatter.format(new Date());

	        String headerKey = "Content-Disposition";
	        String headerValue = "attachment; filename=user" + currentDateTime + ".xlsx";
	        response.setHeader(headerKey, headerValue);

	        List <User> listOfStudents = userServic.getAllUsers();
	        ExcelGeneratorUser generator = new ExcelGeneratorUser(listOfStudents);
	        generator.generateExcelFile(response);
	        return "redirect:/users";	
	    }
	@GetMapping("/users")
	public String listUsers(Model model) {
	
		model.addAttribute("users", userServic.getAllUsers());
		return "users";
	}
	
	@GetMapping("/users/new")
	public String createUserForm(Model model) {
	
		User user = new User();
		user.setMailStatus(false);
		model.addAttribute("user", user);
		return "create_user";
		
	}
	
	@PostMapping("/users")
	public String saveUser(@ModelAttribute("user") User user) {
		user.setCreatedDate(new Date());
	    userServic.saveUser(user);
		
		return "redirect:/users";
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUserForm(@PathVariable Long id, Model model) {
		model.addAttribute("user", userServic.getUserById(id));
		return "edit_user";
	}

	@PostMapping("/users/{id}")
	public String updateUser(@PathVariable Long id,
			@ModelAttribute("user") User user,
			Model model) {
		
		// get student from database by id
		User existingUser = userServic.getUserById(id);
		existingUser.setUserId(id);
		existingUser.setName(user.getName());
		existingUser.setPhoneNo(user.getPhoneNo());
		existingUser.setEmail(user.getEmail());
		// save updated student object
		userServic.updateUser(existingUser);
		return "redirect:/users";		
	}
	
	// handler method to handle delete student request
	
	@GetMapping("/users/{id}")
	public String deleteUser(@PathVariable Long id) {
		userServic.deleteUserById(id);
		return "redirect:/users";
	}
	
}
