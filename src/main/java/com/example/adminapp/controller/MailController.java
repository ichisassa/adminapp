package com.example.adminapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/mail")
public class MailController {

  @GetMapping("/list")
  public String list(Model model) {
    return render(model, "メール一覧", "contents/mail-list", "mail-list");
  }

  @GetMapping("/send")
  public String send(Model model) {
    return render(model, "メール送信", "contents/mail-send", "mail-send");
  }

  private String render(Model model, String pageTitle, String contentTemplate, String activePage) {
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("contentTemplate", contentTemplate);
    model.addAttribute("activePage", activePage);
    return "layout";
  }
}
