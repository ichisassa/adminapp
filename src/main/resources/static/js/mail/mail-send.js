"use strict";
class MailSendPage {
    constructor() {
        this.form = null;
        this.sendButton = null;
        this.messageArea = null;
        this.messageBaseClass = '';
        this.endpoint = '/admin/api/mail/send';
        this.fields = {
            toAddress: null,
            replyTo: null,
            ccAddress: null,
            bccAddress: null,
            subject: null,
            body: null,
            isHtml: null,
        };
        this.fieldErrorAreas = {
            toAddress: null,
            replyTo: null,
            ccAddress: null,
            bccAddress: null,
            subject: null,
            body: null,
            isHtml: null,
        };
    }
    init() {
        this.cacheElements();
        this.registerEvents();
    }
    cacheElements() {
        this.form = document.getElementById('mailSendForm');
        this.sendButton = document.getElementById('btnSendMail');
        this.messageArea = document.getElementById('sendMessage');
        this.messageBaseClass = this.messageArea ? this.messageArea.className : '';
        this.fields = {
            toAddress: document.getElementById('toAddress'),
            replyTo: document.getElementById('toAddress'),
            ccAddress: document.getElementById('ccAddress'),
            bccAddress: document.getElementById('bccAddress'),
            subject: document.getElementById('subject'),
            body: document.getElementById('body'),
            isHtml: document.getElementById('isHtml'),
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
    registerEvents() {
        const handler = (event) => this.handleSendClick(event);
        if (this.form) {
            this.form.addEventListener('submit', handler);
        }
        if (this.sendButton) {
            this.sendButton.addEventListener('click', handler);
        }
    }
    handleSendClick(event) {
        event.preventDefault();
        void this.submitForm();
    }
    async submitForm() {
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
            let body = {};
            try {
                body = (await response.json());
            }
            catch {
                body = {};
            }
            const validatedFieldErrors = body.fieldErrors && typeof body.fieldErrors === 'object' ? body.fieldErrors : {};
            this.renderFieldErrors(validatedFieldErrors);
            const fallbackMessage = response.ok ? '送信しました。' : '送信に失敗しました。';
            const message = typeof body.message === 'string' ? body.message : fallbackMessage;
            if (response.ok && body.success) {
                this.showMessage(message, 'text-success');
                this.resetForm();
            }
            else {
                const composed = this.composeGlobalMessage(message, body.globalErrors);
                this.showMessage(composed, 'text-danger');
            }
        }
        catch {
            this.showMessage('通信エラーが発生しました。時間をおいて再度お試しください。', 'text-danger');
        }
        finally {
            this.toggleSending(false);
        }
    }
    buildPayload() {
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
            isHtml: !!(isHtml && isHtml.checked),
        };
    }
    buildFormBody(payload) {
        const params = new URLSearchParams();
        Object.entries(payload).forEach(([key, value]) => {
            if (typeof value === 'boolean') {
                params.append(key, value ? 'true' : 'false');
            }
            else if (value == null) {
                params.append(key, '');
            }
            else {
                params.append(key, value);
            }
        });
        return params.toString();
    }
    getFieldValue(element) {
        if (!element) {
            return '';
        }
        const value = element.value == null ? '' : element.value;
        return typeof value.trim === 'function' ? value.trim() : value;
    }
    toggleSending(isSending) {
        if (!this.sendButton) {
            return;
        }
        this.sendButton.disabled = isSending;
        this.sendButton.classList.toggle('disabled', isSending);
    }
    resetForm() {
        if (this.form) {
            this.form.reset();
        }
        this.clearFieldErrors();
    }
    showMessage(message, stateClass) {
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
    resetFeedback() {
        this.clearFieldErrors();
        this.showMessage('', '');
    }
    clearFieldErrors() {
        Object.values(this.fieldErrorAreas).forEach((area) => {
            if (area) {
                area.textContent = '';
            }
        });
    }
    renderFieldErrors(fieldErrors) {
        this.clearFieldErrors();
        if (!fieldErrors) {
            return;
        }
        Object.entries(fieldErrors).forEach(([field, message]) => {
            const target = this.fieldErrorAreas[field];
            if (target) {
                target.textContent = typeof message === 'string' ? message : '';
            }
        });
    }
    composeGlobalMessage(message, globalErrors) {
        const lines = [];
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
