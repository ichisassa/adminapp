/**
 * MailSend 画面 Phase1 UI コントローラ。
 * 第二段階で送信処理/AJAX を差し込む予定の空実装。
 */
class MailSendPage {
  private sendButton: HTMLButtonElement | null = null;

  init(): void {
    this.cacheElements();
    this.registerEvents();
  }

  private cacheElements(): void {
    this.sendButton = document.getElementById('btnSendMail') as HTMLButtonElement | null;
  }

  private registerEvents(): void {
    if (!this.sendButton) {
      return;
    }
    this.sendButton.addEventListener('click', (event) => this.handleSendClick(event));
  }

  private handleSendClick(event: Event): void {
    event.preventDefault();
    // TODO: 第二段階で AJAX 経由の送信処理を実装する
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const page = new MailSendPage();
  page.init();
});
