package ru.pon.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import ru.pon.demo.entity.User;
import ru.pon.demo.model.UserDTO;
import ru.pon.demo.services.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping(value = "/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDTO userDto = new UserDTO();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @PostMapping(value = "/registration")
    public String registerUser(
            @ModelAttribute("user") @Valid UserDTO userDTO,
            BindingResult bindingResult,
            Model model) {
        User registeredUser = null;
        if (!bindingResult.hasErrors()) {
            registeredUser = userService.registerUser(userDTO);
        }

        if (registeredUser != null) {
            return "redirect:/login";
        }

        model.addAttribute("matchingPassword", userDTO.getPassword().equals(userDTO.getMatchingPassword()));

        return "registration";
    }
}
