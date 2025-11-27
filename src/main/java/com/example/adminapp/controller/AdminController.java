package com.example.adminapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AdminController Class
 */
@Controller
public class AdminController {

  /**
   * Dashboard画面表示処理
   * @param model Model
   * @return layout.html + dashboard.html
   */
  @GetMapping({"/", "/admin", "/admin/dashboard"})
  public String dashboard(Model model) {
    return render(model, "Dashboard", "dashboard", "dashboard");
  }

  /**
   * Profile画面表示処理
   * @param model Model
   * @return layout.html + profile.html
   */
  @GetMapping("/admin/profile")
  public String profile(Model model) {
    return render(model, "Profile", "profile", "profile");
  }

  /**
   * Settings画面表示処理
   * @param model Model
   * @return layout.html + settings.html
   */
  @GetMapping("/admin/settings")
  public String settings(Model model) {
    return render(model, "Settings", "settings", "settings");
  }

  /**
   * Reports画面表示処理
   * @param model Model
   * @return layout.html + reports.html
   */
  @GetMapping("/admin/reports")
  public String reports(Model model) {
    return render(model, "Reports", "reports", "reports");
  }

  /**
   * MailList画面表示処理
   * @param model Model
   * @return layout.html + mail-list.html
   */
  @GetMapping("/admin/mail/list")
  public String list(Model model) {
    return render(model, "MailList", "mail-list", "mail-list");
  }

  /**
   * MailList画面表示処理
   * @param model Model
   * @return layout.html + mail-send.html
   */
  @GetMapping("/admin/mail/send")
  public String send(Model model) {
    return render(model, "MailSend", "mail-send", "mail-send");
  }

  /**
   * 画面共通処理
   * @param model Model
   * @param String pageTitle, 
   * @param String contentTemplate, 
   * @param String activePage
   * @return layout.html
   */
  private String render(
    Model  model, 
    String pageTitle, 
    String contentTemplate, 
    String activePage) 
  {
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("contentTemplate", "contents/" + contentTemplate);
    model.addAttribute("activePage", activePage);
    return "layout";
  }
}
