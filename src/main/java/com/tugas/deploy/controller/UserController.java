package com.tugas.deploy.controller;

import com.tugas.deploy.model.User;
import com.tugas.deploy.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

@Controller
public class UserController {

    // Kredensial statis sesuai permintaan
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "20230140233";

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {
            session.setAttribute("username", username);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid username or password!");
            return "login";
        }
    }

    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }

        // Mengambil data dari service
        List<User> userList = userService.getAllUsers();

        model.addAttribute("username", session.getAttribute("username"));

        // 👇 UBAH BAGIAN INI: Ganti "userList" menjadi "users" 👇
        model.addAttribute("users", userList);

        return "home";
    }

    @GetMapping("/form")
    public String showFormPage(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }

        // 👇 PERBAIKAN 1: Mengirimkan objek user kosong agar Thymeleaf tidak Error 500
        model.addAttribute("user", new User());

        model.addAttribute("username", session.getAttribute("username"));
        return "form";
    }

    @PostMapping("/submit")
    public String submitForm(
            @ModelAttribute User user,
            HttpSession session) {

        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }

        // 1. Simpan data form ke database
        userService.addUser(user);

        // 2. Redirect langsung ke endpoint /home agar data otomatis ter-refresh!
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, SessionStatus status) {
        session.invalidate();
        status.setComplete();
        return "redirect:/login";
    }
}