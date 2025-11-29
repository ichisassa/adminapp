/** フィールドキー */
type MailFieldKey = 
'toAddress'  | 
'replyTo'    | 
'ccAddress'  | 
'bccAddress' | 
'subject'    | 
'body'       | 
'isHtml';

/** 送信データ */
interface MailSendPayload {
  toAddress  : string;
  replyTo    : string;
  ccAddress  : string;
  bccAddress : string;
  subject    : string;
  body       : string;
  isHtml     : boolean;
}

/** 受信データ */
interface MailSendResponseBody {
  success?      : boolean;
  message?      : string;
  fieldErrors?  : Record<string, string>;
  globalErrors? : string[];
}

/**
 * メール送信管理クラス
 */
class MailSendPage {

  // 送信先
  private readonly endpoint = '/admin/api/mail/send';
  // フォーム
  private form       : HTMLFormElement   | null = null; 
  // ボタン
  private sendButton : HTMLButtonElement | null = null; 
  // メッセージ欄
  private messageArea: HTMLElement       | null = null;
  private messageBaseClass  = '';

    // 各入力項目(document.getElementById)
  private fields: Record<MailFieldKey, HTMLInputElement | HTMLTextAreaElement | null> = {
    toAddress  : null,
    replyTo    : null,    
    ccAddress  : null,
    bccAddress : null,
    subject    : null,
    body       : null,
    isHtml     : null,
  };

  // 各エラーメッセージ項目(document.getElementById)
  private fieldErrorAreas: Partial<Record<MailFieldKey, HTMLElement | null>> = {
    toAddress  : null,
    replyTo    : null,
    ccAddress  : null,
    bccAddress : null,
    subject    : null,
    body       : null,
    isHtml     : null,
  };

  /**
   * 初期化処理
   */
  init(): void {
    this.cacheElements();
    this.registerEvents();
  }

  /**
   * DOM取得処理
   */
  private cacheElements(): void {

    this.form             = document.getElementById('mailSendForm') as HTMLFormElement   | null;
    this.sendButton       = document.getElementById('btnSendMail')  as HTMLButtonElement | null;
    this.messageArea      = document.getElementById('sendMessage')  as HTMLElement       | null;
    this.messageBaseClass = this.messageArea ? this.messageArea.className : '';

    this.fields = {
      toAddress   : document.getElementById('toAddress')  as HTMLInputElement    | null,
      replyTo     : document.getElementById('toAddress')  as HTMLInputElement    | null,
      ccAddress   : document.getElementById('ccAddress')  as HTMLInputElement    | null,
      bccAddress  : document.getElementById('bccAddress') as HTMLInputElement    | null,
      subject     : document.getElementById('subject')    as HTMLInputElement    | null,
      body        : document.getElementById('body')       as HTMLTextAreaElement | null,
      isHtml      : document.getElementById('isHtml')     as HTMLInputElement    | null,
    };

    this.fieldErrorAreas = {
      toAddress  : document.getElementById('toAddressError'),
      replyTo    : document.getElementById('replyToError'),
      ccAddress  : document.getElementById('ccAddressError'),
      bccAddress : document.getElementById('bccAddressError'),
      subject    : document.getElementById('subjectError'),
      body       : document.getElementById('bodyError'),
      isHtml     : document.getElementById('isHtmlError'),
    };
  }

  /**
   * イベント登録処理
   */
  private registerEvents(): void {
    const handler = (event: Event) => this.handleSendClick(event);
  
    if (this.form) {
      // submit event
      this.form.addEventListener('submit', handler);
    }

    if (this.sendButton) {
      // button event
      this.sendButton.addEventListener('click', handler);
    }
  }

  /**
   * 送信トリガー処理
   */
  private handleSendClick(event: Event): void {
    event.preventDefault();
    void this.submitForm();
  }

  /**
   * 送信処理
   */
  private async submitForm(): Promise<void> {

    const payload = this.buildPayload();
    if (!payload) {
      return;
    }

    this.resetFeedback();
    this.toggleSending(true);
    this.showMessage('送信処理中です...', 'text-info');

    try {
      const formBody = this.buildFormBody(payload);
      const response = await fetch(this.endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
          'X-Requested-With': 'XMLHttpRequest',
        },
        credentials: 'same-origin',
        body: formBody,
      });

      let body: MailSendResponseBody = {};
      try {
        body = (await response.json()) as MailSendResponseBody;
      } catch {
        body = {};
      }

