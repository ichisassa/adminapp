package com.example.adminapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 管理ダッシュボード系画面を表示するコントローラ。
 * 各機能ページのモデルセットと共通レイアウトの切り替えを担う。
 */
@Controller
public class AdminController {

  /**
   * ダッシュボード画面を表示する。
   * @param model ビューに渡すモデル
   * @return      レイアウトテンプレート
   */
  @GetMapping({"/", "/dashboard"})
  public String dashboard(Model model) {
    return render(model, "Dashboard", "dashboard", "dashboard");
  }

  /**
   * プロフィール画面を表示する。
   * @param model ビューに渡すモデル
   * @return      レイアウトテンプレート
   */
  @GetMapping("/profile")
  public String profile(Model model) {
    return render(model, "Profile", "profile", "profile");
  }

  /**
   * 設定画面を表示する。
   * @param model ビューに渡すモデル
   * @return      レイアウトテンプレート
   */
  @GetMapping("/settings")
  public String settings(Model model) {
    return render(model, "Settings", "settings", "settings");
  }

  /**
   * レポート画面を表示する。
   * @param model ビューに渡すモデル
   * @return      レイアウトテンプレート
   */
  @GetMapping("/reports")
  public String reports(Model model) {
    return render(model, "Reports", "reports", "reports");
  }

  /**
   * 共通レイアウトに必要な属性を設定し、layout テンプレートを返す。
   *
   * @param model モデル
   * @param pageTitle 画面タイトル
   * @param contentTemplate コンテンツフラグメント名
   * @param activePage サイドバーのアクティブ項目
   * @return レイアウトテンプレート名
   */
  private String render(Model model, String pageTitle, String contentTemplate, String activePage) {
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("contentTemplate", "contents/" + contentTemplate);
    model.addAttribute("activePage", activePage);
    return "layout";
  }
}
