(function () {
  'use strict';

  function MailSendPage() {
    this.form = null;
    this.sendButton = null;
    this.messageArea = null;
    this.messageBaseClass = '';
    this.endpoint = '/admin/api/mail/send';
    this.fields = {};
    this.fieldErrorAreas = {};
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
    this.fieldErrorAreas = {
      toAddress: document.getElementById('toAddressError'),
      ccAddress: document.getElementById('ccAddressError'),
      bccAddress: document.getElementById('bccAddressError'),
      subject: document.getElementById('subjectError'),
      body: document.getElementById('bodyError'),
      isHtml: document.getElementById('isHtmlError')
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
    this.resetFeedback();
    this.toggleSending(true);
    this.showMessage('送信処理中です...', 'text-info');

    var finalize = function finalize() {
      self.toggleSending(false);
    };

    var formBody = this.buildFormBody(payload);

    fetch(this.endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
        'X-Requested-With': 'XMLHttpRequest'
      },
      credentials: 'same-origin',
      body: formBody
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
        self.renderFieldErrors(body.fieldErrors || {});
        var message = body.message || (result.ok ? '送信しました。' : '送信に失敗しました。');
        if (result.ok && body.success) {
          self.showMessage(message, 'text-success');
          self.resetForm();
        } else {
          var composed = self.composeGlobalMessage(message, body.globalErrors);
          self.showMessage(composed, 'text-danger');
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
      toAddress: fields.toAddress ? this.getFieldValue(fields.toAddress) : '',
      ccAddress: fields.ccAddress ? this.getFieldValue(fields.ccAddress) : '',
      bccAddress: fields.bccAddress ? this.getFieldValue(fields.bccAddress) : '',
      subject: fields.subject ? this.getFieldValue(fields.subject) : '',
      body: fields.body ? this.getFieldValue(fields.body) : '',
      isHtml: !!(fields.isHtml && fields.isHtml.checked)
    };
  };

  MailSendPage.prototype.buildFormBody = function buildFormBody(payload) {
    var params = new URLSearchParams();
    Object.keys(payload).forEach(function (key) {
      var value = payload[key];
      if (typeof value === 'boolean') {
        params.append(key, value ? 'true' : 'false');
      } else if (value === null || value === undefined) {
        params.append(key, '');
      } else {
        params.append(key, value);
      }
    });
    return params.toString();
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
    this.clearFieldErrors();
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

  MailSendPage.prototype.resetFeedback = function resetFeedback() {
    this.clearFieldErrors();
    this.showMessage('', '');
  };

  MailSendPage.prototype.clearFieldErrors = function clearFieldErrors() {
    var keys = Object.keys(this.fieldErrorAreas || {});
    for (var i = 0; i < keys.length; i += 1) {
      var area = this.fieldErrorAreas[keys[i]];
      if (area) {
        area.textContent = '';
      }
    }
  };

  MailSendPage.prototype.renderFieldErrors = function renderFieldErrors(fieldErrors) {
    this.clearFieldErrors();
    if (!fieldErrors) {
      return;
    }
    var keys = Object.keys(fieldErrors);
    for (var i = 0; i < keys.length; i += 1) {
      var field = keys[i];
      var message = fieldErrors[field];
      var target = this.fieldErrorAreas[field];
      if (!target) {
        continue;
      }
      target.textContent = message || '';
    }
  };

  MailSendPage.prototype.composeGlobalMessage = function composeGlobalMessage(message, globalErrors) {
    var lines = [];
    if (message) {
      lines.push(message);
    }
    if (Array.isArray(globalErrors) && globalErrors.length > 0) {
      for (var i = 0; i < globalErrors.length; i += 1) {
        if (globalErrors[i]) {
          lines.push(globalErrors[i]);
        }
      }
    }
    return lines.join('\n');
  };

  document.addEventListener('DOMContentLoaded', function onReady() {
    var page = new MailSendPage();
    page.init();
  });
}());
