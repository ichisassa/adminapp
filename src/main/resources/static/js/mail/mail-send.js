(function () {
  'use strict';

  /**
   * mail-send.js main class
   */
  function MailSendPage() {
    this.form = null;
    this.sendButton = null;
    this.messageArea = null;
    this.messageBaseClass = '';
    this.endpoint = '/api/admin/mail/send';
    this.fields = {};
  }

  MailSendPage.prototype.init = function init() {
    this.cacheElements();
    this.registerEvents();
  };

  MailSendPage.prototype.cacheElements = function cacheElements() {
    this.form = document.getElementById('mailSendForm');
    this.sendButton = document.getElementById('btnSendMail');
    this.messageArea = document.getElementById('sendMessage');
    this.messageBaseClass = this.messageArea && this.messageArea.className ? this.messageArea.className : '';
    this.fields = {
      toAddress: document.getElementById('toAddress'),
      ccAddress: document.getElementById('ccAddress'),
      bccAddress: document.getElementById('bccAddress'),
      subject: document.getElementById('subject'),
      body: document.getElementById('body'),
      isHtml: document.getElementById('isHtml')
    };
  };

  MailSendPage.prototype.registerEvents = function registerEvents() {
    var handler = this.handleSendClick.bind(this);
    if (this.form) {
      this.form.addEventListener('submit', handler);
    }
    if (this.sendButton) {
      this.sendButton.addEventListener('click', handler);
    }
  };

  MailSendPage.prototype.handleSendClick = function handleSendClick(event) {
    if (event) {
      event.preventDefault();
    }
    this.submitForm();
  };

  MailSendPage.prototype.submitForm = function submitForm() {
    var payload = this.buildPayload();
    if (!payload) {
      return;
    }

    var self = this;
    this.toggleSending(true);
    this.showMessage('送信処理中です...', 'text-info');

    var finalize = function finalize() {
      self.toggleSending(false);
    };

    fetch(this.endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      },
      credentials: 'same-origin',
      body: JSON.stringify(payload)
    })
      .then(function (response) {
        return response.json().catch(function () {
          return {};
        }).then(function (body) {
          return {
            ok: response.ok,
            body: body
          };
        });
      })
      .then(function (result) {
        var body = result.body || {};
        var message = body.message || (result.ok ? '送信しました。' : '送信に失敗しました。');
        if (result.ok && body.success) {
          self.showMessage(message, 'text-success');
          self.resetForm();
        } else {
          self.showMessage(message, 'text-danger');
        }
        finalize();
      })
      .catch(function () {
        self.showMessage('通信エラーが発生しました。時間をおいて再度お試しください。', 'text-danger');
        finalize();
      });
  };

  MailSendPage.prototype.buildPayload = function buildPayload() {
    var fields = this.fields;
    return {
      toAddress: toAddress,
      ccAddress: fields.ccAddress ? this.getFieldValue(fields.ccAddress) : '',
      bccAddress: fields.bccAddress ? this.getFieldValue(fields.bccAddress) : '',
      subject: fields.subject ? this.getFieldValue(fields.subject) : '',
      body: fields.body ? this.getFieldValue(fields.body) : '',
      isHtml: !!(fields.isHtml && fields.isHtml.checked)
    };
  };

  MailSendPage.prototype.getFieldValue = function getFieldValue(element) {
    if (!element) {
      return '';
    }
    var value = element.value || '';
    if (typeof value.trim === 'function') {
      return value.trim();
    }
    return value;
  };

  MailSendPage.prototype.toggleSending = function toggleSending(isSending) {
    if (!this.sendButton) {
      return;
    }
    this.sendButton.disabled = isSending;
    this.sendButton.classList.toggle('disabled', isSending);
  };

  MailSendPage.prototype.resetForm = function resetForm() {
    if (this.form) {
      this.form.reset();
    }
  };

  MailSendPage.prototype.showMessage = function showMessage(message, stateClass) {
    if (!this.messageArea) {
      return;
    }
    var className = this.messageBaseClass || '';
    if (stateClass) {
      className = className ? className + ' ' + stateClass : stateClass;
    }
    this.messageArea.className = className;
    this.messageArea.textContent = message;
  };

  document.addEventListener('DOMContentLoaded', function onReady() {
    var page = new MailSendPage();
    page.init();
  });
}());