      const validatedFieldErrors =
        body.fieldErrors && typeof body.fieldErrors === 'object' ? body.fieldErrors : {};
      this.renderFieldErrors(validatedFieldErrors);
      const fallbackMessage = response.ok ? '送信しました。' : '送信に失敗しました。';
      const message = typeof body.message === 'string' ? body.message : fallbackMessage;

      if (response.ok && body.success) {
        this.showMessage(message, 'text-success');
        this.resetForm();
      } else {
        const composed = this.composeGlobalMessage(message, body.globalErrors);
        this.showMessage(composed, 'text-danger');
      }
    } catch {
      this.showMessage('通信エラーが発生しました。時間をおいて再度お試しください。', 'text-danger');
    } finally {
      this.toggleSending(false);
    }
  }

  /**
   * 送信データ作成処理
   */
  private buildPayload(): MailSendPayload | null {

    const { 
      toAddress , 
      replyTo   , 
      ccAddress , 
      bccAddress, 
      subject   , 
      body      , 
      isHtml } 
    = this.fields;

    if (!toAddress || !replyTo || !subject || !body) {
      return null;
    }

    return {
      toAddress  : this.getFieldValue(toAddress),
      replyTo    : this.getFieldValue(replyTo),
      ccAddress  : this.getFieldValue(ccAddress),
      bccAddress : this.getFieldValue(bccAddress),
      subject    : this.getFieldValue(subject),
      body       : this.getFieldValue(body),
      isHtml     : !!(isHtml && (isHtml as HTMLInputElement).checked),
    };
  }

  /**
   * URLエンコード化処理
   */
  private buildFormBody(payload: MailSendPayload): string {
  
    const params = new URLSearchParams();

    Object.entries(payload).forEach(([key, value]) => {
      if (typeof value === 'boolean') {
        params.append(key, value ? 'true' : 'false');
      } else if (value == null) {
        params.append(key, '');
      } else {
        params.append(key, value);
      }
    });

    return params.toString();

  }

  /**
   * 各項目値取得処理
   */
  private getFieldValue(element: HTMLInputElement | HTMLTextAreaElement | null): string {
    if (!element) {
      return '';
    }
    const value = element.value == null ? '' : element.value;
    return typeof value.trim === 'function' ? value.trim() : value;
  }

  /**
   * 送信中表示切替処理
   */
  private toggleSending(isSending: boolean): void {
    if (!this.sendButton) {
      return;
    }
    this.sendButton.disabled = isSending;
    this.sendButton.classList.toggle('disabled', isSending);
  }

  /**
   * フォーム初期化処理
   */
  private resetForm(): void {
    if (this.form) {
      this.form.reset();
    }
    this.clearFieldErrors();
  }

  /**
   * メッセージ表示処理
   */
  private showMessage(message: string, stateClass: string): void {
    if (!this.messageArea) {
      return;
    }
    let className = this.messageBaseClass;
    if (stateClass) {
      className = className ? `${className} ${stateClass}` : stateClass;
    }
    this.messageArea.className = className;
    this.messageArea.textContent = message;
  }

  /**
   * 表示リセット処理
   */
  private resetFeedback(): void {
    this.clearFieldErrors();
    this.showMessage('', '');
  }

  /**
   * 項目エラー消去処理
   */
  private clearFieldErrors(): void {
    Object.values(this.fieldErrorAreas).forEach((area) => {
      if (area) {
        area.textContent = '';
      }
    });
  }

  /**
   * 項目エラー反映処理
   */
  private renderFieldErrors(fieldErrors: Record<string, string>): void {
    this.clearFieldErrors();
    if (!fieldErrors) {
      return;
    }
    Object.entries(fieldErrors).forEach(([field, message]) => {
      const target = this.fieldErrorAreas[field as MailFieldKey];
      if (target) {
        target.textContent = typeof message === 'string' ? message : '';
      }
    });
  }

  /**
   * メッセージ合成処理
   */
  private composeGlobalMessage(message?: string, globalErrors?: string[]): string {
    const lines: string[] = [];
    if (message) {
      lines.push(message);
    }
    if (Array.isArray(globalErrors)) {
      globalErrors.forEach((error) => {
        if (typeof error === 'string') {
          const trimmed = error.trim();
          if (trimmed) {
            lines.push(trimmed);
          }
        }
      });
    }
    return lines.join('\n');
  }
}

/**
 * イベント登録処理
 */
document.addEventListener('DOMContentLoaded', () => {
  const page = new MailSendPage();
  page.init();
});
