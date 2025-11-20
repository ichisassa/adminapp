package com.example.adminapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * メール機能の画面遷移を提供するコントローラ。
 * メール一覧と送信画面の表示を担当する。
 */
@Controller
@RequestMapping("/admin/mail")
public class MailController {

  /**
   * メール送信ログ一覧画面を表示する。
   * @param model ビューに渡すモデル
   * @return      レイアウトテンプレート
   */
  @GetMapping("/list")
  public String list(Model model) {
    return render(model, "メール一覧", "contents/mail-list", "mail-list");
  }

  /**
   * メール送信画面を表示する。
   * @param model ビューに渡すモデル
   * @return      レイアウトテンプレート
   */
  @GetMapping("/send")
  public String send(Model model) {
    return render(model, "メール送信", "contents/mail-send", "mail-send");
  }

  /**
   * 共通レイアウト設定を行い、mail レイアウトを返す。
   *
   * @param model モデル
   * @param pageTitle ページタイトル
   * @param contentTemplate 表示する Thymeleaf テンプレート
   * @param activePage サイドバーのアクティブ項目
   * @return レイアウトテンプレート名
   */
  private String render(Model model, String pageTitle, String contentTemplate, String activePage) {
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("contentTemplate", contentTemplate);
    model.addAttribute("activePage", activePage);
    return "layout";
  }
}
