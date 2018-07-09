package hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import hello.model.User;
import hello.repository.UserRepository;

@Controller    // This means that this class is a Controller
public class UserPageController {
	   private UserRepository userRepository;

	    @Autowired
	    public void setProductRepository(UserRepository userRepository) {
	        this.userRepository = userRepository;
	    }

	    @RequestMapping(path = "/")
	    public String index() {
	        return "index";
	    }

	    @RequestMapping(path = "/users/add", method = RequestMethod.GET)
	    public String createProduct(Model model) {
	        model.addAttribute("user", new User());
	        return "edit";
	    }

	    @RequestMapping(path = "users", method = RequestMethod.POST)
	    public String saveProduct(User product) {
	    		userRepository.save(product);
	        return "redirect:/";
	    }

	    @RequestMapping(path = "/users", method = RequestMethod.GET)
	    public String getAllProducts(Model model) {
	        model.addAttribute("users", userRepository.findAll());
	        return "users";
	    }

	    @RequestMapping(path = "/users/edit/{id}", method = RequestMethod.GET)
	    public String editProduct(Model model, @PathVariable(value = "id") Integer id) {
	        model.addAttribute("user", userRepository.findOne(id));
	        return "edit";
	    }

	    @RequestMapping(path = "/users/delete/{id}", method = RequestMethod.GET)
	    public String deleteProduct(@PathVariable(name = "id") Integer id) {
	        userRepository.delete(id);
	        return "redirect:/users";
	    }
}
