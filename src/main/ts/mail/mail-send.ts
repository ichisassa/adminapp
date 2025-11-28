type MailFieldKey = 'toAddress' | 'replyTo' | 'ccAddress' | 'bccAddress' | 'subject' | 'body' | 'isHtml';

interface MailSendPayload {
  toAddress: string;
  replyTo: string;
  ccAddress: string;
  bccAddress: string;
  subject: string;
  body: string;
  isHtml: boolean;
}

interface MailSendResponseBody {
  success?: boolean;
  message?: string;
  fieldErrors?: Record<string, string>;
  globalErrors?: string[];
}

class MailSendPage {
  private form: HTMLFormElement | null = null;
  private sendButton: HTMLButtonElement | null = null;
  private messageArea: HTMLElement | null = null;
  private messageBaseClass = '';
  private readonly endpoint = '/admin/api/mail/send';

  private fields: Record<MailFieldKey, HTMLInputElement | HTMLTextAreaElement | null> = {
    toAddress: null,
    replyTo: null,    
    ccAddress: null,
    bccAddress: null,
    subject: null,
    body: null,
    isHtml: null,
  };

  private fieldErrorAreas: Partial<Record<MailFieldKey, HTMLElement | null>> = {
    toAddress: null,
    replyTo: null,
    ccAddress: null,
    bccAddress: null,
    subject: null,
    body: null,
    isHtml: null,
  };

  init(): void {
    this.cacheElements();
    this.registerEvents();
  }

  private cacheElements(): void {
    this.form = document.getElementById('mailSendForm') as HTMLFormElement | null;
    this.sendButton = document.getElementById('btnSendMail') as HTMLButtonElement | null;
    this.messageArea = document.getElementById('sendMessage');
    this.messageBaseClass = this.messageArea ? this.messageArea.className : '';

    this.fields = {
      toAddress: document.getElementById('toAddress') as HTMLInputElement | null,
      replyTo: document.getElementById('toAddress') as HTMLInputElement | null,
      ccAddress: document.getElementById('ccAddress') as HTMLInputElement | null,
      bccAddress: document.getElementById('bccAddress') as HTMLInputElement | null,
      subject: document.getElementById('subject') as HTMLInputElement | null,
      body: document.getElementById('body') as HTMLTextAreaElement | null,
      isHtml: document.getElementById('isHtml') as HTMLInputElement | null,
    };

    this.fieldErrorAreas = {
      toAddress: document.getElementById('toAddressError'),
      replyTo: document.getElementById('replyToError'),
      ccAddress: document.getElementById('ccAddressError'),
      bccAddress: document.getElementById('bccAddressError'),
      subject: document.getElementById('subjectError'),
      body: document.getElementById('bodyError'),
      isHtml: document.getElementById('isHtmlError'),
    };
  }

  private registerEvents(): void {
    const handler = (event: Event) => this.handleSendClick(event);
    if (this.form) {
      this.form.addEventListener('submit', handler);
    }
    if (this.sendButton) {
      this.sendButton.addEventListener('click', handler);
    }
  }

  private handleSendClick(event: Event): void {
    event.preventDefault();
    void this.submitForm();
  }

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

  private buildPayload(): MailSendPayload | null {
    const { toAddress, replyTo, ccAddress, bccAddress, subject, body, isHtml } = this.fields;
    if (!toAddress || !subject || !body) {
      return null;
    }

    return {
      toAddress: this.getFieldValue(toAddress),
      replyTo: this.getFieldValue(toAddress),
      ccAddress: this.getFieldValue(ccAddress),
      bccAddress: this.getFieldValue(bccAddress),
      subject: this.getFieldValue(subject),
      body: this.getFieldValue(body),
      isHtml: !!(isHtml && (isHtml as HTMLInputElement).checked),
    };
  }

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

  private getFieldValue(element: HTMLInputElement | HTMLTextAreaElement | null): string {
    if (!element) {
      return '';
    }
    const value = element.value == null ? '' : element.value;
    return typeof value.trim === 'function' ? value.trim() : value;
  }

  private toggleSending(isSending: boolean): void {
    if (!this.sendButton) {
      return;
    }
    this.sendButton.disabled = isSending;
    this.sendButton.classList.toggle('disabled', isSending);
  }

  private resetForm(): void {
    if (this.form) {
      this.form.reset();
    }
    this.clearFieldErrors();
  }

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

  private resetFeedback(): void {
    this.clearFieldErrors();
    this.showMessage('', '');
  }

  private clearFieldErrors(): void {
    Object.values(this.fieldErrorAreas).forEach((area) => {
      if (area) {
        area.textContent = '';
      }
    });
  }

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

document.addEventListener('DOMContentLoaded', () => {
  const page = new MailSendPage();
  page.init();
});
