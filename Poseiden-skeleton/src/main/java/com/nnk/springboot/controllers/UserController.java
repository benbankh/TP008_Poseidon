package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @RolesAllowed({"USER","ADMIN"})
    @RequestMapping("/user/list")   // sans spécifier l'attribut method => mappée à toutes les méthodes HTTP par défaut. Cela inclut les requêtes GET, POST, PUT, DELETE, etc.
    public String home(Model model)
    {
        model.addAttribute("users", userRepository.findAll());
        return "user/list";
    }

    @GetMapping("/user/add")
    public String addUser(User bid) {
        return "user/add";
    }


    @PostMapping("/user/validate")
    public String validate(@Valid User user, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
            user.setPassword(user.getPassword());
            userRepository.save(user);

            //model.addAttribute("users", userRepository.findAll());
            //return "redirect:/user/list";// => va appeler le controler, donc la ligne au dessus ne sert à rien

            //autre façon de faire => pas de redirect, on doit alors faire le findAll
            model.addAttribute("users", userRepository.findAll());
            return "user/list";

            //return "/user/list"; //si on fait ça on arrive ici "user/validate" / j'ai l'impression que le redirect est appelé si on change de page..
        }
        return "user/add"; //..là on resterait sur la même page
    }



    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setPassword("");
        model.addAttribute("user", user);
        return "user/update";
    }

    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/update";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setPassword(user.getPassword());
        user.setId(id);
        userRepository.save(user);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/user/list";
    }


    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/user/list";
    }
}
