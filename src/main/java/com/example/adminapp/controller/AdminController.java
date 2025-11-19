package com.example.adminapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

  @GetMapping({"/", "/dashboard"})
  public String dashboard(Model model) {
    return render(model, "Dashboard", "dashboard", "dashboard");
  }

  @GetMapping("/profile")
  public String profile(Model model) {
    return render(model, "Profile", "profile", "profile");
  }

  @GetMapping("/settings")
  public String settings(Model model) {
    return render(model, "Settings", "settings", "settings");
  }

  @GetMapping("/reports")
  public String reports(Model model) {
    return render(model, "Reports", "reports", "reports");
  }

  private String render(Model model, String pageTitle, String contentTemplate, String activePage) {
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("contentTemplate", "contents/" + contentTemplate);
    model.addAttribute("activePage", activePage);
    return "layout";
  }
}
