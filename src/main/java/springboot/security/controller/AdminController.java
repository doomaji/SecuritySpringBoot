package springboot.security.controller;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springboot.security.model.User;
import springboot.security.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    private void addRoles(Model model) {
        model.addAttribute("roles", userService.getAllRoles());
    }

    @GetMapping
    public String adminHome() {
        return "admin/home";
    }

    @GetMapping("/users")
    public String list(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        addRoles(model);
        return "admin/user-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("user", userService.getUser(id));
        addRoles(model);
        return "admin/user-form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult,
                           Model model) {

        if (user.getId() == null && userService.usernameExists(user.getUsername())) {
            bindingResult.rejectValue("username", "duplicate", "Такой username уже занят");
        }

        if (bindingResult.hasErrors()) {
            addRoles(model);
            return "admin/user-form";
        }

        try {
            userService.saveUser(user);
            return "redirect:/admin/users";
        } catch (DataIntegrityViolationException ex) {
            bindingResult.rejectValue("username", "duplicate", "Такой username уже занят");
            addRoles(model);
            return "admin/user-form";
        }
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}